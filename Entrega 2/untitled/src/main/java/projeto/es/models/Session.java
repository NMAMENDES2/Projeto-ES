package projeto.es.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Session extends Entity {
    private Movie movie;
    private Room room;
    private LocalDateTime startTime;
    private double price;
    private boolean[][] occupiedSeats;
    private boolean[][] accessibilitySeats;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public Session(Movie movie, Room room, LocalDateTime startTime, double price) {
        super();
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.price = price;
        this.occupiedSeats = new boolean[room.getRows()][room.getColumns()];
        this.accessibilitySeats = new boolean[room.getRows()][room.getColumns()];
        
        setupAccessibilitySeats();
    }

    private void setupAccessibilitySeats() {
        int rows = room.getRows();
        int cols = room.getColumns();
        
        for (int col = 0; col < cols; col++) {
            accessibilitySeats[rows-1][col] = true;
        }
        
        int middleCol = cols / 2;
        for (int row = 0; row < rows; row++) {
            if (middleCol > 0) accessibilitySeats[row][middleCol-1] = true;
            if (middleCol < cols) accessibilitySeats[row][middleCol] = true;
        }
    }

    // Getters
    public Movie getMovie() { return movie; }
    public Room getRoom() { return room; }
    public LocalDateTime getStartTime() { return startTime; }
    public double getPrice() { return price; }
    public boolean[][] getOccupiedSeats() { return occupiedSeats; }
    public boolean[][] getAccessibilitySeats() { return accessibilitySeats; }

    // Setters
    public void setMovie(Movie movie) { this.movie = movie; }
    public void setRoom(Room room) { 
        this.room = room; 
        this.occupiedSeats = new boolean[room.getRows()][room.getColumns()];
        this.accessibilitySeats = new boolean[room.getRows()][room.getColumns()];
        setupAccessibilitySeats();
    }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setPrice(double price) { this.price = price; }

    // Seat management
    public boolean isSeatOccupied(int row, int col) {
        return occupiedSeats[row][col];
    }

    public boolean isAccessibilitySeat(int row, int col) {
        return accessibilitySeats[row][col];
    }

    public void occupySeat(int row, int col) {
        occupiedSeats[row][col] = true;
    }

    public void freeSeat(int row, int col) {
        occupiedSeats[row][col] = false;
    }

    public boolean isSeatValid(int row, int col) {
        return row >= 0 && row < room.getRows() && 
               col >= 0 && col < room.getColumns();
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s%s) - $%.2f", 
            movie.getName(),
            startTime.format(DISPLAY_FORMATTER),
            room.getName(),
            room.is3D() ? " - 3D" : "",
            price
        );
    }
}