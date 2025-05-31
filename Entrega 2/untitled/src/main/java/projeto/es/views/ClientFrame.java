package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.models.User;
import projeto.es.models.Ticket;
import projeto.es.models.Session;
import projeto.es.Repository.MovieDatabase;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class ClientFrame extends JFrame {
    private final User user;
    private JPanel moviePanel;
    private JPanel ticketPanel;
    private JTabbedPane tabbedPane;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClientFrame(User user) {
        super("Movie Theater - Welcome " + user.getUsername());
        System.out.println("DEBUG: Initializing ClientFrame for user: " + user.getUsername());
        this.user = user;
        
        try {
            setLayout(new BorderLayout(10, 10));
            System.out.println("DEBUG: Layout set");
            
            // Create tabbed pane
            tabbedPane = new JTabbedPane();
            System.out.println("DEBUG: Created tabbed pane");
            
            // Create movie catalog panel
            moviePanel = new JPanel(new GridBagLayout());
            System.out.println("DEBUG: Created movie panel");
            refreshMovieCatalog();
            System.out.println("DEBUG: Refreshed movie catalog");
            JScrollPane movieScrollPane = new JScrollPane(moviePanel);
            movieScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            tabbedPane.addTab("Movies", movieScrollPane);
            
            // Create tickets panel
            ticketPanel = new JPanel(new BorderLayout(10, 10));
            System.out.println("DEBUG: Created ticket panel");
            refreshTicketPanel();
            System.out.println("DEBUG: Refreshed ticket panel");
            tabbedPane.addTab("My Tickets", ticketPanel);
            
            // Add refresh button for tickets
            JButton refreshButton = new JButton("Refresh Tickets");
            refreshButton.addActionListener(e -> refreshTicketPanel());
            ticketPanel.add(refreshButton, BorderLayout.NORTH);
            
            add(tabbedPane, BorderLayout.CENTER);
            System.out.println("DEBUG: Added tabbed pane to frame");
            
            // Add menu bar
            JMenuBar menuBar = createMenuBar();
            setJMenuBar(menuBar);
            System.out.println("DEBUG: Added menu bar");
            
            setSize(1000, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.out.println("DEBUG: ClientFrame initialization complete");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to initialize ClientFrame");
            e.printStackTrace();
            throw e; // Re-throw to be caught by the login frame
        }
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        fileMenu.add(logoutItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.addActionListener(e -> {
            refreshMovieCatalog();
            refreshTicketPanel();
        });
        viewMenu.add(refreshItem);

        // Store menu
        JMenu storeMenu = new JMenu("Store");
        JMenuItem openStoreItem = new JMenuItem("Open Bar");
        openStoreItem.addActionListener(e -> {
            BarFrame barFrame = new BarFrame(user);
            barFrame.setVisible(true);
        });
        storeMenu.add(openStoreItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(storeMenu);
        
        return menuBar;
    }
    
    private void refreshMovieCatalog() {
        moviePanel.removeAll();
        List<Movie> movies = MovieDatabase.getAllMovies();
        
        if (movies.isEmpty()) {
            JLabel noMoviesLabel = new JLabel("No movies available", SwingConstants.CENTER);
            moviePanel.add(noMoviesLabel);
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            int row = 0;
            int col = 0;
            int maxCols = 3;
            
            for (Movie movie : movies) {
                JPanel movieCard = createMovieCard(movie);
                
                gbc.gridx = col;
                gbc.gridy = row;
                
                moviePanel.add(movieCard, gbc);
                
                col++;
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
            }
        }
        
        moviePanel.revalidate();
        moviePanel.repaint();
    }
    
    private void refreshTicketPanel() {
        ticketPanel.removeAll();
        
        // Create ticket list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        LocalDateTime now = LocalDateTime.now();
        // Create a mutable copy of the tickets list
        List<Ticket> tickets = new ArrayList<>(user.getTickets());
        
        if (tickets.isEmpty()) {
            listPanel.add(new JLabel("No tickets found"));
        } else {
            // Sort tickets: upcoming first, then past
            tickets.sort((t1, t2) -> {
                boolean t1IsPast = t1.getSession().getStartTime().isBefore(now);
                boolean t2IsPast = t2.getSession().getStartTime().isBefore(now);
                if (t1IsPast != t2IsPast) {
                    return t1IsPast ? 1 : -1;
                }
                return t1.getSession().getStartTime().compareTo(t2.getSession().getStartTime());
            });
            
            // Add "Upcoming Tickets" section
            if (tickets.stream().anyMatch(t -> t.getSession().getStartTime().isAfter(now))) {
                JLabel upcomingLabel = new JLabel("Upcoming Tickets");
                upcomingLabel.setFont(upcomingLabel.getFont().deriveFont(Font.BOLD, 14));
                listPanel.add(upcomingLabel);
                listPanel.add(Box.createVerticalStrut(10));
                
                for (Ticket ticket : tickets) {
                    if (ticket.getSession().getStartTime().isAfter(now)) {
                        listPanel.add(createTicketPanel(ticket, now));
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
            }
            
            // Add "Past Tickets" section
            if (tickets.stream().anyMatch(t -> t.getSession().getStartTime().isBefore(now))) {
                listPanel.add(Box.createVerticalStrut(20));
                JLabel pastLabel = new JLabel("Past Tickets");
                pastLabel.setFont(pastLabel.getFont().deriveFont(Font.BOLD, 14));
                listPanel.add(pastLabel);
                listPanel.add(Box.createVerticalStrut(10));
                
                for (Ticket ticket : tickets) {
                    if (ticket.getSession().getStartTime().isBefore(now)) {
                        listPanel.add(createTicketPanel(ticket, now));
                        listPanel.add(Box.createVerticalStrut(10));
                    }
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ticketPanel.add(scrollPane, BorderLayout.CENTER);
        
        ticketPanel.revalidate();
        ticketPanel.repaint();
    }
    
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(280, 220));
        
        // Movie title
        JLabel titleLabel = new JLabel(movie.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16));
        card.add(titleLabel, BorderLayout.NORTH);
        
        // Movie details
        JPanel detailsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        detailsPanel.add(new JLabel("Category: " + movie.getCategory()));
        detailsPanel.add(new JLabel("Director: " + movie.getDirector()));
        detailsPanel.add(new JLabel("Duration: " + movie.getDuration() + " minutes"));
        detailsPanel.add(new JLabel("Language: " + movie.getLanguage()));
        
        // Add available sessions count
        List<Session> sessions = SessionDatabase.getSessionsForMovie(movie.getId());
        long upcomingSessions = sessions.stream()
            .filter(s -> s.getStartTime().isAfter(LocalDateTime.now()))
            .count();
        detailsPanel.add(new JLabel("Available Sessions: " + upcomingSessions));
        
        card.add(detailsPanel, BorderLayout.CENTER);
        
        // Book button
        JButton bookButton = new JButton("Book Tickets");
        bookButton.setEnabled(upcomingSessions > 0);
        bookButton.addActionListener(e -> openBookingFrame(movie));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(bookButton);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createTicketPanel(Ticket ticket, LocalDateTime now) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        boolean isUpcoming = ticket.getSession().getStartTime().isAfter(now);
        
        // Set border color based on ticket status
        Color borderColor = isUpcoming ? new Color(46, 125, 50) : Color.GRAY;
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 2, 2));
        Session session = ticket.getSession();
        Movie movie = session.getMovie();
        
        // Add movie and session details
        infoPanel.add(createInfoLabel("Movie:", movie.getName(), Font.BOLD));
        infoPanel.add(createInfoLabel("Time:", session.getStartTime().format(DATE_FORMATTER)));
        infoPanel.add(createInfoLabel("Room:", session.getRoom().getName()));
        infoPanel.add(createInfoLabel("Seat:", String.format("%c%d", 
            (char)('A' + ticket.getRow()), ticket.getColumn() + 1)));
        infoPanel.add(createInfoLabel("Price:", String.format("$%.2f", session.getPrice())));
        
        // Add status label
        JLabel statusLabel = new JLabel(isUpcoming ? "Status: Upcoming" : "Status: Completed");
        statusLabel.setForeground(isUpcoming ? new Color(46, 125, 50) : Color.GRAY);
        infoPanel.add(statusLabel);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Add refund button if the session hasn't started yet
        if (isUpcoming) {
            JButton refundButton = new JButton("Request Refund");
            refundButton.addActionListener(e -> handleRefund(ticket));
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(refundButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        return panel;
    }
    
    private JLabel createInfoLabel(String label, String value) {
        return createInfoLabel(label, value, Font.PLAIN);
    }
    
    private JLabel createInfoLabel(String label, String value, int style) {
        JLabel infoLabel = new JLabel(label + " " + value);
        infoLabel.setFont(infoLabel.getFont().deriveFont(style));
        return infoLabel;
    }
    
    private void handleRefund(Ticket ticket) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Are you sure you want to refund this ticket?\n\nMovie: %s\nTime: %s\nSeat: %c%d\nRefund Amount: $%.2f",
                ticket.getSession().getMovie().getName(),
                ticket.getSession().getStartTime().format(DATE_FORMATTER),
                (char)('A' + ticket.getRow()),
                ticket.getColumn() + 1,
                ticket.getSession().getPrice()),
            "Confirm Refund",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Free up the seat
            ticket.getSession().freeSeat(ticket.getRow(), ticket.getColumn());
            
            // Remove ticket from database
            user.removeTicket(ticket);
            
            // Save the updated session
            SessionDatabase.saveSession(ticket.getSession());
            
            // Show confirmation
            JOptionPane.showMessageDialog(this,
                String.format("Refund processed successfully!\nAmount: $%.2f", 
                    ticket.getSession().getPrice()),
                "Refund Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the ticket panel
            refreshTicketPanel();
        }
    }
    
    private void openBookingFrame(Movie movie) {
        BookingFrame bookingFrame = new BookingFrame(user, movie);
        bookingFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshTicketPanel();
            }
        });
        bookingFrame.setVisible(true);
    }
} 