package projeto.es.models;

import java.util.LinkedList;

public class Ticket extends Entity{
    public double price;
    public Session session;
    public LinkedList<User> holders;

    public Ticket(int id, double price, Session session) {
        super(id);
        this.price = price;
        this.holders = new LinkedList<>();
        this.session = session;
    }

    public void addHolder(User user){
        holders.add(user);
    }

    public LinkedList<User> getHolders(){
        return holders;
    }

    @Override
    public String toString() {
        return "Ticket de avengers";
    }
}
