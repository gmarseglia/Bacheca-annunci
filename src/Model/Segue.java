package Model;

public class Segue {
    private String utente;
    private Long annuncio;

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

    public Long getAnnuncio() {
        return annuncio;
    }

    public void setAnnuncio(Long annuncio) {
        this.annuncio = annuncio;
    }
}
