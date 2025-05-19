package projeto.es.models;

public abstract class NamedEntity extends Entity {
    private String name;

    public NamedEntity(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void SetName(String name) {
        this.name = name;
    }
}
