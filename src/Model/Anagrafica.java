package Model;

import java.time.LocalDate;

public class Anagrafica {
    private String codiceFiscale;
    private String nome;
    private String cognome;
    private Sesso sesso;
    private LocalDate dataNascita;
    private String comuneNascita;
    private String indirizzoResidenza;
    private String indirizzoFatturazione;
    private String usernameUtente;

    public Anagrafica(String codiceFiscale, String nome, String cognome, Sesso sesso,
                      LocalDate dataNascita, String comuneNascita, String indirizzoResidenza, String usernameUtente) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.dataNascita = dataNascita;
        this.comuneNascita = comuneNascita;
        this.indirizzoResidenza = indirizzoResidenza;
        this.usernameUtente = usernameUtente;
    }

    public Anagrafica(String codiceFiscale, String nome, String cognome, Sesso sesso, LocalDate dataNascita, String comuneNascita, String indirizzoResidenza, String indirizzoFatturazione, String usernameUtente) {
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.sesso = sesso;
        this.dataNascita = dataNascita;
        this.comuneNascita = comuneNascita;
        this.indirizzoResidenza = indirizzoResidenza;
        this.indirizzoFatturazione = indirizzoFatturazione;
        this.usernameUtente = usernameUtente;
    }

    public Anagrafica(){}

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

    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    public String getIndirizzoFatturazione() {
        return indirizzoFatturazione;
    }

    public void setIndirizzoFatturazione(String indirizzoFatturazione) {
        this.indirizzoFatturazione = indirizzoFatturazione;
    }

    public String getUsernameUtente() {
        return usernameUtente;
    }

    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }

    public String getID(){
        return this.codiceFiscale;
    }

    @Override
    public String toString() {
        return "Anagrafica{" +
                "codiceFiscale='" + codiceFiscale + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", sesso=" + sesso +
                ", dataNascita=" + dataNascita +
                ", comuneNascita='" + comuneNascita + '\'' +
                ", indirizzoResidenza='" + indirizzoResidenza + '\'' +
                ", indirizzoFatturazione='" + indirizzoFatturazione + '\'' +
                ", usernameUtente='" + usernameUtente + '\'' +
                '}';
    }
}
