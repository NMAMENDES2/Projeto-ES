package projeto.es.views;
import projeto.es.views.*;

import projeto.es.models.User;
import projeto.es.Repository.UserDatabase;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        super("Movie Theater Login");

        // Create main panel with some padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Login"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username field with icon
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(userLabel.getFont().deriveFont(Font.BOLD));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // Password field with icon
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(passLabel.getFont().deriveFont(Font.BOLD));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setEchoChar('●'); // Use a more visible bullet character
        formPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setFont(loginButton.getFont().deriveFont(Font.BOLD));
        loginButton.setPreferredSize(new Dimension(200, 35));
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton, gbc);

        // Add form panel to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Add test credentials label
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel helpLabel = new JLabel("Test credentials: admin/admin or test/test");
        helpLabel.setForeground(Color.GRAY);
        helpPanel.add(helpLabel);
        mainPanel.add(helpPanel, BorderLayout.SOUTH);

        // Set up the frame
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setMinimumSize(new Dimension(350, 250));

        // Add enter key listener
        getRootPane().setDefaultButton(loginButton);

        // Set initial focus
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("DEBUG: Attempting login for user: " + username);
        User user = UserDatabase.authenticate(username, password);
        
        if (user != null) {
            System.out.println("DEBUG: Login successful for user: " + username + ", isAdmin: " + user.isAdmin());
            User.setCurrentUser(user);
            if (user.isAdmin()) {
                System.out.println("DEBUG: Creating AdminDashboardFrame");
                projeto.es.views.AdminDashboardFrame adminFrame = new projeto.es.views.AdminDashboardFrame(user);
                adminFrame.setVisible(true);
            } else {
                System.out.println("DEBUG: Creating ClientFrame");
                try {
                    ClientFrame clientFrame = new projeto.es.views.ClientFrame(user);
                    System.out.println("DEBUG: ClientFrame created successfully");
                    clientFrame.setVisible(true);
                    System.out.println("DEBUG: ClientFrame set to visible");
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to create/show ClientFrame");
                    e.printStackTrace();
                    User.setCurrentUser(null);
                }
            }
            System.out.println("DEBUG: Disposing login frame");
            this.dispose(); // close login window
        } else {
            System.out.println("DEBUG: Login failed for user: " + username);
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); // Clear password field on failed attempt
            passwordField.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}
