package Model;

public class ReportEntry {
    private String utente;
    private float percentuale;

    public ReportEntry(String utente, float percentuale) {
        this.utente = utente;
        this.percentuale = percentuale;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public float getPercentuale() {
        return percentuale;
    }

    public void setPercentuale(float percentuale) {
        this.percentuale = percentuale;
    }

    @Override
    public String toString() {
        return "ReportEntry{" +
                "utente='" + utente + '\'' +
                ", percentuale=" + percentuale +
                '}';
    }
}
