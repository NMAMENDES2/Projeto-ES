package projeto.es.views;

import projeto.es.models.Session;
import javax.swing.*;
import javax.swing.border.Border;
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
    private static final Color AVAILABLE_COLOR = new Color(144, 238, 144); // Light green
    private static final Color OCCUPIED_COLOR = new Color(255, 99, 71);    // Tomato red
    private static final Color SELECTED_COLOR = new Color(135, 206, 250);  // Light sky blue
    private static final Color ACCESSIBILITY_COLOR = new Color(255, 223, 186); // Peach
    private Runnable onSelectionChanged;

    public SeatSelectionPanel(Session session, Runnable onSelectionChanged) {
        this.session = session;
        this.selectedSeats = new ArrayList<>();
        this.onSelectionChanged = onSelectionChanged;
        setLayout(new BorderLayout(10, 10));
        
        // Create screen label
        screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setPreferredSize(new Dimension(0, 30));
        screenLabel.setBackground(new Color(200, 200, 200));
        screenLabel.setOpaque(true);
        screenLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        add(screenLabel, BorderLayout.NORTH);
        
        // Create selection info label
        selectionLabel = new JLabel("Selected seats: 0", SwingConstants.CENTER);
        selectionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        selectionLabel.setFont(selectionLabel.getFont().deriveFont(Font.BOLD));
        
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
        seatsGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        seatButtons = new JToggleButton[rows][cols];
        
        // Calculate button size based on room dimensions
        // Make buttons slightly smaller but maintain minimum size
        int buttonSize = Math.max(25, Math.min(35, Math.min(600 / cols, 400 / rows)));
        Dimension buttonDimension = new Dimension(buttonSize, buttonSize);
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JToggleButton seatButton = createSeatButton(row, col);
                seatButton.setPreferredSize(buttonDimension);
                seatButtons[row][col] = seatButton;
                seatsGrid.add(seatButton);
            }
        }
        
        // Create a scroll pane for the grid
        JScrollPane scrollPane = new JScrollPane(seatsGrid);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        // Set preferred size for scroll pane - make it smaller
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxHeight = (int)(screenSize.height * 0.5); // 50% of screen height
        int maxWidth = (int)(screenSize.width * 0.6);   // 60% of screen width
        
        // Calculate total grid size including spacing and borders
        int totalWidth = (buttonSize + 5) * cols + 40;  // Add padding
        int totalHeight = (buttonSize + 5) * rows + 40; // Add padding
        
        // Set a fixed size that's smaller but maintains aspect ratio
        double aspectRatio = (double)totalWidth / totalHeight;
        int preferredHeight = Math.min(maxHeight, 400); // Limit height to 400 pixels
        int preferredWidth = (int)(preferredHeight * aspectRatio);
        
        if (preferredWidth > maxWidth) {
            preferredWidth = maxWidth;
            preferredHeight = (int)(preferredWidth / aspectRatio);
        }
        
        scrollPane.setPreferredSize(new Dimension(
            Math.min(maxWidth, preferredWidth),
            Math.min(maxHeight, preferredHeight)
        ));
        
        // Center the scroll pane
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(scrollPane);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JToggleButton createSeatButton(int row, int col) {
        JToggleButton seatButton = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw text
                g2.setColor(Color.BLACK);
                g2.setFont(getFont());
                String text = getText();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
        };
        
        // Override default button UI
        seatButton.setContentAreaFilled(false);
        seatButton.setFocusPainted(false);
        seatButton.setBorderPainted(true);
        
        // Set initial appearance
        updateSeatButtonAppearance(seatButton, row, col);
        
        // Add action listener
        seatButton.addActionListener(e -> handleSeatClick(row, col));
        
        // Add tooltip
        updateSeatTooltip(seatButton, row, col);
        
        return seatButton;
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
        JPanel colorBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        colorBox.setPreferredSize(new Dimension(25, 25));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
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
        updateSeatButtonAppearance(seatButtons[row][col], row, col);
        selectionLabel.setText("Selected seats: " + selectedSeats.size());
        
        if (onSelectionChanged != null) {
            onSelectionChanged.run();
        }
    }

    private void updateSeatButtonAppearance(JToggleButton button, int row, int col) {
        // Set the seat label (e.g., "A1", "B2", etc.)
        button.setText(String.format("%c%d", (char)('A' + row), col + 1));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12));
        
        // Set colors and borders based on seat status
        if (session.isSeatOccupied(row, col)) {
            button.setBackground(OCCUPIED_COLOR);
            button.setEnabled(false);
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        } else if (button.isSelected()) {
            button.setBackground(SELECTED_COLOR);
            button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        } else if (session.isAccessibilitySeat(row, col)) {
            button.setBackground(ACCESSIBILITY_COLOR);
            button.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        } else {
            button.setBackground(AVAILABLE_COLOR);
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        }
        
        // Force the button to use our custom colors
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.repaint();
        
        // Update tooltip
        updateSeatTooltip(button, row, col);
    }

    private void updateSeatTooltip(JToggleButton button, int row, int col) {
        String status;
        if (session.isSeatOccupied(row, col)) {
            status = "Occupied";
        } else if (button.isSelected()) {
            status = "Selected";
        } else if (session.isAccessibilitySeat(row, col)) {
            status = "Accessibility Seat";
        } else {
            status = "Available";
        }
        button.setToolTipText(String.format("Seat %c%d - %s", (char)('A' + row), col + 1, status));
    }

    public List<Point> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    public void clearSelection() {
        selectedSeats.clear();
        for (int row = 0; row < seatButtons.length; row++) {
            for (int col = 0; col < seatButtons[row].length; col++) {
                seatButtons[row][col].setSelected(false);
                updateSeatButtonAppearance(seatButtons[row][col], row, col);
            }
        }
    }
} 