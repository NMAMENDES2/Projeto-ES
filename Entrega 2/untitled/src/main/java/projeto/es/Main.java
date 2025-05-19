package projeto.es;

import projeto.es.models.Ticket;
import projeto.es.models.User;

public class Main {
    public static void main(String[] args) {
        User teste1 = new User(1, "Nuno");
        User teste2 = new User(2, "OP");

        Ticket ticket1 = new Ticket(1, 12);
        Ticket ticket2 = new Ticket(1, 12);

        teste1.addTicket(ticket1);
        teste2.addTicket(ticket2);
    }
}