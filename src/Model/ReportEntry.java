package Model;

public class ReportEntry {
    private String utente;
    private Float percentuale;

    public ReportEntry(String utente, Float percentuale) {
        this.utente = utente;
        this.percentuale = percentuale;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }

    public Float getPercentuale() {
        return percentuale;
    }

    public void setPercentuale(Float percentuale) {
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
