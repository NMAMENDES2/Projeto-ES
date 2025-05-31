package projeto.es.models;

import projeto.es.Repository.TicketDatabase;
import java.util.List;

public class User {
    private String name;
    private String password;
    private boolean isAdmin;
    private static User currentUser;

    public User(String name, String password, boolean isAdmin) {
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return name;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public List<Ticket> getTickets() {
        return TicketDatabase.getTicketsForUser(name);
    }

    public void addTicket(Ticket ticket) {
        TicketDatabase.saveTicket(ticket);
    }

    public void removeTicket(Ticket ticket) {
        TicketDatabase.removeTicket(ticket);
    }
}
