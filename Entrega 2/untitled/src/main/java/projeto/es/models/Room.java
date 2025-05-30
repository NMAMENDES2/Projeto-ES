package projeto.es.models;

public class Room extends Entity {
    private String name;
    private int rows;
    private int columns;
    private boolean is3D;

    public Room(String name, int rows, int columns, boolean is3D) {
        super();
        this.name = name;
        this.rows = rows;
        this.columns = columns;
        this.is3D = is3D;
    }

    // Getters
    public String getName() { return name; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public boolean is3D() { return is3D; }
    public int getCapacity() { return rows * columns; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setRows(int rows) { this.rows = rows; }
    public void setColumns(int columns) { this.columns = columns; }
    public void set3D(boolean is3D) { this.is3D = is3D; }

    @Override
    public String toString() {
        return String.format("%s (%dx%d)%s", 
            name, rows, columns, 
            is3D ? " - 3D" : ""
        );
    }
}
