package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.Repository.MovieDatabase;
import projeto.es.Repository.RoomDatabase;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel moviesPanel;
    private JPanel roomsPanel;
    private JPanel sessionsPanel;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminFrame() {
        super("Movie Theater Administration");
        
        setLayout(new BorderLayout());
        
        // Create tabbed pane
        createTabbedPane();
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        
        // Movies tab
        JPanel moviesPanel = new MovieManagementPanel();
        tabbedPane.addTab("Movies", moviesPanel);
        
        // Rooms tab
        JPanel roomsPanel = new RoomManagementPanel();
        tabbedPane.addTab("Rooms", roomsPanel);
        
        // Sessions tab
        JPanel sessionsPanel = new SessionManagementPanel();
        tabbedPane.addTab("Sessions", sessionsPanel);

        // Bar Products tab
        JPanel barPanel = new ProductManagementPanel();
        tabbedPane.addTab("Bar Products", barPanel);

        // Order History tab
        JPanel orderHistoryPanel = new OrderHistoryPanel();
        tabbedPane.addTab("Order History", orderHistoryPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void refreshMovieList(DefaultListModel<Movie> model) {
        model.clear();
        List<Movie> movies = MovieDatabase.getAllMovies();
        for (Movie movie : movies) {
            model.addElement(movie);
        }
    }
    
    private void refreshRoomList(DefaultListModel<Room> model) {
        model.clear();
        List<Room> rooms = RoomDatabase.getAllRooms();
        for (Room room : rooms) {
            model.addElement(room);
        }
    }
    
    private void refreshSessionList(DefaultListModel<Session> model) {
        model.clear();
        List<Session> sessions = SessionDatabase.getAllSessions();
        for (Session session : sessions) {
            model.addElement(session);
        }
    }
    
    private void addMovie() {
        // TODO: Implement movie addition dialog
        JOptionPane.showMessageDialog(this, "Add movie functionality coming soon!");
    }
    
    private void editMovie(Movie movie) {
        // TODO: Implement movie editing dialog
        JOptionPane.showMessageDialog(this, "Edit movie functionality coming soon!");
    }
    
    private void deleteMovie(Movie movie) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + movie.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            MovieDatabase.deleteMovie(movie.getId());
            refreshMovieList((DefaultListModel<Movie>) ((JList<?>) ((JScrollPane) moviesPanel.getComponent(0)).getViewport().getView()).getModel());
        }
    }
    
    private void addRoom() {
        // TODO: Implement room addition dialog
        JOptionPane.showMessageDialog(this, "Add room functionality coming soon!");
    }
    
    private void editRoom(Room room) {
        // TODO: Implement room editing dialog
        JOptionPane.showMessageDialog(this, "Edit room functionality coming soon!");
    }
    
    private void deleteRoom(Room room) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + room.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            RoomDatabase.deleteRoom(room.getId());
            refreshRoomList((DefaultListModel<Room>) ((JList<?>) ((JScrollPane) roomsPanel.getComponent(0)).getViewport().getView()).getModel());
        }
    }
    
    private void addSession() {
        // TODO: Implement session addition dialog
        JOptionPane.showMessageDialog(this, "Add session functionality coming soon!");
    }
    
    private void editSession(Session session) {
        // TODO: Implement session editing dialog
        JOptionPane.showMessageDialog(this, "Edit session functionality coming soon!");
    }
    
    private void deleteSession(Session session) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this session?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            SessionDatabase.deleteSession(session.getId());
            refreshSessionList((DefaultListModel<Session>) ((JList<?>) ((JScrollPane) sessionsPanel.getComponent(0)).getViewport().getView()).getModel());
        }
    }
} 