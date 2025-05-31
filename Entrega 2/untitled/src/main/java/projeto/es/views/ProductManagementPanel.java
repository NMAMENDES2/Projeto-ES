package projeto.es.views;

import projeto.es.models.Product;
import projeto.es.Repository.ProductDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

public class ProductManagementPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private final String[] CATEGORIES = {"Drinks", "Snacks", "Combos"};

    public ProductManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showProductDialog(null));
        editButton.addActionListener(e -> editSelectedProduct());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        refreshButton.addActionListener(e -> refreshTable());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Initial load
        refreshTable();
    }

    private void createTable() {
        String[] columns = {"ID", "Name", "Price", "Stock", "Category", "Description", "Allergens", "Min Age"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Product> products = ProductDatabase.getAllProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                String.format("$%.2f", product.getPrice()),
                product.getStock(),
                product.getCategory(),
                product.getDescription(),
                String.join(", ", product.getAllergens()),
                product.getMinimumAge() > 0 ? product.getMinimumAge() + "+" : "None"
            });
        }
    }

    private void showProductDialog(Product product) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            product == null ? "Add Product" : "Edit Product", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID field
        JTextField idField = new JTextField(20);
        if (product != null) {
            idField.setText(product.getId());
            idField.setEnabled(false);
        }
        addFormField(formPanel, "ID:", idField, gbc);

        // Name field
        JTextField nameField = new JTextField(20);
        if (product != null) nameField.setText(product.getName());
        addFormField(formPanel, "Name:", nameField, gbc);

        // Price field
        JTextField priceField = new JTextField(20);
        if (product != null) priceField.setText(String.format("%.2f", product.getPrice()));
        addFormField(formPanel, "Price:", priceField, gbc);

        // Stock field
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(
            product != null ? product.getStock() : 0, 0, 999, 1));
        addFormField(formPanel, "Stock:", stockSpinner, gbc);

        // Category combo
        JComboBox<String> categoryCombo = new JComboBox<>(CATEGORIES);
        if (product != null) categoryCombo.setSelectedItem(product.getCategory());
        addFormField(formPanel, "Category:", categoryCombo, gbc);

        // Description field
        JTextField descField = new JTextField(20);
        if (product != null) descField.setText(product.getDescription());
        addFormField(formPanel, "Description:", descField, gbc);

        // Allergens field
        JTextField allergensField = new JTextField(20);
        if (product != null) allergensField.setText(String.join(", ", product.getAllergens()));
        addFormField(formPanel, "Allergens:", allergensField, gbc);

        // Minimum age spinner
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(
            product != null ? product.getMinimumAge() : 0, 0, 21, 1));
        addFormField(formPanel, "Minimum Age:", ageSpinner, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = (Integer) stockSpinner.getValue();
                String category = (String) categoryCombo.getSelectedItem();
                String description = descField.getText().trim();
                Set<String> allergens = new HashSet<>();
                if (!allergensField.getText().trim().isEmpty()) {
                    allergens.addAll(Arrays.asList(allergensField.getText().split("\\s*,\\s*")));
                }
                int minAge = (Integer) ageSpinner.getValue();

                if (id.isEmpty() || name.isEmpty() || description.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled");
                }

                Product newProduct = new Product(id, name, price, stock, category, 
                    description, allergens, minAge);
                
                if (product == null) {
                    // Check if ID already exists
                    if (ProductDatabase.getProduct(id) != null) {
                        throw new IllegalArgumentException("Product ID already exists");
                    }
                }
                
                ProductDatabase.saveProduct(newProduct);
                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Invalid number format",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            Product product = ProductDatabase.getProduct(id);
            if (product != null) {
                showProductDialog(product);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a product to edit",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ProductDatabase.deleteProduct(id);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a product to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
} 