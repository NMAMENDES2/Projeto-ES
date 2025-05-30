package projeto.es.Repository;

import projeto.es.models.Movie;
import projeto.es.models.Room;
import projeto.es.models.Session;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionDatabase {
    private static final String FILE_PATH = "sessions.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void saveSession(Session session) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(String.format("%d,%d,%d,%s,%.2f",
                    session.getId(),
                    session.getMovie().getId(),
                    session.getRoom().getId(),
                    session.getStartTime().format(formatter),
                    session.getPrice()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
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
                        sessions.add(session);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public static List<Session> getSessionsForMovie(int movieId) {
        return getAllSessions().stream()
                .filter(session -> session.getMovie().getId() == movieId)
                .collect(Collectors.toList());
    }

    public static Session getSessionById(int id) {
        return getAllSessions().stream()
                .filter(session -> session.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void updateSession(Session updatedSession) {
        List<Session> sessions = getAllSessions();
        List<Session> updatedSessions = new ArrayList<>();
        
        for (Session session : sessions) {
            if (session.getId() == updatedSession.getId()) {
                updatedSessions.add(updatedSession);
            } else {
                updatedSessions.add(session);
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Session session : updatedSessions) {
                writer.println(String.format("%d,%d,%d,%s,%.2f",
                        session.getId(),
                        session.getMovie().getId(),
                        session.getRoom().getId(),
                        session.getStartTime().format(formatter),
                        session.getPrice()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSession(int id) {
        List<Session> sessions = getAllSessions();
        sessions.removeIf(session -> session.getId() == id);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Session session : sessions) {
                writer.println(String.format("%d,%d,%d,%s,%.2f",
                        session.getId(),
                        session.getMovie().getId(),
                        session.getRoom().getId(),
                        session.getStartTime().format(formatter),
                        session.getPrice()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 