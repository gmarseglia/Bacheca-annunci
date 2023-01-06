package Model;

import java.time.LocalDateTime;

public class Commento {
    private String utente;
    private long annuncio;
    private LocalDateTime scritto;
    private String testo;

    public Commento(String utente, long annuncio, LocalDateTime scritto, String testo) {
        this.utente = utente;
        this.annuncio = annuncio;
        this.scritto = scritto;
        this.testo = testo;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public long getAnnuncio() {
        return annuncio;
    }

    public void setAnnuncio(long annuncio) {
        this.annuncio = annuncio;
    }

    public LocalDateTime getScritto() {
        return scritto;
    }

    public void setScritto(LocalDateTime scritto) {
        this.scritto = scritto;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    @Override
    public String toString() {
        return "Commento{" +
                "utente='" + utente + '\'' +
                ", annuncio=" + annuncio +
                ", scritto=" + scritto +
                ", testo='" + testo + '\'' +
                '}';
    }
}
