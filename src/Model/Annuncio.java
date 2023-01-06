package Model;

import java.time.LocalDateTime;

public class Annuncio {
    private long numero;
    private String inserzionista;
    private String descrizione;
    private long priceInCents;
    private String categoria;
    private LocalDateTime inserito;
    private LocalDateTime modificato;
    private LocalDateTime venduto;

    public Annuncio(long numero) {
        this.numero = numero;
    }

    public Annuncio(long numero, String inserzionista, String descrizione, long priceInCents, String categoria, LocalDateTime inserito, LocalDateTime modificato, LocalDateTime venduto) {
        this.numero = numero;
        this.inserzionista = inserzionista;
        this.descrizione = descrizione;
        this.priceInCents = priceInCents;
        this.categoria = categoria;
        this.inserito = inserito;
        this.modificato = modificato;
        this.venduto = venduto;
    }

    public Annuncio(String inserzionista, String descrizione, long priceInCents, String categoria, LocalDateTime inserito) {
        this.inserzionista = inserzionista;
        this.descrizione = descrizione;
        this.priceInCents = priceInCents;
        this.categoria = categoria;
        this.inserito = inserito;
    }

    public long getID() {
        return this.numero;
    }

    public long getNumero() {
        return numero;
    }

    public void setNumero(long numero) {
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

    public long getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(long priceInCents) {
        this.priceInCents = priceInCents;
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
        return "Annuncio{" +
                "numero=" + numero +
                ", inserzionista='" + inserzionista + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", priceInCents=" + priceInCents +
                ", categoria='" + categoria + '\'' +
                ", inserito=" + inserito +
                ", modificato=" + modificato +
                ", venduto=" + venduto +
                '}';
    }
}
