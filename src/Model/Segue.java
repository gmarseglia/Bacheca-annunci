package Model;

public class Segue {
    private String utente;
    private long annuncio;

    public Segue(String utente, long annuncio) {
        this.utente = utente;
        this.annuncio = annuncio;
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
}
