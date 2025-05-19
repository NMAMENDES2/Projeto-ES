package projeto.es.views;

import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.models.Ticket;
import projeto.es.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TicketFrame extends JFrame {
    private User user;
    private DefaultListModel<String> ticketListModel = new DefaultListModel<>();
    private JList<String> ticketList = new JList<>(ticketListModel);

    public TicketFrame(User user) {
        super("Tickets for " + user.getName());
        this.user = user;

        JButton addButton = new JButton("Add Ticket");
        addButton.addActionListener(e -> {
            Room room = new Room("Sala1", 12);
            Session session = new Session(room);
            Ticket ticket = new Ticket(1, session);
            user.addTicket(ticket);
            ticketListModel.addElement(ticket.toString());
        });

        setLayout(new BorderLayout());
        add(addButton, BorderLayout.NORTH);
        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
