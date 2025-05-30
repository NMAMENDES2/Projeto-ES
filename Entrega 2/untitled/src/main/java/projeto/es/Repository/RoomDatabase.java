package projeto.es.Repository;

import projeto.es.models.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDatabase {
    private static final String FILE_PATH = "rooms.txt";

    public static void saveRoom(Room room) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(String.format("%d,%s,%d,%d,%b",
                    room.getId(),
                    room.getName(),
                    room.getRows(),
                    room.getColumns(),
                    room.is3D()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Room room = new Room(
                            parts[1], // name
                            Integer.parseInt(parts[2]), // rows
                            Integer.parseInt(parts[3]), // columns
                            Boolean.parseBoolean(parts[4]) // is3D
                    );
                    rooms.add(room);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static Room getRoomById(int id) {
        return getAllRooms().stream()
                .filter(room -> room.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void updateRoom(Room updatedRoom) {
        List<Room> rooms = getAllRooms();
        List<Room> updatedRooms = new ArrayList<>();
        
        for (Room room : rooms) {
            if (room.getId() == updatedRoom.getId()) {
                updatedRooms.add(updatedRoom);
            } else {
                updatedRooms.add(room);
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Room room : updatedRooms) {
                writer.println(String.format("%d,%s,%d,%d,%b",
                        room.getId(),
                        room.getName(),
                        room.getRows(),
                        room.getColumns(),
                        room.is3D()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRoom(int id) {
        List<Room> rooms = getAllRooms();
        rooms.removeIf(room -> room.getId() == id);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Room room : rooms) {
                writer.println(String.format("%d,%s,%d,%d,%b",
                        room.getId(),
                        room.getName(),
                        room.getRows(),
                        room.getColumns(),
                        room.is3D()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 