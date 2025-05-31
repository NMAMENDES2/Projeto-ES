package projeto.es.Repository;

import projeto.es.models.Movie;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovieDatabase {
    private static final String FILE_PATH = "movies.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public static void saveMovie(Movie movie) {

        System.out.println("DEBUG: Saving new session with ID " + movie.getId());
        System.out.println("DEBUG: Movie: " +  movie.getName());
        
        List<Movie> movies = getAllMovies();
        System.out.println("Movies size: " + movies.size());
        movies.add(movie);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Movie m : movies) {
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%d",
                    m.getId(),
                    m.getName(),
                    m.getLanguage(),
                    m.getCategory(),
                    m.getDirector(),
                    m.getReleaseDate().format(formatter),
                    m.getDuration()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        if (!file.exists()) {
            return movies;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        Movie movie = new Movie(
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            LocalDate.parse(parts[5], formatter),
                            Integer.parseInt(parts[6])
                        );
                        movie.setId(Integer.parseInt(parts[0]));
                        movies.add(movie);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static Movie getMovieById(int id) {
        return getAllMovies().stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void updateMovie(Movie updatedMovie) {
        List<Movie> movies = getAllMovies();
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getId() == updatedMovie.getId()) {
                movies.set(i, updatedMovie);
                break;
            }
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Movie movie : movies) {
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%d",
                    movie.getId(),
                    movie.getName(),
                    movie.getLanguage(),
                    movie.getCategory(),
                    movie.getDirector(),
                    movie.getReleaseDate().format(formatter),
                    movie.getDuration()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMovie(int id) {
        List<Movie> movies = getAllMovies();
        movies.removeIf(movie -> movie.getId() == id);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Movie movie : movies) {
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%d",
                    movie.getId(),
                    movie.getName(),
                    movie.getLanguage(),
                    movie.getCategory(),
                    movie.getDirector(),
                    movie.getReleaseDate().format(formatter),
                    movie.getDuration()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 