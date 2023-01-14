package Model;

public class Recapito {
    private String valore;
    private TipoRecapito tipo;
    private String anagrafica;

    public Recapito(String valore, TipoRecapito tipo, String anagrafica) {
        this.valore = valore;
        this.tipo = tipo;
        this.anagrafica = anagrafica;
    }

    public Recapito() {
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

    public String getAnagrafica() {
        return anagrafica;
    }

    public void setAnagrafica(String anagrafica) {
        this.anagrafica = anagrafica;
    }

    public String getAnagraficaID() {
        return this.anagrafica;
    }

    @Override
    public String toString() {
        return "Recapito{" +
                "valore='" + valore + '\'' +
                ", tipo=" + tipo +
                ", anagrafica='" + anagrafica + '\'' +
                '}';
    }

    public String toPrettyString(boolean printAnagrafica) {
        return String.format("""
                        Recapito "%s" di tipo %s%s
                        """,
                valore,
                switch (tipo) {
                    case EMAIL -> "email";
                    case CELLULARE -> "cellulare";
                    case TELEFONO -> "telefono";
                },
                (printAnagrafica ? " riferito a " + anagrafica : "") + ".");
    }
}
