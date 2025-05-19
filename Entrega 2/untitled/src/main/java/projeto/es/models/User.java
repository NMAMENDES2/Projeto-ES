package projeto.es.models;

import java.util.LinkedList;

public class User extends NamedEntity {

    private String email;
    private LinkedList<Ticket> tickets;

    public User(int id, String name) {
        super(id, name);
        this.tickets = new LinkedList<Ticket>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Isto vai para os controllers
    protected void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
