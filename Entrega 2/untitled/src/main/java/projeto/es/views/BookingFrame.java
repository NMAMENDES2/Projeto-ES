package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.models.User;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingFrame extends JFrame {
    private final User user;
    private final Movie movie;
    private JComboBox<Session> sessionComboBox;
    private SeatSelectionPanel seatSelectionPanel;
    private JLabel priceLabel;
    private Session currentSession;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public BookingFrame(User user, Movie movie) {
        super("Book Tickets - " + movie.getName());
        this.user = user;
        this.movie = movie;
        
        setLayout(new BorderLayout(10, 10));
        
        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Top panel with movie details and session selection
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Movie details
        JPanel movieDetailsPanel = createMovieDetailsPanel();
        topPanel.add(movieDetailsPanel, BorderLayout.CENTER);
        
        // Session selection
        JPanel sessionPanel = createSessionPanel();
        topPanel.add(sessionPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create a scroll pane for the main content
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
        
        // Price display and payment button in a separate panel outside scroll pane
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        priceLabel = new JLabel("Total: $0.00");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setFont(new Font(priceLabel.getFont().getName(), Font.BOLD, 16));
        bottomPanel.add(priceLabel, BorderLayout.CENTER);
        
        JButton confirmButton = new JButton("Proceed to Payment");
        confirmButton.addActionListener(e -> handleBooking());
        bottomPanel.add(confirmButton, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initial setup
        List<Session> sessions = getAvailableSessions();
        if (!sessions.isEmpty()) {
            currentSession = sessions.get(0);
            updateSeatSelection();
        }
        
        // Set frame size based on screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new Dimension(
            (int)(screenSize.width * 0.7),
            (int)(screenSize.height * 0.8)
        ));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private List<Session> getAvailableSessions() {
        LocalDateTime now = LocalDateTime.now();
        return SessionDatabase.getSessionsForMovie(movie.getId())
            .stream()
            .filter(s -> s.getStartTime().isAfter(now))
            .sorted((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()))
            .collect(Collectors.toList());
    }
    
    private JPanel createMovieDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Movie Details"));
        
        panel.add(new JLabel("Title: " + movie.getName()));
        panel.add(new JLabel("Category: " + movie.getCategory()));
        panel.add(new JLabel("Director: " + movie.getDirector()));
        panel.add(new JLabel("Duration: " + movie.getDuration() + " minutes"));
        
        return panel;
    }
    
    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Session"));
        
        List<Session> availableSessions = getAvailableSessions();
        
        if (availableSessions.isEmpty()) {
            panel.add(new JLabel("No upcoming sessions available"));
            sessionComboBox = new JComboBox<>();
            sessionComboBox.setEnabled(false);
        } else {
            sessionComboBox = new JComboBox<>(availableSessions.toArray(new Session[0]));
            sessionComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Session) {
                        Session session = (Session) value;
                        String text = String.format("%s - Room: %s - $%.2f", 
                            session.getStartTime().format(DATE_FORMATTER),
                            session.getRoom().getName(),
                            session.getPrice());
                        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
            
            sessionComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Session selectedSession = (Session) sessionComboBox.getSelectedItem();
                    if (selectedSession != null && selectedSession.getStartTime().isAfter(LocalDateTime.now())) {
                        currentSession = selectedSession;
                        updateSeatSelection();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "This session is no longer available.",
                            "Session Unavailable",
                            JOptionPane.ERROR_MESSAGE);
                        sessionComboBox.setSelectedItem(currentSession);
                    }
                }
            });
        }
        
        panel.add(sessionComboBox);
        return panel;
    }
    
    private void updateSeatSelection() {
        if (currentSession != null) {
            if (seatSelectionPanel != null) {
                ((JPanel)((JScrollPane)getContentPane().getComponent(0)).getViewport().getView()).remove(seatSelectionPanel);
            }
            seatSelectionPanel = new SeatSelectionPanel(currentSession, this::updatePrice);
            
            // Add the seat selection panel to the main panel
            ((JPanel)((JScrollPane)getContentPane().getComponent(0)).getViewport().getView()).add(seatSelectionPanel, BorderLayout.CENTER);
            
            revalidate();
            repaint();
            updatePrice();
        }
    }
    
    private void updatePrice() {
        if (currentSession != null && seatSelectionPanel != null) {
            int selectedCount = seatSelectionPanel.getSelectedSeats().size();
            double totalPrice = selectedCount * currentSession.getPrice();
            priceLabel.setText(String.format("Total: $%.2f", totalPrice));
        }
    }
    
    private void handleBooking() {
        if (currentSession == null) {
            JOptionPane.showMessageDialog(this, "Please select a session");
            return;
        }
        
        if (currentSession.getStartTime().isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                "This session is no longer available.",
                "Session Unavailable",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Point> selectedSeats = seatSelectionPanel.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat");
            return;
        }
        
        // Open payment frame
        PaymentFrame paymentFrame = new PaymentFrame(user, currentSession, selectedSeats);
        paymentFrame.setVisible(true);
        dispose(); // Close booking frame
    }
} 