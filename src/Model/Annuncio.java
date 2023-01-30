package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Annuncio {
    private Long numero;
    private String inserzionista;
    private String descrizione;
    private String categoria;
    private LocalDateTime inserito;
    private LocalDateTime modificato;
    private LocalDateTime venduto;

    public Annuncio(Long numero) {
        this.numero = numero;
    }

    public Annuncio(Long numero, String inserzionista, String descrizione, String categoria, LocalDateTime inserito, LocalDateTime modificato, LocalDateTime venduto) {
        this.numero = numero;
        this.inserzionista = inserzionista;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.inserito = inserito;
        this.modificato = modificato;
        this.venduto = venduto;
    }

    public Annuncio(String inserzionista, String descrizione, String categoria, LocalDateTime inserito) {
        this.inserzionista = inserzionista;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.inserito = inserito;
    }

    public Long getID() {
        return this.numero;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getInserzionista() {
        return inserzionista;
    }

    public void setInserzionista(String inserzionista) {
        this.inserzionista = inserzionista;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDateTime getInserito() {
        return inserito;
    }

    public void setInserito(LocalDateTime inserito) {
        this.inserito = inserito;
    }

    public LocalDateTime getModificato() {
        if (modificato == null) {
            if (venduto == null)
                return inserito;
            else
                return venduto;
        }
        return modificato;
    }

    public void setModificato(LocalDateTime modificato) {
        this.modificato = modificato;
    }

    public LocalDateTime getVenduto() {
        return venduto;
    }

    public void setVenduto(LocalDateTime venduto) {
        this.venduto = venduto;
    }

    @Override
    public String toString() {
        return "Annuncio{" + "numero=" + numero + ", inserzionista='" + inserzionista + '\'' + ", descrizione='" + descrizione + '\'' + ", categoria='" + categoria + '\'' + ", inserito=" + inserito + ", modificato=" + modificato + ", venduto=" + venduto + '}';
    }

    public String toPrettyString(String dateTimeFormat) {
        return String.format("""
                Annuncio #%d inserito da "%s":
                Stato: %s
                Descrizione: %s
                Categoria: %s
                Inserito il: %s
                Ultima modifica: %s
                """, numero, inserzionista, (venduto == null) ? "Disponibile" : "Venduto il " + ((dateTimeFormat == null) ? venduto : venduto.format(DateTimeFormatter.ofPattern(dateTimeFormat))), descrizione, categoria, (dateTimeFormat == null) ? inserito : inserito.format(DateTimeFormatter.ofPattern(dateTimeFormat)), (dateTimeFormat == null) ? this.getModificato() : this.getModificato().format(DateTimeFormatter.ofPattern(dateTimeFormat)));
    }
}
