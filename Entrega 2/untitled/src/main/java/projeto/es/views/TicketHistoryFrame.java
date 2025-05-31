package projeto.es.views;

import projeto.es.models.User;
import projeto.es.models.Ticket;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class TicketHistoryFrame extends JFrame {
    private final User user;
    private DefaultListModel<String> ticketListModel;
    private JList<String> ticketList;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public TicketHistoryFrame(User user) {
        super("Ticket History - " + user.getUsername());
        this.user = user;
        
        setLayout(new BorderLayout(10, 10));
        
        // Create header
        JLabel headerLabel = new JLabel("Your Tickets", SwingConstants.CENTER);
        headerLabel.setFont(new Font(headerLabel.getFont().getName(), Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);
        
        // Create ticket list
        ticketListModel = new DefaultListModel<>();
        ticketList = new JList<>(ticketListModel);
        ticketList.setCellRenderer(new TicketListCellRenderer());
        
        // Add tickets to the list
        refreshTicketList();
        
        // Add list to scroll pane
        JScrollPane scrollPane = new JScrollPane(ticketList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void refreshTicketList() {
        ticketListModel.clear();
        List<Ticket> sortedTickets = new ArrayList<>(user.getTickets());
        sortedTickets.sort((t1, t2) -> t1.getSession().getStartTime().compareTo(t2.getSession().getStartTime()));
        
        LocalDateTime now = LocalDateTime.now();
        
        for (Ticket ticket : sortedTickets) {
            LocalDateTime showtime = ticket.getSession().getStartTime();
            String status = showtime.isAfter(now) ? "Upcoming" : "Past";
            String timeUntil = showtime.isAfter(now) ? 
                getTimeUntilShow(showtime) : "Already shown";
            
            ticketListModel.addElement(String.format("%s | %s | %s | Seat: %c%d | %s",
                ticket.getSession().getMovie().getName(),
                showtime.format(formatter),
                ticket.getSession().getRoom().getName(),
                (char)('A' + ticket.getRow()),
                ticket.getColumn() + 1,
                timeUntil
            ));
        }
    }
    
    private String getTimeUntilShow(LocalDateTime showtime) {
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(now, showtime).toDays();
        if (days > 0) {
            return "In " + days + " day" + (days > 1 ? "s" : "");
        }
        long hours = java.time.Duration.between(now, showtime).toHours();
        if (hours > 0) {
            return "In " + hours + " hour" + (hours > 1 ? "s" : "");
        }
        long minutes = java.time.Duration.between(now, showtime).toMinutes();
        return "In " + minutes + " minute" + (minutes > 1 ? "s" : "");
    }
    
    private class TicketListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
            // Set different background colors for upcoming and past shows
            String text = value.toString();
            if (!isSelected) {
                if (text.contains("Already shown")) {
                    label.setBackground(new Color(245, 245, 245)); // Light gray for past shows
                } else {
                    label.setBackground(new Color(240, 255, 240)); // Light green for upcoming shows
                }
            }
            
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return label;
        }
    }
} 