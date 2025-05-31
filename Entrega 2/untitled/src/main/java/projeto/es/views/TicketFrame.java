package projeto.es.views;

import projeto.es.models.*;

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
                "Room 1",
                6, 
                8, 
                false
            );
        });

        setLayout(new BorderLayout());
        add(addButton, BorderLayout.NORTH);
        add(new JScrollPane(ticketList), BorderLayout.CENTER);

        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
