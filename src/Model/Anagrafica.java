package Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Anagrafica {
    private String codiceFiscale;
    private String nome;
    private String cognome;
    private Sesso sesso;
    private LocalDate dataNascita;
    private String comuneNascita;
    private String viaResidenza;
    private String civicoResidenza;
    private String capResidenza;
    private String viaFatturazione;
    private String civicoFatturazione;
    private String capFatturazione;

    public Anagrafica() {
    }

    public Anagrafica(String codiceFiscale, String nome, String cognome, Sesso sesso, LocalDate dataNascita, String comuneNascita, String viaResidenza, String civicoResidenza, String capResidenza, String viaFatturazione, String civicoFatturazione, String capFatturazione) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.dataNascita = dataNascita;
        this.comuneNascita = comuneNascita;
        this.viaResidenza = viaResidenza;
        this.civicoResidenza = civicoResidenza;
        this.capResidenza = capResidenza;
        this.viaFatturazione = viaFatturazione;
        this.civicoFatturazione = civicoFatturazione;
        this.capFatturazione = capFatturazione;
    }

    public Anagrafica(String codiceFiscale, String nome, String cognome, Sesso sesso, LocalDate dataNascita, String comuneNascita, String viaResidenza, String civicoResidenza, String capResidenza) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.dataNascita = dataNascita;
        this.comuneNascita = comuneNascita;
        this.viaResidenza = viaResidenza;
        this.civicoResidenza = civicoResidenza;
        this.capResidenza = capResidenza;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Sesso getSesso() {
        return sesso;
    }

    public void setSesso(Sesso sesso) {
        this.sesso = sesso;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getComuneNascita() {
        return comuneNascita;
    }

    public void setComuneNascita(String comuneNascita) {
        this.comuneNascita = comuneNascita;
    }

    public String getViaResidenza() {
        return viaResidenza;
    }

    public void setViaResidenza(String viaResidenza) {
        this.viaResidenza = viaResidenza;
    }

    public String getCivicoResidenza() {
        return civicoResidenza;
    }

    public void setCivicoResidenza(String civicoResidenza) {
        this.civicoResidenza = civicoResidenza;
    }

    public String getCapResidenza() {
        return capResidenza;
    }

    public void setCapResidenza(String capResidenza) {
        this.capResidenza = capResidenza;
    }

    public String getViaFatturazione() {
        return viaFatturazione;
    }

    public void setViaFatturazione(String viaFatturazione) {
        this.viaFatturazione = viaFatturazione;
    }

    public String getCivicoFatturazione() {
        return civicoFatturazione;
    }

    public void setCivicoFatturazione(String civicoFatturazione) {
        this.civicoFatturazione = civicoFatturazione;
    }

    public String getCapFatturazione() {
        return capFatturazione;
    }

    public void setCapFatturazione(String capFatturazione) {
        this.capFatturazione = capFatturazione;
    }

    public String getIndirizzoResidenza() {
        return viaResidenza + " " + civicoResidenza + ", " + capResidenza;
    }

    public String getIndirizzoFatturazione() {
        if (viaFatturazione == null) return null;
        return viaFatturazione + " " + civicoFatturazione + ", " + capFatturazione;
    }

    public String getID() {
        return this.codiceFiscale;
    }

    @Override
    public String toString() {
        return "Anagrafica{" + "codiceFiscale='" + codiceFiscale + '\'' + ", nome='" + nome + '\'' + ", cognome='" + cognome + '\'' + ", sesso=" + sesso + ", dataNascita=" + dataNascita + ", comuneNascita='" + comuneNascita + '\'' + ", viaResidenza='" + viaResidenza + '\'' + ", civicoResidenza='" + civicoResidenza + '\'' + ", capResidenza='" + capResidenza + '\'' + ", viaFatturazione='" + viaFatturazione + '\'' + ", civicoFatturazione='" + civicoFatturazione + '\'' + ", capFatturazione='" + capFatturazione + '\'' + '}';
    }

    public String toPrettyString(String dateFormat) {
        return String.format("""
                Codice fiscale: %s
                Nome e Cognome: %s %s
                Sesso: %s
                Nat%s il: %s a %s
                Indirizzo di residenza: %s
                Indirizzo di fatturazione: %s
                """, codiceFiscale, nome, cognome, sesso, (sesso == Sesso.DONNA) ? "a" : "o", (dateFormat == null) ? dataNascita : dataNascita.format(DateTimeFormatter.ofPattern(dateFormat)), comuneNascita, this.getIndirizzoResidenza(), (this.getIndirizzoFatturazione() == null) ? this.getIndirizzoResidenza() : this.getIndirizzoFatturazione());
    }
}
