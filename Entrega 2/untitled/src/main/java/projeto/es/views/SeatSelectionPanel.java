package projeto.es.views;

import projeto.es.models.Session;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionPanel extends JPanel {
    private Session session;
    private List<Point> selectedSeats;
    private JToggleButton[][] seatButtons;
    private JPanel seatsGrid;
    private JLabel screenLabel;
    private JLabel selectionLabel;
    private static final Color AVAILABLE_COLOR = new Color(200, 255, 200);
    private static final Color OCCUPIED_COLOR = new Color(255, 200, 200);
    private static final Color SELECTED_COLOR = new Color(200, 200, 255);
    private static final Color ACCESSIBILITY_COLOR = new Color(255, 255, 200);
    private Runnable onSelectionChanged;

    public SeatSelectionPanel(Session session, Runnable onSelectionChanged) {
        this.session = session;
        this.selectedSeats = new ArrayList<>();
        this.onSelectionChanged = onSelectionChanged;
        setLayout(new BorderLayout(10, 10));
        
        // Create screen label
        screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setPreferredSize(new Dimension(0, 30));
        screenLabel.setBackground(Color.LIGHT_GRAY);
        screenLabel.setOpaque(true);
        add(screenLabel, BorderLayout.NORTH);
        
        // Create selection info label
        selectionLabel = new JLabel("Selected seats: 0", SwingConstants.CENTER);
        selectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Create seats grid
        createSeatsGrid();
        
        // Create legend and info panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(selectionLabel, BorderLayout.NORTH);
        bottomPanel.add(createLegend(), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createSeatsGrid() {
        int rows = session.getRoom().getRows();
        int cols = session.getRoom().getColumns();
        
        seatsGrid = new JPanel(new GridLayout(rows, cols, 5, 5));
        seatsGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        seatButtons = new JToggleButton[rows][cols];
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JToggleButton seatButton = new JToggleButton();
                seatButton.setPreferredSize(new Dimension(40, 40));
                
                // Set initial color based on seat status
                updateSeatButtonColor(seatButton, row, col);
                
                // Add action listener
                final int finalRow = row;
                final int finalCol = col;
                seatButton.addActionListener(e -> handleSeatClick(finalRow, finalCol));
                
                seatButtons[row][col] = seatButton;
                seatsGrid.add(seatButton);
            }
        }
        
        // Center the grid
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(seatsGrid);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legend.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        legend.add(createLegendItem("Available", AVAILABLE_COLOR));
        legend.add(createLegendItem("Selected", SELECTED_COLOR));
        legend.add(createLegendItem("Occupied", OCCUPIED_COLOR));
        legend.add(createLegendItem("Accessibility", ACCESSIBILITY_COLOR));
        
        return legend;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        item.add(colorBox);
        item.add(new JLabel(text));
        return item;
    }

    private void handleSeatClick(int row, int col) {
        if (session.isSeatOccupied(row, col)) {
            seatButtons[row][col].setSelected(false);
            return;
        }

        Point seat = new Point(row, col);
        if (seatButtons[row][col].isSelected()) {
            selectedSeats.add(seat);
        } else {
            selectedSeats.remove(seat);
        }
        updateSeatButtonColor(seatButtons[row][col], row, col);
        selectionLabel.setText("Selected seats: " + selectedSeats.size());
        
        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    private void updateSeatButtonColor(JToggleButton button, int row, int col) {
        if (session.isSeatOccupied(row, col)) {
            button.setBackground(OCCUPIED_COLOR);
            button.setEnabled(false);
        } else if (button.isSelected()) {
            button.setBackground(SELECTED_COLOR);
        } else if (session.isAccessibilitySeat(row, col)) {
            button.setBackground(ACCESSIBILITY_COLOR);
        } else {
            button.setBackground(AVAILABLE_COLOR);
        }
        button.setText(String.format("%c%d", (char)('A' + row), col + 1));
    }

    public List<Point> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    public void clearSelection() {
        selectedSeats.clear();
        for (int row = 0; row < seatButtons.length; row++) {
            for (int col = 0; col < seatButtons[row].length; col++) {
                seatButtons[row][col].setSelected(false);
                updateSeatButtonColor(seatButtons[row][col], row, col);
            }
        }
    }
} 