package projeto.es.models;

import java.util.ArrayList;
import java.util.List;

public class Ticket {
    private Session session;
    private int row;
    private int column;
    private List<OrderItem> orderItems;
    private double totalPrice;

    public Ticket(Session session, int row, int column) {
        this.session = session;
        this.row = row;
        this.column = column;
        this.orderItems = new ArrayList<>();
        this.totalPrice = session.getPrice(); // Initialize with session price
    }

    public Session getSession() {
        return session;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        totalPrice += item.getTotalPrice();
    }

    public void removeOrderItem(OrderItem item) {
        if (orderItems.remove(item)) {
            totalPrice -= item.getTotalPrice();
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getSessionPrice() {
        return session.getPrice();
    }

    public double getProductsTotal() {
        return orderItems.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Movie: %s\n", session.getMovie().getName()));
        sb.append(String.format("Session: %s\n", session.getStartTime()));
        sb.append(String.format("Seat: %c%d\n", (char)('A' + row), column + 1));
        sb.append(String.format("Ticket Price: $%.2f\n", session.getPrice()));
        
        if (!orderItems.isEmpty()) {
            sb.append("\nConcessions:\n");
            for (OrderItem item : orderItems) {
                sb.append(String.format("- %s\n", item.toString()));
            }
            sb.append(String.format("\nProducts Total: $%.2f", getProductsTotal()));
        }
        
        sb.append(String.format("\nTotal Price: $%.2f", totalPrice));
        return sb.toString();
    }
}
