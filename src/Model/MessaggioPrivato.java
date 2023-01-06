package Model;

import java.time.LocalDateTime;

public class MessaggioPrivato {
    private String mittente;
    private String destinatario;
    private LocalDateTime inviato;
    private String testo;

    public MessaggioPrivato(String mittente, String destinatario, LocalDateTime inviato, String testo) {
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.inviato = inviato;
        this.testo = testo;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
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

    @Override
    public String toString() {
        return "MessaggioPrivato{" +
                "mittente='" + mittente + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", inviato=" + inviato +
                ", testo='" + testo + '\'' +
                '}';
    }
}
