package projeto.es.views;

import projeto.es.models.Product;
import projeto.es.models.OrderItem;
import projeto.es.Repository.ProductDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSelectionPanel extends JPanel {
    private final Map<String, JSpinner> quantitySpinners = new HashMap<>();
    private final List<OrderItem> selectedItems = new ArrayList<>();
    private final JLabel totalLabel;
    private final Runnable onUpdateCallback;

    public ProductSelectionPanel(Runnable onUpdateCallback) {
        this.onUpdateCallback = onUpdateCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize products
        ProductDatabase.initialize();

        // Create main panel with categories
        JTabbedPane categoryTabs = new JTabbedPane();
        
        // Add products by category
        addCategoryTab(categoryTabs, "Drinks");
        addCategoryTab(categoryTabs, "Snacks");
        addCategoryTab(categoryTabs, "Combos");

        add(categoryTabs, BorderLayout.CENTER);

        // Add total panel at bottom
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Products Total: $0.00");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        totalPanel.add(totalLabel);
        add(totalPanel, BorderLayout.SOUTH);
    }

    private void addCategoryTab(JTabbedPane tabbedPane, String category) {
        List<Product> products = ProductDatabase.getProductsByCategory(category);
        if (products.isEmpty()) return;

        JPanel categoryPanel = new JPanel(new GridBagLayout());
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;
        for (Product product : products) {
            // Product panel
            JPanel productPanel = new JPanel(new BorderLayout(5, 5));
            productPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Product info
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
            infoPanel.add(new JLabel(product.getName()));
            infoPanel.add(new JLabel(String.format("$%.2f", product.getPrice())));
            infoPanel.add(new JLabel("In stock: " + product.getStock()));
            productPanel.add(infoPanel, BorderLayout.CENTER);

            // Quantity spinner
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, product.getStock(), 1);
            JSpinner spinner = new JSpinner(spinnerModel);
            spinner.addChangeListener(e -> updateSelection());
            quantitySpinners.put(product.getId(), spinner);

            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            quantityPanel.add(new JLabel("Quantity:"));
            quantityPanel.add(spinner);
            productPanel.add(quantityPanel, BorderLayout.SOUTH);

            gbc.gridy = row++;
            categoryPanel.add(productPanel, gbc);
        }

        // Add filler to push components to the top
        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        categoryPanel.add(Box.createVerticalGlue(), gbc);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(categoryPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab(category, scrollPane);
    }

    private void updateSelection() {
        selectedItems.clear();
        double total = 0.0;

        for (Map.Entry<String, JSpinner> entry : quantitySpinners.entrySet()) {
            int quantity = (Integer) entry.getValue().getValue();
            if (quantity > 0) {
                Product product = ProductDatabase.getProduct(entry.getKey());
                OrderItem item = new OrderItem(product, quantity);
                selectedItems.add(item);
                total += item.getTotalPrice();
            }
        }

        totalLabel.setText(String.format("Products Total: $%.2f", total));
        if (onUpdateCallback != null) {
            onUpdateCallback.run();
        }
    }

    public List<OrderItem> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public double getTotal() {
        return selectedItems.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
    }

    public void clearSelection() {
        quantitySpinners.values().forEach(spinner -> spinner.setValue(0));
        updateSelection();
    }
} 