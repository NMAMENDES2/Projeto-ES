package projeto.es.views;

import projeto.es.models.Movie;
import projeto.es.Repository.MovieDatabase;

import javax.swing.*;
import java.awt.*;

public class MovieDialog extends JDialog {
    private final Movie movie;
    private JTextField idField;
    private JTextField nameField;
    private JTextField categoryField;
    private JTextField directorField;
    private JSpinner durationSpinner;
    private JTextField languageField;

    public MovieDialog(Movie movie) {
        super((Frame) null, movie == null ? "Add Movie" : "Edit Movie", true);
        this.movie = movie;
        
        setLayout(new BorderLayout(10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ID field
        idField = new JTextField(20);
        if (movie != null) {
            idField.setText(movie.getId());
            idField.setEnabled(false);
        }
        addFormField(formPanel, "ID:", idField, gbc);
        
        // Name field
        nameField = new JTextField(20);
        if (movie != null) nameField.setText(movie.getName());
        addFormField(formPanel, "Name:", nameField, gbc);
        
        // Category field
        categoryField = new JTextField(20);
        if (movie != null) categoryField.setText(movie.getCategory());
        addFormField(formPanel, "Category:", categoryField, gbc);
        
        // Director field
        directorField = new JTextField(20);
        if (movie != null) directorField.setText(movie.getDirector());
        addFormField(formPanel, "Director:", directorField, gbc);
        
        // Duration spinner
        durationSpinner = new JSpinner(new SpinnerNumberModel(
            movie != null ? movie.getDuration() : 90, 1, 300, 1));
        addFormField(formPanel, "Duration (minutes):", durationSpinner, gbc);
        
        // Language field
        languageField = new JTextField(20);
        if (movie != null) languageField.setText(movie.getLanguage());
        addFormField(formPanel, "Language:", languageField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            if (validateAndSave()) {
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        gbc.gridy++;
    }
    
    private boolean validateAndSave() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String director = directorField.getText().trim();
            int duration = (Integer) durationSpinner.getValue();
            String language = languageField.getText().trim();
            
            if (id.isEmpty() || name.isEmpty() || category.isEmpty() || 
                director.isEmpty() || language.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled");
            }
            
            Movie newMovie = new Movie(id, name, category, director, duration, language);
            
            if (movie == null) {
                // Check if ID already exists
                if (MovieDatabase.getMovie(id) != null) {
                    throw new IllegalArgumentException("Movie ID already exists");
                }
            }
            
            MovieDatabase.saveMovie(newMovie);
            return true;
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
} 