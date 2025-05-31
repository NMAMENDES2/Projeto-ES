package projeto.es.Repository;

import projeto.es.models.Product;
import java.io.*;
import java.util.*;

public class ProductDatabase {
    private static final String PRODUCTS_FILE = "data/products.txt";
    private static Map<String, Product> products = new HashMap<>();
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) return;
        
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) {
            // Initialize with some default products
            createDefaultProducts();
        } else {
            loadProducts();
        }
        isInitialized = true;
    }

    private static void createDefaultProducts() {
        List<Product> defaultProducts = Arrays.asList(
            new Product("DRINK1", "Coca-Cola", 2.99, 100, "Drinks", "Regular 500ml", 
                new HashSet<>(), 0),
            new Product("DRINK2", "Beer", 4.99, 50, "Drinks", "330ml", 
                new HashSet<>(), 18),
            new Product("DRINK3", "Orange Juice", 3.99, 80, "Drinks", "Fresh squeezed 400ml", 
                new HashSet<>(), 0),
            new Product("SNACK1", "Popcorn", 4.99, 100, "Snacks", "Large bucket", 
                new HashSet<>(Arrays.asList("Corn")), 0),
            new Product("SNACK2", "Peanuts", 3.99, 50, "Snacks", "Salted 200g", 
                new HashSet<>(Arrays.asList("Peanuts", "Tree Nuts")), 0),
            new Product("SNACK3", "Chocolate Bar", 2.99, 80, "Snacks", "Milk chocolate", 
                new HashSet<>(Arrays.asList("Milk", "Soy")), 0),
            new Product("COMBO1", "Movie Combo", 9.99, 50, "Combos", "Large popcorn + Regular drink", 
                new HashSet<>(Arrays.asList("Corn")), 0)
        );

        for (Product product : defaultProducts) {
            products.put(product.getId(), product);
        }
        saveProducts();
    }

    private static void loadProducts() {
        products.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Set<String> allergens = new HashSet<>();
                    if (!parts[6].isEmpty()) {
                        allergens.addAll(Arrays.asList(parts[6].split(",")));
                    }
                    
                    Product product = new Product(
                        parts[0], // id
                        parts[1], // name
                        Double.parseDouble(parts[2]), // price
                        Integer.parseInt(parts[3]), // stock
                        parts[4], // category
                        parts[5], // description
                        allergens, // allergens
                        Integer.parseInt(parts[7]) // minimumAge
                    );
                    products.put(product.getId(), product);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
    }

    public static void saveProducts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products.values()) {
                String allergens = String.join(",", product.getAllergens());
                writer.println(String.format("%s|%s|%.2f|%d|%s|%s|%s|%d",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCategory(),
                    product.getDescription(),
                    allergens,
                    product.getMinimumAge()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    public static List<Product> getAllProducts() {
        initialize();
        return new ArrayList<>(products.values());
    }

    public static List<Product> getProductsByCategory(String category) {
        initialize();
        return products.values().stream()
            .filter(p -> p.getCategory().equals(category))
            .toList();
    }

    public static Product getProduct(String id) {
        initialize();
        return products.get(id);
    }

    public static boolean updateProductStock(String id, int quantity) {
        initialize();
        Product product = products.get(id);
        if (product != null) {
            if (quantity < 0 && !product.decreaseStock(-quantity)) {
                return false;
            }
            if (quantity > 0) {
                product.increaseStock(quantity);
            }
            saveProducts();
            return true;
        }
        return false;
    }

    public static void saveProduct(Product product) {
        products.put(product.getId(), product);
        saveProducts();
    }

    public static void deleteProduct(String id) {
        products.remove(id);
        saveProducts();
    }
} 