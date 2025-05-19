package projeto.es.controllers;

import projeto.es.Exceptions.NoSessionException;
import projeto.es.Exceptions.RoomCapacityExtendedException;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.models.User;

public class TicketController {
    public void assignTicket(Ticket ticket, User user) {
        if(ticket.session == null){
            throw new NoSessionException("No session is active");
        }
        if(ticket.session.full){
            throw new RoomCapacityExtendedException("Session is already full");
        }

        ticket.addHolder(user);
        ticket.session.addTicket(ticket);
        user.addTicket(ticket);
    }
}
