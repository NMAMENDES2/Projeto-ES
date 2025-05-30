package projeto.es.models;

import java.util.LinkedList;

public class User extends NamedEntity {

    private String email;
    private String password;
    private LinkedList<Ticket> tickets;
    private boolean isAdmin;

    public User(String name, String password, boolean isAdmin) {
        super(name);
        this.tickets = new LinkedList<Ticket>();
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return getName();
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // Isto vai para os controllers
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
