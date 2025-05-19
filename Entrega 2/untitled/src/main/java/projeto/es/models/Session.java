package projeto.es.models;

import java.util.LinkedList;

public class Session extends Entity {
    public boolean full;
    public Room room;
    public LinkedList<Ticket> tickets; // ?
    public String sessionStart;

    public Session(int id){
        super(id);
        full = false;
        room = null;
        tickets = new LinkedList<>();
        sessionStart = "";
    }

    public void isFull(){
        if(tickets.size() >= room.getCapacity()){
            full = true;
        }
    }

    public void addTicket(Ticket ticket){
        if(this.full){
            tickets.add(ticket);
        }
        throw new IllegalStateException("Session is already full");
    }
}
