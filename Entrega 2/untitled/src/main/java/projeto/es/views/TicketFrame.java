package projeto.es.views;

import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.models.User;

import javax.swing.*;
import java.awt.*;

public class TicketFrame extends JFrame {
    private User user;
    private DefaultListModel<String> ticketListModel = new DefaultListModel<>();
    private JList<String> ticketList = new JList<>(ticketListModel);

    public TicketFrame(User user) {
        super("Tickets for " + user.getUsername());
        this.user = user;

        JButton addButton = new JButton("Add Ticket");
        addButton.addActionListener(e -> {
            Room room = new Room(
                "room1", 
                "Room 1", 
                6, 
                8, 
                false
            );
            Session session = new Session(
                "session1",
                "movie1",
                room.getId(),
                java.time.LocalDateTime.now(),
                room.getRows(),
                room.getColumns()
            );
            Ticket ticket = new Ticket(1, session, 1, 1);
            user.addTicket(ticket);
            ticketListModel.addElement(ticket.toString());
        });

        setLayout(new BorderLayout());
        add(addButton, BorderLayout.NORTH);
        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
