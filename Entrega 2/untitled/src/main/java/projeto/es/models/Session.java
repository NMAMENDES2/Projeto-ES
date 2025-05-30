package projeto.es.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Session extends Entity {
    private Movie movie;
    private Room room;
    private LocalDateTime startTime;
    private double price;
    private boolean[][] occupiedSeats;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public Session(Movie movie, Room room, LocalDateTime startTime, double price) {
        super();
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.price = price;
        this.occupiedSeats = new boolean[room.getRows()][room.getColumns()];
    }

    // Getters
    public Movie getMovie() { return movie; }
    public Room getRoom() { return room; }
    public LocalDateTime getStartTime() { return startTime; }
    public double getPrice() { return price; }
    public boolean[][] getOccupiedSeats() { return occupiedSeats; }

    // Setters
    public void setMovie(Movie movie) { this.movie = movie; }
    public void setRoom(Room room) { this.room = room; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setPrice(double price) { this.price = price; }

    // Seat management
    public boolean isSeatOccupied(int row, int col) {
        return occupiedSeats[row][col];
    }

    public void occupySeat(int row, int col) {
        occupiedSeats[row][col] = true;
    }

    public void freeSeat(int row, int col) {
        occupiedSeats[row][col] = false;
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