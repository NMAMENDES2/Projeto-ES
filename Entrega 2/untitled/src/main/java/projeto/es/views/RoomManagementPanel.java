package projeto.es.views;

import projeto.es.models.Room;
import projeto.es.Repository.RoomDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private DefaultListModel<Room> roomListModel;
    private JList<Room> roomList;

    public RoomManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Room list
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Room room = (Room) value;
                setText(room.getName() + " (" + room.getRows() + "x" + room.getColumns() + ")");
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> {
            Room selected = roomList.getSelectedValue();
            if (selected != null) {
                editRoom(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a room to edit",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> {
            Room selected = roomList.getSelectedValue();
            if (selected != null) {
                deleteRoom(selected);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Please select a room to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        refreshButton.addActionListener(e -> refreshRoomList());

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Initial load
        refreshRoomList();
    }

    private void refreshRoomList() {
        roomListModel.clear();
        List<Room> rooms = RoomDatabase.getAllRooms();
        for (Room room : rooms) {
            roomListModel.addElement(room);
        }
    }

    private void addRoom() {
        RoomDialog dialog = new RoomDialog(null);
        dialog.setVisible(true);
        refreshRoomList();
    }

    private void editRoom(Room room) {
        RoomDialog dialog = new RoomDialog(room);
        dialog.setVisible(true);
        refreshRoomList();
    }

    private void deleteRoom(Room room) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this room?\n" + room.getName(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            RoomDatabase.deleteRoom(room.getId());
            refreshRoomList();
        }
    }
} 