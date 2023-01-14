package Model;

public class ReportEntry {
    private String utente;
    private Float percentuale;
    private Integer venduti;

    public ReportEntry(String utente, Float percentuale, Integer venduti) {
        this.utente = utente;
        this.percentuale = percentuale;
        this.venduti = venduti;
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

    public Integer getVenduti() {
        return venduti;
    }

    public void setVenduti(Integer venduti) {
        this.venduti = venduti;
    }

    @Override
    public String toString() {
        return "ReportEntry{" +
                "utente='" + utente + '\'' +
                ", percentuale=" + percentuale +
                ", venduti=" + venduti +
                '}';
    }

    public String toPrettyString() {
        return String.format("\"%s\" ha venduto il %.1f%% su %d annunci inseriti.",
                utente, percentuale, venduti);
    }
}
