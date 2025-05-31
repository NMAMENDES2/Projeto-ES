package projeto.es.Repository;

import projeto.es.models.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDatabase {
    private static final String FILE_PATH = "rooms.txt";

    public static void saveRoom(Room room) {
        System.out.println("DEBUG: Saving new room with ID " + room.getId());
        System.out.println("DEBUG: Room: " + room.getName());
        
        List<Room> rooms = getAllRooms();
        System.out.println("DEBUG: Existing rooms: " + rooms.size());
        rooms.add(room);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Room r : rooms) {
                String line = String.format("%d,%s,%d,%d,%b",
                    r.getId(),
                    r.getName(),
                    r.getRows(),
                    r.getColumns(),
                    r.is3D());
                System.out.println("DEBUG: Writing room: " + line);
                writer.println(line);
            }
            System.out.println("DEBUG: Rooms saved successfully");
        } catch (IOException e) {
            System.err.println("ERROR saving room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Room> getAllRooms() {
        System.out.println("DEBUG: Reading rooms from " + new File(FILE_PATH).getAbsolutePath());
        List<Room> rooms = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            System.out.println("DEBUG: Rooms file does not exist");
            return rooms;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    System.out.println("DEBUG: Reading line: " + line);
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        try {
                            Room room = new Room(
                                parts[1],
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]),
                                Boolean.parseBoolean(parts[4])
                            );
                            room.setId(Integer.parseInt(parts[0]));
                            rooms.add(room);
                            System.out.println("DEBUG: Loaded room: " + room.getId() + " - " + room.getName());
                        } catch (Exception e) {
                            System.err.println("ERROR parsing room line: " + line);
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR reading rooms: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG: Returning " + rooms.size() + " rooms");
        return rooms;
    }

    public static Room getRoomById(int id) {
        System.out.println("DEBUG: Looking for room with ID: " + id);
        Room room = getAllRooms().stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
        System.out.println("DEBUG: Found room: " + (room != null ? room.getName() : "null"));
        return room;
    }

    public static void updateRoom(Room updatedRoom) {
        List<Room> rooms = getAllRooms();
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId() == updatedRoom.getId()) {
                rooms.set(i, updatedRoom);
                break;
            }
        }
        
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