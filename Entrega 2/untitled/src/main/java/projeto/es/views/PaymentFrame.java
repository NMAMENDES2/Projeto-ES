package projeto.es.views;

import projeto.es.models.*;
import projeto.es.Repository.ProductDatabase;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentFrame extends JFrame {
    private final User user;
    private final Session session;
    private final List<Point> selectedSeats;
    private ProductSelectionPanel productPanel;
    private JLabel totalPriceLabel;
    private double totalPrice;
    private JComboBox<String> paymentMethodCombo;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JButton fillTestDataButton;

    public PaymentFrame(User user, Session session, List<Point> selectedSeats) {
        super("Payment - " + session.getMovie().getName());
        this.user = user;
        this.session = session;
        this.selectedSeats = selectedSeats;
        
        setLayout(new BorderLayout(10, 10));
        
        // Order summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);
        
        // Product selection panel
        productPanel = new ProductSelectionPanel(this::updateTotalPrice);
        add(productPanel, BorderLayout.CENTER);
        
        // Payment panel
        JPanel paymentPanel = createPaymentPanel();
        add(paymentPanel, BorderLayout.SOUTH);
        
        updateTotalPrice();
        
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Check if session is in the past
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                "Cannot book tickets for past sessions.",
                "Invalid Session",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Add input validation
        addInputValidation();
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Summary"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Movie and session info
        panel.add(new JLabel("Movie: " + session.getMovie().getName()));
        panel.add(new JLabel("Session: " + session.getStartTime()));
        panel.add(new JLabel("Room: " + session.getRoom().getName()));
        
        // Selected seats
        StringBuilder seatsStr = new StringBuilder("Selected Seats: ");
        for (Point seat : selectedSeats) {
            if (seatsStr.length() > 15) seatsStr.append(", ");
            seatsStr.append(String.format("%c%d", (char)('A' + seat.x), seat.y + 1));
        }
        panel.add(new JLabel(seatsStr.toString()));
        
        // Ticket prices
        panel.add(new JLabel(String.format("Ticket Price: $%.2f each", session.getPrice())));
        panel.add(new JLabel(String.format("Tickets Total: $%.2f", session.getPrice() * selectedSeats.size())));
        
        return panel;
    }
    
    private JPanel createPaymentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Payment form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Payment method
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        paymentMethodCombo = new JComboBox<>(new String[]{"Credit Card", "Debit Card"});
        gbc.gridx = 1;
        formPanel.add(paymentMethodCombo, gbc);

        // Card number
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Card Number:"), gbc);
        cardNumberField = new JTextField(16);
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);

        // Expiry date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Expiry Date (MM/YY):"), gbc);
        expiryField = new JTextField(5);
        gbc.gridx = 1;
        formPanel.add(expiryField, gbc);

        // CVV
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("CVV:"), gbc);
        cvvField = new JTextField(3);
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);

        // Test data button
        fillTestDataButton = new JButton("Fill Test Data");
        fillTestDataButton.addActionListener(e -> fillTestData());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(fillTestDataButton, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Bottom panel with total and pay button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Total price
        totalPriceLabel = new JLabel();
        totalPriceLabel.setFont(totalPriceLabel.getFont().deriveFont(Font.BOLD, 16));
        bottomPanel.add(totalPriceLabel, BorderLayout.CENTER);
        
        // Pay button
        JButton payButton = new JButton("Complete Payment");
        payButton.addActionListener(e -> processPayment());
        bottomPanel.add(payButton, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private void addInputValidation() {
        // Card number validation (numbers only, max 16 digits)
        cardNumberField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || cardNumberField.getText().length() >= 16) {
                    e.consume();
                }
            }
        });

        // Expiry date validation (MM/YY format)
        expiryField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String text = expiryField.getText();
                char c = e.getKeyChar();
                if ((!Character.isDigit(c) && c != '/') || text.length() >= 5 ||
                    (c == '/' && text.contains("/"))) {
                    e.consume();
                }
            }
        });

        // CVV validation (numbers only, max 3 digits)
        cvvField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || cvvField.getText().length() >= 3) {
                    e.consume();
                }
            }
        });
    }

    private void fillTestData() {
        cardNumberField.setText("4111111111111111");
        expiryField.setText("12/25");
        cvvField.setText("123");
    }
    
    private void updateTotalPrice() {
        double ticketsTotal = session.getPrice() * selectedSeats.size();
        double productsTotal = productPanel.getTotal();
        totalPrice = ticketsTotal + productsTotal;
        totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
    }
    
    private void processPayment() {
        // Validate payment fields
        if (cardNumberField.getText().length() != 16 ||
            !expiryField.getText().matches("\\d{2}/\\d{2}") ||
            cvvField.getText().length() != 3) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid payment details",
                "Invalid Payment Details",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create tickets with products
        for (Point seat : selectedSeats) {
            Ticket ticket = new Ticket(session, seat.x, seat.y);
            
            // Add selected products to the ticket
            for (OrderItem item : productPanel.getSelectedItems()) {
                // Decrease product stock
                if (!ProductDatabase.updateProductStock(item.getProduct().getId(), -item.getQuantity())) {
                    JOptionPane.showMessageDialog(this,
                        "Sorry, some products are no longer in stock. Please review your order.",
                        "Stock Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ticket.addOrderItem(item);
            }
            
            // Add ticket to user
            user.addTicket(ticket);
            
            // Mark seat as occupied
            session.occupySeat(seat.x, seat.y);
        }
        
        // Save the updated session to persist occupied seats
        SessionDatabase.saveSession(session);
        
        // Show success message
        JOptionPane.showMessageDialog(this,
            String.format("Payment successful!\nTotal amount: $%.2f", totalPrice),
            "Payment Complete",
            JOptionPane.INFORMATION_MESSAGE);
            
        dispose(); // Close payment window
    }
} 