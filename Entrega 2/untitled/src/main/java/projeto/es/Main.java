package projeto.es;

import projeto.es.controllers.TicketController;
import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.models.User;

public class Main {
    public static void main(String[] args) {
        TicketController ticketController = new TicketController();

        User user = new User(1, "P", "12333");
        Room room = new Room(1, "N", 2);
        Session session = new Session(1, room);
        Ticket ticket = new Ticket(1, 12, session);

        ticketController.assignTicket(ticket, user);

    }
}