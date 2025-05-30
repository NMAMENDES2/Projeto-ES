package projeto.es.views;

import projeto.es.Repository.MovieDatabase;
import projeto.es.models.Movie;
import projeto.es.models.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovieCatalogFrame extends JFrame {
    private final User currentUser;
    private JPanel moviesPanel;

    public MovieCatalogFrame(User user) {
        super("Movie Catalog");
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        
        // Create welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(new JLabel("Welcome, " + user.getUsername() + "!"));
        add(welcomePanel, BorderLayout.NORTH);
        
        // Create scrollable movies panel
        moviesPanel = new JPanel();
        moviesPanel.setLayout(new GridLayout(0, 3, 10, 10)); // 3 columns, dynamic rows
        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        loadMovies();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadMovies() {
        List<Movie> movies = MovieDatabase.getAllMovies();
        moviesPanel.removeAll();
        
        for (Movie movie : movies) {
            JPanel movieCard = createMovieCard(movie);
            moviesPanel.add(movieCard);
        }
        
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }
    
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Add movie details
        JLabel titleLabel = new JLabel(movie.getName());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        
        JLabel categoryLabel = new JLabel(movie.getCategory());
        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(categoryLabel);
        
        JLabel directorLabel = new JLabel("Director: " + movie.getDirector());
        directorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(directorLabel);
        
        JLabel languageLabel = new JLabel("Language: " + movie.getLanguage());
        languageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(languageLabel);
        
        JLabel durationLabel = new JLabel(movie.getDuration() + " minutes");
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(durationLabel);
        
        JLabel releaseDateLabel = new JLabel("Release: " + movie.getReleaseDate());
        releaseDateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(releaseDateLabel);
        
        // Add book button
        JButton bookButton = new JButton("Book Ticket");
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.addActionListener(e -> openBookingWindow(movie));
        card.add(bookButton);
        
        return card;
    }
    
    private void openBookingWindow(Movie movie) {
        new BookingFrame(currentUser, movie).setVisible(true);
    }
} 