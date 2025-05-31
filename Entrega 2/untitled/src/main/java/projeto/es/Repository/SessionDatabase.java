package projeto.es.Repository;

import projeto.es.models.Movie;
import projeto.es.models.Room;
import projeto.es.models.Session;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

public class SessionDatabase {
    private static final String FILE_PATH = "sessions.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void saveSession(Session session) {
        List<Session> sessions = getAllSessions();
        
        // Update existing session if it exists
        boolean found = false;
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getId() == session.getId()) {
                sessions.set(i, session);
                found = true;
                break;
            }
        }
        
        // Add new session if it doesn't exist
        if (!found) {
            sessions.add(session);
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Session s : sessions) {
                // Convert occupied seats to string
                String occupiedSeats = serializeSeats(s.getOccupiedSeats());
                
                writer.println(String.format("%d,%d,%d,%s,%.2f,%s",
                    s.getId(),
                    s.getMovie().getId(),
                    s.getRoom().getId(),
                    s.getStartTime().format(formatter),
                    s.getPrice(),
                    occupiedSeats));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String serializeSeats(boolean[][] seats) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : seats) {
            for (boolean seat : row) {
                sb.append(seat ? '1' : '0');
            }
        }
        return Base64.getEncoder().encodeToString(sb.toString().getBytes());
    }

    private static boolean[][] deserializeSeats(String data, int rows, int cols) {
        try {
            String decoded = new String(Base64.getDecoder().decode(data));
            boolean[][] seats = new boolean[rows][cols];
            int index = 0;
            for (int i = 0; i < rows && index < decoded.length(); i++) {
                for (int j = 0; j < cols && index < decoded.length(); j++) {
                    seats[i][j] = decoded.charAt(index++) == '1';
                }
            }
            return seats;
        } catch (Exception e) {
            return new boolean[rows][cols];
        }
    }

    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return sessions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        Movie movie = MovieDatabase.getMovieById(Integer.parseInt(parts[1]));
                        Room room = RoomDatabase.getRoomById(Integer.parseInt(parts[2]));
                        if (movie != null && room != null) {
                            Session session = new Session(
                                movie,
                                room,
                                LocalDateTime.parse(parts[3], formatter),
                                Double.parseDouble(parts[4])
                            );
                            session.setId(Integer.parseInt(parts[0]));
                            
                            // Load occupied seats
                            boolean[][] occupiedSeats = deserializeSeats(parts[5], room.getRows(), room.getColumns());
                            for (int i = 0; i < occupiedSeats.length; i++) {
                                for (int j = 0; j < occupiedSeats[i].length; j++) {
                                    if (occupiedSeats[i][j]) {
                                        session.occupySeat(i, j);
                                    }
                                }
                            }
                            
                            sessions.add(session);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public static Session getSessionById(int id) {
        return getAllSessions().stream()
                .filter(session -> session.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static List<Session> getSessionsForMovie(int movieId) {
        return getAllSessions().stream()
                .filter(session -> session.getMovie().getId() == movieId)
                .toList();
    }

    public static void deleteSession(int id) {
        List<Session> sessions = getAllSessions();
        sessions.removeIf(session -> session.getId() == id);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Session session : sessions) {
                String occupiedSeats = serializeSeats(session.getOccupiedSeats());
                writer.println(String.format("%d,%d,%d,%s,%.2f,%s",
                    session.getId(),
                    session.getMovie().getId(),
                    session.getRoom().getId(),
                    session.getStartTime().format(formatter),
                    session.getPrice(),
                    occupiedSeats));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 