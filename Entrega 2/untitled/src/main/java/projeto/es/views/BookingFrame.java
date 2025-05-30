package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.models.User;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BookingFrame extends JFrame {
    private final User user;
    private final Movie movie;
    private JComboBox<Session> sessionComboBox;
    private JPanel seatsPanel;
    private final boolean[][] selectedSeats = new boolean[6][8]; // 6 rows, 8 seats per row
    private final List<JToggleButton> seatButtons = new ArrayList<>();
    
    public BookingFrame(User user, Movie movie) {
        super("Book Tickets - " + movie.getName());
        this.user = user;
        this.movie = movie;
        
        setLayout(new BorderLayout(10, 10));
        
        // Movie details panel
        JPanel movieDetailsPanel = createMovieDetailsPanel();
        add(movieDetailsPanel, BorderLayout.NORTH);
        
        // Session selection
        JPanel sessionPanel = createSessionPanel();
        add(sessionPanel, BorderLayout.CENTER);
        
        // Seats selection
        JPanel seatsContainer = createSeatsPanel();
        add(seatsContainer, BorderLayout.SOUTH);
        
        // Confirm booking button
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(e -> handleBooking());
        add(confirmButton, BorderLayout.SOUTH);
        
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private JPanel createMovieDetailsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Movie Details"));
        
        panel.add(new JLabel("Title: " + movie.getName()));
        panel.add(new JLabel("Category: " + movie.getCategory()));
        panel.add(new JLabel("Director: " + movie.getDirector()));
        panel.add(new JLabel("Duration: " + movie.getDuration() + " minutes"));
        
        return panel;
    }
    
    private JPanel createSessionPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Select Session"));
        
        List<Session> sessions = SessionDatabase.getSessionsForMovie(movie.getId());
        sessionComboBox = new JComboBox<>(sessions.toArray(new Session[0]));
        panel.add(sessionComboBox);
        
        return panel;
    }
    
    private JPanel createSeatsPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder("Select Seats"));
        
        seatsPanel = new JPanel(new GridLayout(6, 8, 5, 5));
        
        // Create seat buttons
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                JToggleButton seatButton = new JToggleButton(String.format("%c%d", (char)('A' + row), col + 1));
                seatButton.setPreferredSize(new Dimension(50, 50));
                final int finalRow = row;
                final int finalCol = col;
                seatButton.addActionListener(e -> toggleSeat(finalRow, finalCol));
                seatButtons.add(seatButton);
                seatsPanel.add(seatButton);
            }
        }
        
        // Add screen label
        JLabel screenLabel = new JLabel("SCREEN", SwingConstants.CENTER);
        screenLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        container.add(screenLabel, BorderLayout.NORTH);
        container.add(seatsPanel, BorderLayout.CENTER);
        
        return container;
    }
    
    private void toggleSeat(int row, int col) {
        selectedSeats[row][col] = !selectedSeats[row][col];
    }
    
    private void handleBooking() {
        Session selectedSession = (Session) sessionComboBox.getSelectedItem();
        if (selectedSession == null) {
            JOptionPane.showMessageDialog(this, "Please select a session");
            return;
        }
        
        List<Point> selectedSeatsList = new ArrayList<>();
        for (int row = 0; row < selectedSeats.length; row++) {
            for (int col = 0; col < selectedSeats[row].length; col++) {
                if (selectedSeats[row][col]) {
                    selectedSeatsList.add(new Point(row, col));
                }
            }
        }
        
        if (selectedSeatsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one seat");
            return;
        }
        
        // Create tickets for each selected seat
        for (Point seat : selectedSeatsList) {
            Ticket ticket = new Ticket(selectedSession, seat.x, seat.y, user);
            user.addTicket(ticket);
        }
        
        JOptionPane.showMessageDialog(this, "Booking successful!");
        dispose();
    }
} 