package projeto.es.models;

import java.util.Date;

public class Film extends Entity{
    private String title;
    private String director;
    private Date launchDate;
    private String ageRestriction;
    private int duration;

    public Film(String titulo, String realizacao, Date dataDeLancamento, String restricaoDeIdade, int duracao) {
        super();
        this.title = titulo;
        this.director = realizacao;
        this.launchDate = dataDeLancamento;
        this.ageRestriction = restricaoDeIdade;
        this.duration = duracao;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString() {
        return "Film {" +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", launch date=" + launchDate +
                ", age restriction='" + ageRestriction + '\'' +
                ", duration =" + duration + " minutes" +
                '}';
    }

}
