package projeto.es.Repository;

import projeto.es.models.ProductOrder;
import projeto.es.models.OrderItem;
import projeto.es.models.User;
import projeto.es.models.Product;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductOrderDatabase {
    private static final String ORDERS_FILE = "data/product_orders.txt";
    private static Map<String, ProductOrder> orders = new HashMap<>();
    private static boolean isInitialized = false;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void initialize() {
        if (isInitialized) return;
        
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(ORDERS_FILE);
        if (file.exists()) {
            loadOrders();
        }
        isInitialized = true;
    }

    private static void loadOrders() {
        orders.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or whitespace-only lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        String id = parts[0].trim();
                        String userId = parts[1].trim();
                        double totalPrice = Double.parseDouble(parts[2].trim());
                        LocalDateTime orderTime = LocalDateTime.parse(parts[3].trim(), formatter);
                        
                        // Parse items
                        List<OrderItem> items = new ArrayList<>();
                        String[] itemStrings = parts[4].split(";");
                        for (String itemStr : itemStrings) {
                            if (itemStr.trim().isEmpty()) continue;
                            
                            String[] itemParts = itemStr.split(",");
                            if (itemParts.length == 2) {
                                String productId = itemParts[0].trim();
                                int quantity = Integer.parseInt(itemParts[1].trim());
                                Product product = ProductDatabase.getProduct(productId);
                                if (product != null) {
                                    items.add(new OrderItem(product, quantity));
                                }
                            }
                        }
                        
                        User user = UserDatabase.getUserByUsername(userId);
                        if (user != null && !items.isEmpty()) {
                            ProductOrder order = new ProductOrder(id, user, items, totalPrice);
                            orders.put(id, order);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing order line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                    // Continue processing other lines
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders file: " + e.getMessage());
        }
    }

    public static void saveOrders() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (ProductOrder order : orders.values()) {
                // Format items as productId,quantity;productId,quantity;...
                StringBuilder itemsStr = new StringBuilder();
                for (OrderItem item : order.getItems()) {
                    if (itemsStr.length() > 0) itemsStr.append(";");
                    itemsStr.append(item.getProduct().getId())
                           .append(",")
                           .append(item.getQuantity());
                }
                
                writer.println(String.format("%s|%s|%.2f|%s|%s",
                    order.getId(),
                    order.getUser().getUsername(),
                    order.getTotalPrice(),
                    order.getOrderTime().format(formatter),
                    itemsStr.toString()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }

    public static void addOrder(ProductOrder order) {
        initialize();
        orders.put(order.getId(), order);
        saveOrders();
    }

    public static List<ProductOrder> getAllOrders() {
        initialize();
        return new ArrayList<>(orders.values());
    }

    public static List<ProductOrder> getOrdersForUser(String username) {
        initialize();
        return orders.values().stream()
            .filter(order -> order.getUser().getUsername().equals(username))
            .toList();
    }
} 