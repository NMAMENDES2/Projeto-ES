package projeto.es.models;

public class Ticket extends Entity{
    public double price;

    public Ticket(int id, double price) {
        super(id);
        this.price = price;
    }

}
