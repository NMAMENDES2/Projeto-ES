package projeto.es.views;

import projeto.es.models.Session;
import projeto.es.Repository.SessionDatabase;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionManagementPanel extends JPanel {
    private DefaultListModel<Session> sessionListModel;
    private JList<Session> sessionList;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SessionManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Session list
        sessionListModel = new DefaultListModel<>();
        sessionList = new JList<>(sessionListModel);
        sessionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Session session = (Session) value;
                setText(String.format("%s - %s - %s - $%.2f",
                    session.getMovie().getName(),
                    session.getRoom().getName(),
                    session.getStartTime().format(formatter),
                    session.getPrice()));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(sessionList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Session");
        JButton editButton = new JButton("Edit Session");
        JButton deleteButton = new JButton("Delete Session");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addSession());
        editButton.addActionListener(e -> {
            Session selected = sessionList.getSelectedValue();
            if (selected != null) {
                editSession(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a session to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            Session selected = sessionList.getSelectedValue();
            if (selected != null) {
                deleteSession(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a session to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshSessionList());

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Initial load
        refreshSessionList();
    }

    private void refreshSessionList() {
        sessionListModel.clear();
        List<Session> sessions = SessionDatabase.getAllSessions();
        for (Session session : sessions) {
            sessionListModel.addElement(session);
        }
    }

    private void addSession() {
        SessionDialog dialog = new SessionDialog(null);
        dialog.setVisible(true);
        refreshSessionList();
    }

    private void editSession(Session session) {
        SessionDialog dialog = new SessionDialog(session);
        dialog.setVisible(true);
        refreshSessionList();
    }

    private void deleteSession(Session session) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Are you sure you want to delete this session?\n%s - %s - %s",
                session.getMovie().getName(),
                session.getRoom().getName(),
                session.getStartTime().format(formatter)),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            SessionDatabase.deleteSession(session.getId());
            refreshSessionList();
        }
    }
} 