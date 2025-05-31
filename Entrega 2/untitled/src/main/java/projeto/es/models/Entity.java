package projeto.es.models;

public abstract class Entity {
    private static int nextId = 1;
    private int id;

    protected Entity() {
        this.id = nextId++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }
}
