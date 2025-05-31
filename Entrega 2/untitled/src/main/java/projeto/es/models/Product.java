package projeto.es.models;

import java.util.Set;
import java.util.HashSet;

public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;
    private String category; // e.g., "Drinks", "Snacks", "Combos"
    private String description;
    private Set<String> allergens;
    private int minimumAge;

    public Product(String id, String name, double price, int stock, String category, String description, Set<String> allergens, int minimumAge) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.description = description;
        this.allergens = new HashSet<>(allergens);
        this.minimumAge = minimumAge;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public Set<String> getAllergens() { return new HashSet<>(allergens); }
    public int getMinimumAge() { return minimumAge; }

    // Setters
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setDescription(String description) { this.description = description; }
    public void setAllergens(Set<String> allergens) { this.allergens = new HashSet<>(allergens); }
    public void setMinimumAge(int minimumAge) { this.minimumAge = minimumAge; }

    // Stock management
    public boolean decreaseStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            return true;
        }
        return false;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public boolean hasAllergen(String allergen) {
        return allergens.contains(allergen);
    }

    public boolean isAgeRestricted() {
        return minimumAge > 0;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
} 