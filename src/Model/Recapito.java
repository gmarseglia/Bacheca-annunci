package Model;

public class Recapito {
    private String valore;
    private TipoRecapito tipo;
    private Anagrafica anagrafica;

    public Recapito(String valore, TipoRecapito tipo, Anagrafica anagrafica) {
        this.valore = valore;
        this.tipo = tipo;
        this.anagrafica = anagrafica;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public TipoRecapito getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecapito tipo) {
        this.tipo = tipo;
    }

    public Anagrafica getAnagrafica() {
        return anagrafica;
    }

    public void setAnagrafica(Anagrafica anagrafica) {
        this.anagrafica = anagrafica;
    }

    public String getAnagraficaID(){
        return this.anagrafica.getCodiceFiscale();
    }
}
