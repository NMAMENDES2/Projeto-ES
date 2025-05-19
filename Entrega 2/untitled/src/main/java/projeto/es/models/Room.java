package projeto.es.models;

public class Room extends NamedEntity{
    private int capacity;
    private String soundLevel;
    private String seatType;
    private String acessibility;

    public Room(int id, String name, int capacity){
        super(id, name);
        this.capacity = capacity;
        this.soundLevel = "";
        this.seatType = "";
        this.acessibility = "";
    }

    public int getCapacity() {
        return capacity;
    }
}
