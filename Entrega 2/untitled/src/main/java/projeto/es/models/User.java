package projeto.es.models;

import java.util.LinkedList;

public class User extends NamedEntity {

    private String email;
    private String password;
    private LinkedList<Ticket> tickets;

    public User(int id, String name, String password) {
        super(id, name);
        this.tickets = new LinkedList<Ticket>();
        this.password = password;
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


    // Isto vai para os controllers
    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }
}
