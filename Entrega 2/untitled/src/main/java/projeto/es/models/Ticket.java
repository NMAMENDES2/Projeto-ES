package projeto.es.models;

import java.util.LinkedList;

public class Ticket extends Entity {
    private Session session;
    private int row;
    private int column;
    private User user;

    public Ticket(Session session, int row, int column, User user) {
        super();
        this.session = session;
        this.row = row;
        this.column = column;
        this.user = user;
        session.occupySeat(row, column);
    }

    public Session getSession() {
        return session;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public User getUser() {
        return user;
    }

    public double getPrice() {
        return session.getPrice();
    }

    public void addHolder(User user) {
        // This method is not used in the new implementation
    }

    public LinkedList<User> getHolders() {
        // This method is not used in the new implementation
        return null;
    }

    public void setSession(Session session) {
        this.session.freeSeat(row, column);
        this.session = session;
        this.session.occupySeat(row, column);
    }

    public void setRow(int row) {
        this.session.freeSeat(this.row, this.column);
        this.row = row;
        this.session.occupySeat(row, this.column);
    }

    public void setColumn(int column) {
        this.session.freeSeat(this.row, this.column);
        this.column = column;
        this.session.occupySeat(this.row, column);
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("Ticket #%d - %s - Row: %d, Seat: %d - $%.2f", 
            getId(),
            session.toString(),
            row + 1,
            column + 1,
            getPrice()
        );
    }
}
