package projeto.es.views;

import projeto.es.models.ProductOrder;
import projeto.es.models.OrderItem;
import projeto.es.Repository.ProductOrderDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public OrderHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Initial load
        refreshTable();
    }

    private void createTable() {
        String[] columns = {"Order ID", "User", "Items", "Total Price", "Date/Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(150);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<ProductOrder> orders = ProductOrderDatabase.getAllOrders();
        
        for (ProductOrder order : orders) {
            // Format items as a readable string
            StringBuilder itemsStr = new StringBuilder();
            for (OrderItem item : order.getItems()) {
                if (itemsStr.length() > 0) itemsStr.append(", ");
                itemsStr.append(String.format("%dx %s", 
                    item.getQuantity(), 
                    item.getProduct().getName()));
            }
            
            tableModel.addRow(new Object[]{
                order.getId(),
                order.getUser().getUsername(),
                itemsStr.toString(),
                String.format("$%.2f", order.getTotalPrice()),
                order.getOrderTime().format(formatter)
            });
        }
    }
} 