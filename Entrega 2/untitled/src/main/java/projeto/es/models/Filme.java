package projeto.es.models;

import java.util.Date;

public class Filme {
    private int id;
    private String titulo;
    private String realizacao;
    private Date dataDeLancamento;
    private String restricaoDeIdade;
    private int duracao;

    public Filme(int id, String titulo, String realizacao, Date dataDeLancamento, String restricaoDeIdade, int duracao) {
        this.id = id;
        this.titulo = titulo;
        this.realizacao = realizacao;
        this.dataDeLancamento = dataDeLancamento;
        this.restricaoDeIdade = restricaoDeIdade;
        this.duracao = duracao;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRealizacao() {
        return realizacao;
    }

    public void setRealizacao(String realizacao) {
        this.realizacao = realizacao;
    }

    public Date getDataDeLancamento() {
        return dataDeLancamento;
    }

    public void setDataDeLancamento(Date dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    public String getRestricaoDeIdade() {
        return restricaoDeIdade;
    }

    public void setRestricaoDeIdade(String restricaoDeIdade) {
        this.restricaoDeIdade = restricaoDeIdade;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public String toString() {
        return "Filme {" +
                "id=" + id +
                ", título='" + titulo + '\'' +
                ", realização='" + realizacao + '\'' +
                ", data de lançamento=" + dataDeLancamento +
                ", restrição de idade='" + restricaoDeIdade + '\'' +
                ", duração=" + duracao + " minutos" +
                '}';
    }

}
