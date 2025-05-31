package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.Repository.MovieDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovieManagementPanel extends JPanel {
    private DefaultListModel<Movie> movieListModel;
    private JList<Movie> movieList;

    public MovieManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Movie list
        movieListModel = new DefaultListModel<>();
        movieList = new JList<>(movieListModel);
        movieList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Movie movie = (Movie) value;
                setText(movie.getName() + " - " + movie.getCategory());
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(movieList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Movie");
        JButton editButton = new JButton("Edit Movie");
        JButton deleteButton = new JButton("Delete Movie");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addMovie());
        editButton.addActionListener(e -> {
            Movie selected = movieList.getSelectedValue();
            if (selected != null) {
                editMovie(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a movie to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            Movie selected = movieList.getSelectedValue();
            if (selected != null) {
                deleteMovie(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a movie to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshMovieList());

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Initial load
        refreshMovieList();
    }

    private void refreshMovieList() {
        movieListModel.clear();
        List<Movie> movies = MovieDatabase.getAllMovies();
        for (Movie movie : movies) {
            movieListModel.addElement(movie);
        }
    }

    private void addMovie() {
        MovieDialog dialog = new MovieDialog(null);
        dialog.setVisible(true);
        refreshMovieList();
    }

    private void editMovie(Movie movie) {
        MovieDialog dialog = new MovieDialog(movie);
        dialog.setVisible(true);
        refreshMovieList();
    }

    private void deleteMovie(Movie movie) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this movie?\n" + movie.getName(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            MovieDatabase.deleteMovie(movie.getId());
            refreshMovieList();
        }
    }
} 