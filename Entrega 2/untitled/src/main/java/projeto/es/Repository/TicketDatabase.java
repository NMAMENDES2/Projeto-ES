package projeto.es.Repository;

import projeto.es.models.Ticket;
import projeto.es.models.User;
import projeto.es.models.Session;
import projeto.es.models.OrderItem;
import projeto.es.models.Product;

import java.io.*;
import java.util.*;

public class TicketDatabase {
    private static final String FILE_PATH = "tickets.txt";
    private static final String ORDERS_FILE_PATH = "ticket_orders.txt";
    private static Map<String, List<Ticket>> userTickets = new HashMap<>();

    public static void saveTicket(Ticket ticket) {
        String username = getCurrentUsername();
        if (username == null) return;

        List<Ticket> tickets = userTickets.getOrDefault(username, new ArrayList<>());
        tickets.add(ticket);
        userTickets.put(username, tickets);
        saveAllTickets();
    }

    public static void removeTicket(Ticket ticket) {
        String username = getCurrentUsername();
        if (username == null) return;

        List<Ticket> tickets = userTickets.getOrDefault(username, new ArrayList<>());
        tickets.removeIf(t -> 
            t.getSession().getId() == ticket.getSession().getId() &&
            t.getRow() == ticket.getRow() &&
            t.getColumn() == ticket.getColumn()
        );
        userTickets.put(username, tickets);
        saveAllTickets();
    }

    public static List<Ticket> getTicketsForUser(String username) {
        loadTickets();
        return userTickets.getOrDefault(username, new ArrayList<>());
    }

    private static String getCurrentUsername() {
        User currentUser = User.getCurrentUser();
        return currentUser != null ? currentUser.getUsername() : null;
    }

    private static void loadTickets() {
        userTickets.clear();
        File file = new File(FILE_PATH);
        File ordersFile = new File(ORDERS_FILE_PATH);
        
        if (!file.exists()) {
            return;
        }
        
        // Load base tickets
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        // Format: username|sessionId|row|column
                        String username = parts[0];
                        int sessionId = Integer.parseInt(parts[1]);
                        int row = Integer.parseInt(parts[2]);
                        int column = Integer.parseInt(parts[3]);
                        
                        Session session = SessionDatabase.getSessionById(sessionId);
                        if (session != null) {
                            Ticket ticket = new Ticket(session, row, column);
                            List<Ticket> tickets = userTickets.getOrDefault(username, new ArrayList<>());
                            tickets.add(ticket);
                            userTickets.put(username, tickets);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }

        // Load orders if they exist
        if (ordersFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(ordersFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split("\\|");
                        if (parts.length == 6) {
                            // Format: username|sessionId|row|column|productId|quantity
                            String username = parts[0];
                            int sessionId = Integer.parseInt(parts[1]);
                            int row = Integer.parseInt(parts[2]);
                            int column = Integer.parseInt(parts[3]);
                            String productId = parts[4];
                            int quantity = Integer.parseInt(parts[5]);

                            // Find the corresponding ticket
                            List<Ticket> userTicketList = userTickets.get(username);
                            if (userTicketList != null) {
                                for (Ticket ticket : userTicketList) {
                                    if (ticket.getSession().getId() == sessionId &&
                                        ticket.getRow() == row &&
                                        ticket.getColumn() == column) {
                                        
                                        Product product = ProductDatabase.getProduct(productId);
                                        if (product != null) {
                                            ticket.addOrderItem(new OrderItem(product, quantity));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading ticket orders: " + e.getMessage());
            }
        }
    }

    private static void saveAllTickets() {
        // Save base tickets
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<String, List<Ticket>> entry : userTickets.entrySet()) {
                String username = entry.getKey();
                for (Ticket ticket : entry.getValue()) {
                    writer.println(String.format("%s|%d|%d|%d",
                        username,
                        ticket.getSession().getId(),
                        ticket.getRow(),
                        ticket.getColumn()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving tickets: " + e.getMessage());
        }

        // Save orders
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE_PATH))) {
            for (Map.Entry<String, List<Ticket>> entry : userTickets.entrySet()) {
                String username = entry.getKey();
                for (Ticket ticket : entry.getValue()) {
                    for (OrderItem order : ticket.getOrderItems()) {
                        writer.println(String.format("%s|%d|%d|%d|%s|%d",
                            username,
                            ticket.getSession().getId(),
                            ticket.getRow(),
                            ticket.getColumn(),
                            order.getProduct().getId(),
                            order.getQuantity()
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving ticket orders: " + e.getMessage());
        }
    }
} 