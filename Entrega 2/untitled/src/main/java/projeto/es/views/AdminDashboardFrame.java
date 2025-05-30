package projeto.es.views;

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

    public AdminDashboardFrame(User admin) {
        super("Admin Dashboard");
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("User must be an admin");
        }
        this.admin = admin;

        setLayout(new BorderLayout());
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Movies", createMoviesPanel());
        tabbedPane.addTab("Rooms", createRoomsPanel());
        tabbedPane.addTab("Sessions", createSessionsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel createMoviesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create list of movies
        movieListModel = new DefaultListModel<>();
        JList<String> movieList = new JList<>(movieListModel);
        refreshMovieList();
        
        // Create buttons
        JButton addButton = new JButton("Add Movie");
        JButton editButton = new JButton("Edit Movie");
        JButton deleteButton = new JButton("Delete Movie");
        
        JPanel buttonPanel = new JPanel();
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
                int movieId = Integer.parseInt(selected.split(":")[0]);
                showEditMovieDialog(MovieDatabase.getMovieById(movieId));
            }
        });
        deleteButton.addActionListener(e -> {
            String selected = movieList.getSelectedValue();
            if (selected != null) {
                int movieId = Integer.parseInt(selected.split(":")[0]);
                deleteMovie(movieId);
            }
        });
        
        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create list of rooms
        roomListModel = new DefaultListModel<>();
        JList<String> roomList = new JList<>(roomListModel);
        refreshRoomList();
        
        // Create buttons
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        
        JPanel buttonPanel = new JPanel();
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
                int roomId = Integer.parseInt(selected.split(":")[0]);
                showEditRoomDialog(RoomDatabase.getRoomById(roomId));
            }
        });
        deleteButton.addActionListener(e -> {
            String selected = roomList.getSelectedValue();
            if (selected != null) {
                int roomId = Integer.parseInt(selected.split(":")[0]);
                deleteRoom(roomId);
            }
        });
        
        return panel;
    }

    private JPanel createSessionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create list of sessions
        sessionListModel = new DefaultListModel<>();
        JList<String> sessionList = new JList<>(sessionListModel);
        refreshSessionList();
        
        // Create buttons
        JButton addButton = new JButton("Add Session");
        JButton deleteButton = new JButton("Delete Session");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        
        // Add components
        panel.add(new JScrollPane(sessionList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddSessionDialog());
        deleteButton.addActionListener(e -> {
            String selected = sessionList.getSelectedValue();
            if (selected != null) {
                int sessionId = Integer.parseInt(selected.split(":")[0]);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Session session : sessions) {
            Movie movie = session.getMovie();
            Room room = session.getRoom();
            sessionListModel.addElement(String.format("%d: %s - %s (Room: %s) - $%.2f", 
                session.getId(), movie.getName(), 
                session.getStartTime().format(formatter), 
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

        JPanel panel = new JPanel(new GridLayout(0, 2));
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
                java.util.Date date = (java.util.Date)releaseDateSpinner.getValue();
                LocalDate releaseDate = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                
                Movie movie = new Movie(
                    nameField.getText(),
                    languageField.getText(),
                    categoryField.getText(),
                    directorField.getText(),
                    releaseDate,
                    Integer.parseInt(durationField.getText())
                );
                MovieDatabase.saveMovie(movie);
                refreshMovieList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
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
            movie.getReleaseDate().atStartOfDay()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
        ));

        JPanel panel = new JPanel(new GridLayout(0, 2));
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
                java.util.Date date = (java.util.Date)releaseDateSpinner.getValue();
                LocalDate releaseDate = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                
                movie.setName(nameField.getText());
                movie.setLanguage(languageField.getText());
                movie.setCategory(categoryField.getText());
                movie.setDirector(directorField.getText());
                movie.setDuration(Integer.parseInt(durationField.getText()));
                movie.setReleaseDate(releaseDate);
                
                MovieDatabase.updateMovie(movie);
                refreshMovieList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }

    private void showAddRoomDialog() {
        JTextField nameField = new JTextField(20);
        JTextField rowsField = new JTextField(5);
        JTextField columnsField = new JTextField(5);
        JCheckBox is3DBox = new JCheckBox();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Rows:"));
        panel.add(rowsField);
        panel.add(new JLabel("Columns:"));
        panel.add(columnsField);
        panel.add(new JLabel("3D:"));
        panel.add(is3DBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                Room room = new Room(
                    nameField.getText(),
                    Integer.parseInt(rowsField.getText()),
                    Integer.parseInt(columnsField.getText()),
                    is3DBox.isSelected()
                );
                RoomDatabase.saveRoom(room);
                refreshRoomList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
            }
        }
    }

    private void showEditRoomDialog(Room room) {
        JTextField nameField = new JTextField(room.getName(), 20);
        JTextField rowsField = new JTextField(String.valueOf(room.getRows()), 5);
        JTextField columnsField = new JTextField(String.valueOf(room.getColumns()), 5);
        JCheckBox is3DBox = new JCheckBox("", room.is3D());

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Rows:"));
        panel.add(rowsField);
        panel.add(new JLabel("Columns:"));
        panel.add(columnsField);
        panel.add(new JLabel("3D:"));
        panel.add(is3DBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
        if (result == JOptionPane.OK_OPTION) {
            try {
                room.setName(nameField.getText());
                room.setRows(Integer.parseInt(rowsField.getText()));
                room.setColumns(Integer.parseInt(columnsField.getText()));
                room.set3D(is3DBox.isSelected());
                
                RoomDatabase.updateRoom(room);
                refreshRoomList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format");
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
        JTextField priceField = new JTextField("15.99", 10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
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
                double price = Double.parseDouble(priceField.getText());
                
                Session session = new Session(
                    selectedMovie,
                    selectedRoom,
                    dateTime,
                    price
                );
                
                SessionDatabase.saveSession(session);
                refreshSessionList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format");
            }
        }
    }

    private void deleteMovie(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this movie?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            MovieDatabase.deleteMovie(id);
            refreshMovieList();
        }
    }

    private void deleteRoom(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this room?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            RoomDatabase.deleteRoom(id);
            refreshRoomList();
        }
    }

    private void deleteSession(int id) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this session?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            SessionDatabase.deleteSession(id);
            refreshSessionList();
        }
    }
} 