package projeto.es.models;

import java.util.LinkedList;

public class Session extends Entity {
    public boolean full;
    public Room room;
    public LinkedList<Ticket> tickets; // ?
    public String sessionStart;

    public Session(int id, Room room){
        super(id);
        full = false;
        tickets = new LinkedList<>();
        sessionStart = "";
        this.room = room;
    }

    public boolean isFull(){
        return full;
    }

    public void addTicket(Ticket ticket){
        if (tickets.size() > room.getCapacity()){
            full = true;
            return;
        }
        tickets.add(ticket);
    }
}