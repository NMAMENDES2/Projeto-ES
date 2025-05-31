package projeto.es.views;
import projeto.es.views.*;

import projeto.es.Repository.MovieDatabase;
import projeto.es.Repository.RoomDatabase;
import projeto.es.Repository.SessionDatabase;
import projeto.es.models.Movie;
import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.models.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private final User admin;
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> movieListModel;
    private DefaultListModel<String> roomListModel;
    private DefaultListModel<String> sessionListModel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminDashboardFrame(User admin) {
        super("Admin Dashboard - " + admin.getUsername());
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("User must be an admin");
        }
        this.admin = admin;

        setLayout(new BorderLayout());
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Movies", createMoviesPanel());
        tabbedPane.addTab("Rooms", createRoomsPanel());
        tabbedPane.addTab("Sessions", createSessionsPanel());
        tabbedPane.addTab("Bar Products", new ProductManagementPanel());
        tabbedPane.addTab("Order History", new OrderHistoryPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
            refreshMovieList();
            refreshRoomList();
            refreshSessionList();
            // Refresh bar products and order history
            ((ProductManagementPanel) tabbedPane.getComponentAt(3)).refreshTable();
            ((OrderHistoryPanel) tabbedPane.getComponentAt(4)).refreshTable();
        });
        viewMenu.add(refreshItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        
        return menuBar;
    }

    private JPanel createMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create list of movies
        movieListModel = new DefaultListModel<>();
        JList<String> movieList = new JList<>(movieListModel);
        refreshMovieList();
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Movie");
        JButton editButton = new JButton("Edit Movie");
        JButton deleteButton = new JButton("Delete Movie");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        panel.add(new JScrollPane(movieList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddMovieDialog());
        editButton.addActionListener(e -> {
            String selected = movieList.getSelectedValue();
            if (selected != null) {
                int movieId = Integer.parseInt(selected.split(":")[0].trim());
                Movie movie = MovieDatabase.getMovieById(movieId);
                if (movie != null) {
                    showEditMovieDialog(movie);
                }
            }
        });
        deleteButton.addActionListener(e -> {
            String selected = movieList.getSelectedValue();
            if (selected != null) {
                int movieId = Integer.parseInt(selected.split(":")[0].trim());
                deleteMovie(movieId);
            }
        });
        
        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create list of rooms
        roomListModel = new DefaultListModel<>();
        JList<String> roomList = new JList<>(roomListModel);
        refreshRoomList();
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        panel.add(new JScrollPane(roomList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddRoomDialog());
        editButton.addActionListener(e -> {
            String selected = roomList.getSelectedValue();
            if (selected != null) {
                int roomId = Integer.parseInt(selected.split(":")[0].trim());
                Room room = RoomDatabase.getRoomById(roomId);
                if (room != null) {
                    showEditRoomDialog(room);
                }
            }
        });
        deleteButton.addActionListener(e -> {
            String selected = roomList.getSelectedValue();
            if (selected != null) {
                int roomId = Integer.parseInt(selected.split(":")[0].trim());
                deleteRoom(roomId);
            }
        });
        
        return panel;
    }

    private JPanel createSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create list of sessions
        sessionListModel = new DefaultListModel<>();
        JList<String> sessionList = new JList<>(sessionListModel);
        refreshSessionList();
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Session");
        JButton editButton = new JButton("Edit Session");
        JButton deleteButton = new JButton("Delete Session");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        panel.add(new JScrollPane(sessionList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddSessionDialog());
        editButton.addActionListener(e -> {
            String selected = sessionList.getSelectedValue();
            if (selected != null) {
                int sessionId = Integer.parseInt(selected.split(":")[0].trim());
                Session session = SessionDatabase.getSessionById(sessionId);
                if (session != null) {
                    showEditSessionDialog(session);
                }
            }
        });
        deleteButton.addActionListener(e -> {
            String selected = sessionList.getSelectedValue();
            if (selected != null) {
                int sessionId = Integer.parseInt(selected.split(":")[0].trim());
                deleteSession(sessionId);
            }
        });
        
        return panel;
    }

    private void refreshMovieList() {
        movieListModel.clear();
        List<Movie> movies = MovieDatabase.getAllMovies();
        for (Movie movie : movies) {
            movieListModel.addElement(String.format("%d: %s (%s) - %d min", 
                movie.getId(), movie.getName(), movie.getLanguage(), movie.getDuration()));
        }
    }

    private void refreshRoomList() {
        roomListModel.clear();
        List<Room> rooms = RoomDatabase.getAllRooms();
        for (Room room : rooms) {
            roomListModel.addElement(String.format("%d: %s (%dx%d)%s", 
                room.getId(), room.getName(), room.getRows(), room.getColumns(),
                room.is3D() ? " - 3D" : ""));
        }
    }

    private void refreshSessionList() {
        sessionListModel.clear();
        List<Session> sessions = SessionDatabase.getAllSessions();
        for (Session session : sessions) {
            Movie movie = session.getMovie();
            Room room = session.getRoom();
            sessionListModel.addElement(String.format("%d: %s - %s (Room: %s) - $%.2f", 
                session.getId(), movie.getName(), 
                session.getStartTime().format(DATE_FORMATTER), 
                room.getName(),
                session.getPrice()));
        }
    }

    private void showAddMovieDialog() {
        JTextField nameField = new JTextField(20);
        JTextField languageField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField directorField = new JTextField(20);
        JTextField durationField = new JTextField(5);
        JSpinner releaseDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(releaseDateSpinner, "yyyy-MM-dd");
        releaseDateSpinner.setEditor(dateEditor);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Language:"));
        panel.add(languageField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Director:"));
        panel.add(directorField);
        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationField);
        panel.add(new JLabel("Release Date:"));
        panel.add(releaseDateSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Movie",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String language = languageField.getText().trim();
                String category = categoryField.getText().trim();
                String director = directorField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());
                java.util.Date date = (java.util.Date)releaseDateSpinner.getValue();
                LocalDate releaseDate = LocalDate.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault());
                
                if (name.isEmpty() || language.isEmpty() || category.isEmpty() || director.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled");
                }
                
                Movie movie = new Movie(name, language, category, director, releaseDate, duration);
                MovieDatabase.saveMovie(movie);
                refreshMovieList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid duration format");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showEditMovieDialog(Movie movie) {
        JTextField nameField = new JTextField(movie.getName(), 20);
        JTextField languageField = new JTextField(movie.getLanguage(), 20);
        JTextField categoryField = new JTextField(movie.getCategory(), 20);
        JTextField directorField = new JTextField(movie.getDirector(), 20);
        JTextField durationField = new JTextField(String.valueOf(movie.getDuration()), 5);
        JSpinner releaseDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(releaseDateSpinner, "yyyy-MM-dd");
        releaseDateSpinner.setEditor(dateEditor);
        releaseDateSpinner.setValue(java.util.Date.from(
            movie.getReleaseDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Language:"));
        panel.add(languageField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Director:"));
        panel.add(directorField);
        panel.add(new JLabel("Duration (minutes):"));
        panel.add(durationField);
        panel.add(new JLabel("Release Date:"));
        panel.add(releaseDateSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Movie",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String language = languageField.getText().trim();
                String category = categoryField.getText().trim();
                String director = directorField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());
                java.util.Date date = (java.util.Date)releaseDateSpinner.getValue();
                LocalDate releaseDate = LocalDate.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault());
                
                if (name.isEmpty() || language.isEmpty() || category.isEmpty() || director.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled");
                }
                
                movie.setName(name);
                movie.setLanguage(language);
                movie.setCategory(category);
                movie.setDirector(director);
                movie.setDuration(duration);
                movie.setReleaseDate(releaseDate);
                
                MovieDatabase.saveMovie(movie);
                refreshMovieList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid duration format");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showAddRoomDialog() {
        JTextField nameField = new JTextField(20);
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(6, 1, 20, 1));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 20, 1));
        JCheckBox is3DCheckBox = new JCheckBox("3D Capable");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Rows:"));
        panel.add(rowsSpinner);
        panel.add(new JLabel("Columns:"));
        panel.add(colsSpinner);
        panel.add(new JLabel("Capabilities:"));
        panel.add(is3DCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int rows = (Integer)rowsSpinner.getValue();
                int cols = (Integer)colsSpinner.getValue();
                boolean is3D = is3DCheckBox.isSelected();
                
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Room name must be filled");
                }
                
                Room room = new Room(name, rows, cols, is3D);
                RoomDatabase.saveRoom(room);
                refreshRoomList();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showEditRoomDialog(Room room) {
        JTextField nameField = new JTextField(room.getName(), 20);
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(room.getRows(), 1, 20, 1));
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(room.getColumns(), 1, 20, 1));
        JCheckBox is3DCheckBox = new JCheckBox("3D Capable", room.is3D());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Rows:"));
        panel.add(rowsSpinner);
        panel.add(new JLabel("Columns:"));
        panel.add(colsSpinner);
        panel.add(new JLabel("Capabilities:"));
        panel.add(is3DCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int rows = (Integer)rowsSpinner.getValue();
                int cols = (Integer)colsSpinner.getValue();
                boolean is3D = is3DCheckBox.isSelected();
                
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Room name must be filled");
                }
                
                room.setName(name);
                room.setRows(rows);
                room.setColumns(cols);
                room.set3D(is3D);
                
                RoomDatabase.saveRoom(room);
                refreshRoomList();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showAddSessionDialog() {
        List<Movie> movies = MovieDatabase.getAllMovies();
        List<Room> rooms = RoomDatabase.getAllRooms();
        
        if (movies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add movies first");
            return;
        }
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add rooms first");
            return;
        }

        JComboBox<Movie> movieBox = new JComboBox<>(movies.toArray(new Movie[0]));
        JComboBox<Room> roomBox = new JComboBox<>(rooms.toArray(new Room[0]));
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date()); // Set current date/time as default
        JTextField priceField = new JTextField("15.99", 10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Movie:"));
        panel.add(movieBox);
        panel.add(new JLabel("Room:"));
        panel.add(roomBox);
        panel.add(new JLabel("Date/Time:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Session",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                Movie selectedMovie = (Movie)movieBox.getSelectedItem();
                Room selectedRoom = (Room)roomBox.getSelectedItem();
                java.util.Date date = (java.util.Date)dateSpinner.getValue();
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (price <= 0) {
                    throw new IllegalArgumentException("Price must be greater than 0");
                }
                
                Session session = new Session(selectedMovie, selectedRoom, dateTime, price);
                SessionDatabase.saveSession(session);
                refreshSessionList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void showEditSessionDialog(Session session) {
        List<Movie> movies = MovieDatabase.getAllMovies();
        List<Room> rooms = RoomDatabase.getAllRooms();
        
        JComboBox<Movie> movieBox = new JComboBox<>(movies.toArray(new Movie[0]));
        movieBox.setSelectedItem(session.getMovie());
        
        JComboBox<Room> roomBox = new JComboBox<>(rooms.toArray(new Room[0]));
        roomBox.setSelectedItem(session.getRoom());
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.util.Date.from(
            session.getStartTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        
        JTextField priceField = new JTextField(String.format("%.2f", session.getPrice()), 10);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Movie:"));
        panel.add(movieBox);
        panel.add(new JLabel("Room:"));
        panel.add(roomBox);
        panel.add(new JLabel("Date/Time:"));
        panel.add(dateSpinner);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Session",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                Movie selectedMovie = (Movie)movieBox.getSelectedItem();
                Room selectedRoom = (Room)roomBox.getSelectedItem();
                java.util.Date date = (java.util.Date)dateSpinner.getValue();
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                    date.toInstant(), java.time.ZoneId.systemDefault());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (price <= 0) {
                    throw new IllegalArgumentException("Price must be greater than 0");
                }
                
                session.setMovie(selectedMovie);
                session.setRoom(selectedRoom);
                session.setStartTime(dateTime);
                session.setPrice(price);
                
                SessionDatabase.saveSession(session);
                refreshSessionList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void deleteMovie(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this movie?\nThis will also delete all associated sessions.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            MovieDatabase.deleteMovie(id);
            refreshMovieList();
            refreshSessionList(); // Refresh sessions as they might be affected
        }
    }

    private void deleteRoom(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this room?\nThis will also delete all associated sessions.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            RoomDatabase.deleteRoom(id);
            refreshRoomList();
            refreshSessionList(); // Refresh sessions as they might be affected
        }
    }

    private void deleteSession(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this session?\nThis will also cancel all associated tickets.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            SessionDatabase.deleteSession(id);
            refreshSessionList();
        }
    }
} 