package Model;

public class Utente {
    private String username;
    private long annunci_inseriti;
    private long annunci_venduti;
    private Credenziali credenziali;
    private Anagrafica anagrafica;

    public Utente(String username) {
        this.username = username;
    }

    public Utente(String username, long annunci_inseriti, long annunci_venduti) {
        this.username = username;
        this.annunci_inseriti = annunci_inseriti;
        this.annunci_venduti = annunci_venduti;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getAnnunci_inseriti() {
        return annunci_inseriti;
    }

    public void setAnnunci_inseriti(long annunci_inseriti) {
        this.annunci_inseriti = annunci_inseriti;
    }

    public long getAnnunci_venduti() {
        return annunci_venduti;
    }

    public void setAnnunci_venduti(long annunci_venduti) {
        this.annunci_venduti = annunci_venduti;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    public Anagrafica getAnagrafica() {
        return anagrafica;
    }

    public void setAnagrafica(Anagrafica anagrafica) {
        this.anagrafica = anagrafica;
    }

    public String getID(){
        return this.username;
    }
}
