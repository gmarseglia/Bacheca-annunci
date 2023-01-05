package Model;

import java.time.LocalDateTime;

public class MessaggioPrivato {
    private Utente mittente;
    private Utente destinatario;
    private LocalDateTime inviato;
    private String testo;

    public MessaggioPrivato(Utente mittente, Utente destinatario, LocalDateTime inviato, String testo) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.inviato = inviato;
        this.testo = testo;
    }

    public Utente getMittente() {
        return mittente;
    }

    public void setMittente(Utente mittente) {
        this.mittente = mittente;
    }

    public Utente getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Utente destinatario) {
        this.destinatario = destinatario;
    }

    public LocalDateTime getInviato() {
        return inviato;
    }

    public void setInviato(LocalDateTime inviato) {
        this.inviato = inviato;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
