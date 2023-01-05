package View;


import Controller.RegistrationController;
import DAO.DAO;
import DAO.BatchResult;
import Model.*;
import Utility.RndData;

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
        mode = MODE.LOGIN;

        switch (mode) {
            case LOGIN -> login();
            case REGISTRATION -> register();
        }
    }

    private static void login() {
        String username, password;

        boolean random = false;
        if (random) {
            username = RndData.randomString(15);
            password = RndData.randomString(15);
        } else {
            username = "user";
            password = "pass";
        }

        Credenziali credenziali = new Credenziali(username, password, null);
        boolean loginResult = RegistrationController.login(credenziali);

        if (loginResult) ActiveUser.setRole(credenziali.getRole());

        dispatch(loginResult);
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
        boolean random = false;
        if (random) {
            username = RndData.randomString(15);
            password = RndData.randomString(15);
            role = Role.BASE;
            codiceFiscale = RndData.getRandomCF();
            nome = RndData.randomString(15);
            cognome = RndData.randomString(15);
            sesso = Sesso.UOMO;
            dataNascita = LocalDate.of(1998, 7, 10);
            comuneNascita = "roma";
            indirizzoResidenza = "indirizzo";
            indirizzoFatturazione = null;
            recapitoList.add(new Recapito(RndData.randomString(15), TipoRecapito.EMAIL, codiceFiscale));
            recapitoList.add(new Recapito(RndData.randomString(15), TipoRecapito.CELLULARE, codiceFiscale));
            recapitoList.add(new Recapito(RndData.randomString(15), TipoRecapito.TELEFONO, codiceFiscale));
        } else {
            username = "user2";
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
        }

        Utente utente = new Utente(username);
        Credenziali credenziali = new Credenziali(username, password, role);
        Anagrafica anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username);
        if (indirizzoFatturazione != null) anagrafica.setIndirizzoFatturazione(indirizzoFatturazione);

        boolean registrationResult;
        registrationResult = RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);

        System.out.printf("Registration was: %s\n", (registrationResult) ? "successful" : "unsuccessful");

        if (!registrationResult) return; //#TODO: should go to main

        BatchResult recapitiBatchResult;
        recapitiBatchResult = RegistrationController.registrazioneRecapitiFacoltativi(recapitoList);

        if (recapitiBatchResult.getAllTrue()) {
            System.out.println("Tutti i recapiti sono stati registrati con successo.\n");
        } else {
            for (int recapitoIndex = 1; recapitoIndex < recapitoList.size(); recapitoIndex++) {
                System.out.printf("Recapito #%d:%s registrato con %s.\n", recapitoIndex, recapitoList.get(recapitoIndex),
                        (recapitiBatchResult.getSinglesResult()[recapitoIndex - 1] == 1) ? "successo" : "insuccesso");
            }
        }

        ActiveUser.setRole(credenziali.getRole());

        dispatch(registrationResult);
    }

    private static void dispatch(boolean result) {
        if (result) {
            switch (ActiveUser.getRole()) {
                case BASE -> Base.dispatch();
                case GESTORE -> Gestore.dispatch();
            }
        } else {
            if (false) main(null);
        }
    }


}
