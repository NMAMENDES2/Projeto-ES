package projeto.es.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class ProductOrder {
    private String id;
    private User user;
    private List<OrderItem> items;
    private double totalPrice;
    private LocalDateTime orderTime;

    public ProductOrder(String id, User user, List<OrderItem> items, double totalPrice) {
        this.id = id;
        this.user = user;
        this.items = new ArrayList<>(items);
        this.totalPrice = totalPrice;
        this.orderTime = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderTime() { return orderTime; }
} 