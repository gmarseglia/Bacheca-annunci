package View;


import DAO.DAO;
import Model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Login {

    private enum MODE {
        LOGIN,
        REGISTRATION
    }

    public static void main(String[] args) {
        MODE mode;

        ActiveUser.setRole(Role.REGISTRATORE);

        /*
            #TODO: Ask users if they want to log in or register
         */
        mode = MODE.REGISTRATION;

        switch (mode) {
            case LOGIN -> login();
            case REGISTRATION -> register();
        }
    }

    private static void login() {

    }

    private static void register() {
        String username, password;
        Role role;
        String codiceFiscale;
        String nome, cognome;
        Sesso sesso;
        LocalDate dataNascita;
        String comuneNascita, indirizzoResidenza, indirizzoFatturazione;
        List<Recapito> recapitoList = new ArrayList<>();

        /*
        #TODO: Ask users info for registration
         */
        username = "user";
        password = "pass";
        role = Role.BASE;
        codiceFiscale = "KRTMRA98L10H501E";
        nome = "mario";
        cognome = "kart";
        sesso = Sesso.UOMO;
        dataNascita = LocalDate.of(1998, 7, 10);
        comuneNascita = "roma";
        indirizzoResidenza = "indirizzo";
        indirizzoFatturazione = null;
        recapitoList.add(new Recapito("email@email.com", TipoRecapito.EMAIL, codiceFiscale));
        recapitoList.add(new Recapito("339123456", TipoRecapito.CELLULARE, codiceFiscale));

        Utente utente = new Utente(username);
        Credenziali credenziali = new Credenziali(username, password, role);
        Anagrafica anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username);
        if (indirizzoFatturazione != null) anagrafica.setIndirizzoFatturazione(indirizzoFatturazione);

        boolean result = DAO.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);

        System.out.printf("Registration was: %s\n\n", (result) ? "successful" : "unsuccessful");

        if (result) {
            ActiveUser.setRole(credenziali.getRole());
            switch (ActiveUser.getRole()) {
                case BASE -> Base.dispatch();
                case GESTORE -> Gestore.dispatch();
            }
        } else
            main(null);
    }


}
