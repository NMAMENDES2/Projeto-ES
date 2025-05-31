package projeto.es.views;

import projeto.es.models.*;
import projeto.es.Repository.ProductDatabase;
import projeto.es.Repository.ProductOrderDatabase;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BarFrame extends JFrame {
    private final User user;
    private final Map<String, JSpinner> quantitySpinners = new HashMap<>();
    private final List<OrderItem> selectedItems = new ArrayList<>();
    private JLabel totalLabel;
    private double totalPrice = 0.0;

    public BarFrame(User user) {
        super("Movie Theater Bar");
        this.user = user;
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPanel);

        // Initialize products
        ProductDatabase.initialize();

        // Create products panel
        JPanel productsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add products
        List<Product> products = ProductDatabase.getAllProducts();
        int row = 0;
        for (Product product : products) {
            JPanel productPanel = createProductPanel(product);
            gbc.gridy = row++;
            productsPanel.add(productPanel, gbc);
        }

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add bottom panel with total and checkout button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total label
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        bottomPanel.add(totalLabel, BorderLayout.CENTER);

        // Checkout button
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> handleCheckout());
        bottomPanel.add(checkoutButton, BorderLayout.EAST);

        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel createProductPanel(Product product) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        infoPanel.add(new JLabel(product.getName()));
        infoPanel.add(new JLabel(String.format("$%.2f", product.getPrice())));
        infoPanel.add(new JLabel("In stock: " + product.getStock()));
        if (product.getMinimumAge() > 0) {
            infoPanel.add(new JLabel("Age: " + product.getMinimumAge() + "+"));
        }
        panel.add(infoPanel, BorderLayout.CENTER);

        // Quantity spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, product.getStock(), 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.addChangeListener(e -> updateSelection());
        quantitySpinners.put(product.getId(), spinner);

        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        quantityPanel.add(new JLabel("Quantity:"));
        quantityPanel.add(spinner);
        panel.add(quantityPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateSelection() {
        selectedItems.clear();
        totalPrice = 0.0;

        for (Map.Entry<String, JSpinner> entry : quantitySpinners.entrySet()) {
            int quantity = (Integer) entry.getValue().getValue();
            if (quantity > 0) {
                Product product = ProductDatabase.getProduct(entry.getKey());
                OrderItem item = new OrderItem(product, quantity);
                selectedItems.add(item);
                totalPrice += item.getTotalPrice();
            }
        }

        totalLabel.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void handleCheckout() {
        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select at least one item",
                "No Items Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check age restrictions
        boolean hasAgeRestriction = selectedItems.stream()
            .anyMatch(item -> item.getProduct().getMinimumAge() > 0);

        if (hasAgeRestriction) {
            int choice = JOptionPane.showConfirmDialog(this,
                "This order contains age-restricted items. Are you 18 or older?",
                "Age Verification",
                JOptionPane.YES_NO_OPTION);
            
            if (choice != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Sorry, you must be 18 or older to purchase these items.",
                    "Age Restriction",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Process the order
        for (OrderItem item : selectedItems) {
            if (!ProductDatabase.updateProductStock(item.getProduct().getId(), -item.getQuantity())) {
                JOptionPane.showMessageDialog(this,
                    "Sorry, some products are no longer in stock. Please review your order.",
                    "Stock Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Save the order
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        ProductOrder order = new ProductOrder(orderId, user, selectedItems, totalPrice);
        ProductOrderDatabase.addOrder(order);

        // Show success message
        JOptionPane.showMessageDialog(this,
            String.format("Order successful!\nOrder ID: %s\nTotal amount: $%.2f", orderId, totalPrice),
            "Order Complete",
            JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }
} 