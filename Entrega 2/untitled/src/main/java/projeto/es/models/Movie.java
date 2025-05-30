package projeto.es.models;

import java.time.LocalDate;

public class Movie extends Entity {
    private String name;
    private String language;
    private String category;
    private String director;
    private LocalDate releaseDate;
    private int duration; // in minutes

    public Movie(String name, String language, String category, String director, LocalDate releaseDate, int duration) {
        super();
        this.name = name;
        this.language = language;
        this.category = category;
        this.director = director;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    // Getters
    public String getName() { return name; }
    public String getLanguage() { return language; }
    public String getCategory() { return category; }
    public String getDirector() { return director; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public int getDuration() { return duration; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLanguage(String language) { this.language = language; }
    public void setCategory(String category) { this.category = category; }
    public void setDirector(String director) { this.director = director; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %d min", name, language, duration);
    }
} 