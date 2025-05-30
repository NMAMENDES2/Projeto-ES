package projeto.es.Repository;

import projeto.es.models.Movie;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDatabase {
    private static final String FILE_PATH = "movies.txt";

    public static void saveMovie(Movie movie) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(String.format("%d,%s,%s,%s,%s,%s,%d",
                    movie.getId(),
                    movie.getName(),
                    movie.getLanguage(),
                    movie.getCategory(),
                    movie.getDirector(),
                    movie.getReleaseDate(),
                    movie.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Movie movie = new Movie(
                            parts[1], // name
                            parts[2], // language
                            parts[3], // category
                            parts[4], // director
                            LocalDate.parse(parts[5]), // releaseDate
                            Integer.parseInt(parts[6]) // duration
                    );
                    movies.add(movie);
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
        List<Movie> updatedMovies = new ArrayList<>();
        
        for (Movie movie : movies) {
            if (movie.getId() == updatedMovie.getId()) {
                updatedMovies.add(updatedMovie);
            } else {
                updatedMovies.add(movie);
            }
        }
        
        // Rewrite the entire file
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Movie movie : updatedMovies) {
                writer.println(String.format("%d,%s,%s,%s,%s,%s,%d",
                        movie.getId(),
                        movie.getName(),
                        movie.getLanguage(),
                        movie.getCategory(),
                        movie.getDirector(),
                        movie.getReleaseDate(),
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
                        movie.getReleaseDate(),
                        movie.getDuration()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 