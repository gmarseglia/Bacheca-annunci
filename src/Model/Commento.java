package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commento {
    private String utente;
    private Long annuncio;
    private LocalDateTime scritto;
    private String testo;

    public Commento(String utente, Long annuncio, LocalDateTime scritto, String testo) {
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

    public Long getAnnuncio() {
        return annuncio;
    }

    public void setAnnuncio(Long annuncio) {
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

    public String toPrettyString(String dateTimeFormat, boolean printAnnuncio) {
        return String.format("""
                        "%s" ha commentato %s %s:
                        \t%s""",
                utente, ((printAnnuncio) ? "sotto l'annuncio " + annuncio : "") + " il",
                (dateTimeFormat == null) ? scritto : scritto.format(DateTimeFormatter.ofPattern(dateTimeFormat)),
                testo
        );
    }
}
