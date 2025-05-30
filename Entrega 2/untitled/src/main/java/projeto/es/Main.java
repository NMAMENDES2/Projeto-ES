package projeto.es;

import projeto.es.Repository.MovieDatabase;
import projeto.es.Repository.RoomDatabase;
import projeto.es.Repository.SessionDatabase;
import projeto.es.models.Movie;
import projeto.es.models.Room;
import projeto.es.models.Session;
import projeto.es.views.LoginFrame;

import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main {
    private static void initializeSampleData() {
        // Create a sample movie
        Movie avengers = new Movie(
            "Avengers: Endgame",
            "English",
            "Action/Sci-Fi",
            "Anthony and Joe Russo",
            LocalDate.of(2019, 4, 26),
            180  // 3 hours duration
        );
        MovieDatabase.saveMovie(avengers);

        // Create a sample room
        Room mainHall = new Room(
            "Main Hall",
            6,  // 6 rows
            8,  // 8 seats per row
            true  // 3D capable
        );
        RoomDatabase.saveRoom(mainHall);

        // Create sample sessions for today and tomorrow
        LocalDateTime now = LocalDateTime.now();
        
        Session afternoon = new Session(
            avengers,
            mainHall,
            now.withHour(15).withMinute(0),  // 3:00 PM today
            15.99  // Price for afternoon show
        );
        SessionDatabase.saveSession(afternoon);

        Session evening = new Session(
            avengers,
            mainHall,
            now.withHour(20).withMinute(0),  // 8:00 PM today
            18.99  // Price for evening show
        );
        SessionDatabase.saveSession(evening);

        Session tomorrow = new Session(
            avengers,
            mainHall,
            now.plusDays(1).withHour(17).withMinute(0),  // 5:00 PM tomorrow
            16.99  // Price for tomorrow's show
        );
        SessionDatabase.saveSession(tomorrow);
    }

    public static void main(String[] args) {
        // Initialize sample data if needed
        if (MovieDatabase.getAllMovies().isEmpty()) {
            initializeSampleData();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}