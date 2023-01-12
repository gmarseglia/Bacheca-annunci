package View;


import Controller.DatabaseConnectionController;
import Controller.RegistrationController;
import DAO.BatchResult;
import DAO.DBResult;
import Model.*;
import Model.Exception.InputInterruptedRuntimeException;
import Utility.ScannerUtility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViewLogin {

    private enum MODE {
        LOGIN,
        REGISTRATION,
        EXIT
    }

    public static void main(String[] args) {
        try {
            ActiveUser.setRole(Role.REGISTRATORE);

            MODE selectedMode = null;
            do {
                switch (ScannerUtility.askFirstChar("(L)ogin, (R)egistrazione o (U)scire?")) {
                    case "l", "L" -> selectedMode = MODE.LOGIN;
                    case "r", "R" -> selectedMode = MODE.REGISTRATION;
                    case "u", "U" -> selectedMode = MODE.EXIT;
                }
            } while (selectedMode == null);

            switch (selectedMode) {
                case LOGIN -> login();
                case REGISTRATION -> register();
                case EXIT -> {
                    System.out.println("Chiusura della connessione con il database.");
                    String message = DatabaseConnectionController.closeConnection();
                    if (message != null)
                        System.out.println(message);
                    System.out.println("Uscita dall'applicazione.");
                    System.exit(0);
                }
            }

        } catch (InputInterruptedRuntimeException e) {
            ExceptionHandler.handleInputInterrupted(e);
        }
    }

    private static void login() {
        String username, password;

        username = ScannerUtility.askString("Username", 30);
        password = ScannerUtility.askString("Password", 30);
        Credenziali credenziali = new Credenziali(username, password, null);

        System.out.printf("Login di '%s'... ", credenziali.getUsername());

        DBResult loginResult = RegistrationController.login(credenziali);

        if (loginResult.getResult()) {
            ActiveUser.setRole(credenziali.getRole());
            ActiveUser.setUsername(credenziali.getUsername());
        }

        if (loginResult.getResult())
            System.out.printf("eseguito con successo.\n");
        else {
            System.out.printf("eseguito con insuccesso (%s).\n", loginResult.getMessage());
        }


        dispatch(loginResult.getResult());
    }

    private static void register() {
        String username;
        Utente utente = null;
        String password;
        Role role;
        Credenziali credenziali = null;
        String codiceFiscale;
        String nome, cognome;
        Sesso sesso;
        LocalDate dataNascita;
        String comuneNascita, indirizzoResidenza, indirizzoFatturazione;
        Anagrafica anagrafica = null;
        String valoreRecapito;
        TipoRecapito tipoRecapito;
        List<Recapito> recapitoList = new ArrayList<>();

        // CREDENZIALI
        Boolean confirmCredenziali;
        do {
            username = ScannerUtility.askString("Username", 30);
            password = ScannerUtility.askString("Password", 30);
            role = null;
            do {
                switch (ScannerUtility.askFirstChar("Ruolo (B)ase o (G)estore")) {
                    case "b", "B" -> role = Role.BASE;
                    case "g", "G" -> role = Role.GESTORE;
                }
            } while (role == null);

            System.out.printf("\nUsername: %s\nPassword: %s\nRuolo: %s\n",
                    username,
                    password,
                    switch (role) {
                        case BASE -> "Base";
                        case GESTORE -> "Gestore";
                        default -> null;
                    });

            confirmCredenziali = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o?")) {
                    case "s", "S" -> {
                        confirmCredenziali = true;
                        utente = new Utente(username);
                        credenziali = new Credenziali(username, password, role);
                    }
                    case "n", "N" -> confirmCredenziali = false;
                }
            } while (confirmCredenziali == null);

        } while (!confirmCredenziali);

        // ANAGRAFICA
        Boolean confirmAnagrafica = null;
        do {
            System.out.println();
            codiceFiscale = ScannerUtility.askString("Codice fiscale", 16);
            nome = ScannerUtility.askText("Nome", 100);
            cognome = ScannerUtility.askText("Cognome", 100);
            sesso = null;
            do {
                switch (ScannerUtility.askFirstChar("Sesso: (U)omo o (D)onna")) {
                    case "u", "U" -> sesso = Sesso.UOMO;
                    case "d", "D" -> sesso = Sesso.DONNA;
                }
            } while (sesso == null);
            dataNascita = ScannerUtility.askLocalDate("Data di nascita");
            comuneNascita = ScannerUtility.askText("Comune di nascita", 100);
            indirizzoResidenza = ScannerUtility.askText("Indirizzo di residenza", 100);
            indirizzoFatturazione = null;
            Boolean askFatturazione = null;
            do {
                switch (ScannerUtility.askFirstChar("Indirizzo di fatturazione è diverso da indirizzo di residenza? (S)i o (N)o")) {
                    case "s", "S" -> {
                        indirizzoFatturazione = ScannerUtility.askText("Indirizzo di fatturazione", 100);
                        askFatturazione = true;
                    }
                    case "n", "N" -> askFatturazione = false;
                }
            } while (askFatturazione == null);

            System.out.printf("\nCodice fiscale: %s\nNome: %s\nCognome: %s\nSesso: %s\n" +
                            "Data di nascita: %s\nComune di nascita: %s\nIndirizzo di residenza: %s\nIndirizzo di fatturazione: %s\n",
                    codiceFiscale, nome, cognome, (sesso == Sesso.DONNA) ? "Donna" : "Uomo",
                    dataNascita.format(DateTimeFormatter.ofPattern(ScannerUtility.DATE_FORMAT)),
                    comuneNascita, indirizzoResidenza, (indirizzoFatturazione == null) ? "" : indirizzoFatturazione
            );

            confirmAnagrafica = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermi? (S)i o (N)o?")) {
                    case "s", "S" -> {
                        confirmAnagrafica = true;
                        anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, indirizzoFatturazione);
                    }
                    case "n", "N" -> confirmAnagrafica = false;
                }
            } while (confirmAnagrafica == null);

        } while (!confirmAnagrafica);

        // RECAPITO PREFERITO
        Boolean confirmRecapitoPreferito;
        do {
            System.out.println();
            valoreRecapito = ScannerUtility.askString("Valore recapito preferito", 60);
            tipoRecapito = null;
            do {
                switch (ScannerUtility.askFirstChar("Tipo recapito preferito? (T)elefono, (C)ellulare o (E)mail")) {
                    case "t", "T" -> tipoRecapito = TipoRecapito.TELEFONO;
                    case "c", "C" -> tipoRecapito = TipoRecapito.CELLULARE;
                    case "e", "E" -> tipoRecapito = TipoRecapito.EMAIL;
                }
            } while (tipoRecapito == null);

            System.out.printf("\nValore recapito preferito: %s\nTipo recapito preferito: %s\n",
                    valoreRecapito,
                    switch (tipoRecapito) {
                        case TELEFONO -> "Telefono";
                        case CELLULARE -> "Cellulare";
                        case EMAIL -> "Email";
                    });

            confirmRecapitoPreferito = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermi recapito preferito? (S)i o (N)o")) {
                    case "s", "S" -> {
                        confirmRecapitoPreferito = true;
                        recapitoList.add(new Recapito(valoreRecapito, tipoRecapito, codiceFiscale));
                    }
                    case "n", "N" -> confirmRecapitoPreferito = false;
                }
            } while (confirmRecapitoPreferito == null);

        } while (!confirmRecapitoPreferito);

        // ALTRI RECAPITI
        Boolean confirmAltriRecapiti;
        int recapitiCounter = 1;
        do {
            confirmAltriRecapiti = null;
            System.out.println();
            do {
                switch (ScannerUtility.askFirstChar("Fornire altri recapiti? (S)i o (N)o")) {
                    case "s", "S" -> confirmAltriRecapiti = true;
                    case "n", "N" -> confirmAltriRecapiti = false;
                }
            } while (confirmAltriRecapiti == null);

            if (confirmAltriRecapiti) {
                System.out.println();
                Boolean confirmRecapito;
                do {
                    valoreRecapito = ScannerUtility.askString(String.format("Valore recapito #%d", recapitiCounter), 60);
                    tipoRecapito = null;
                    do {
                        switch (ScannerUtility.askFirstChar("Tipo recapito? (T)elefono, (C)ellulare o (E)mail")) {
                            case "t", "T" -> tipoRecapito = TipoRecapito.TELEFONO;
                            case "c", "C" -> tipoRecapito = TipoRecapito.CELLULARE;
                            case "e", "E" -> tipoRecapito = TipoRecapito.EMAIL;
                        }
                    } while (tipoRecapito == null);

                    System.out.printf("\nValore recapito #%d: %s\nTipo recapito #%d: %s\n",
                            recapitiCounter,
                            valoreRecapito,
                            recapitiCounter,
                            switch (tipoRecapito) {
                                case TELEFONO -> "Telefono";
                                case CELLULARE -> "Cellulare";
                                case EMAIL -> "Email";
                            });

                    confirmRecapito = null;
                    do {
                        switch (ScannerUtility.askFirstChar(String.format("Confermi recapito #%d? (S)i o (N)o", recapitiCounter))) {
                            case "s", "S" -> {
                                recapitiCounter++;
                                confirmRecapito = true;
                                recapitoList.add(new Recapito(valoreRecapito, tipoRecapito, codiceFiscale));
                            }
                            case "n", "N" -> confirmRecapito = false;
                        }
                    } while (confirmRecapito == null);

                } while (!confirmRecapito);
            }

        } while (confirmAltriRecapiti);


        System.out.printf("\nRegistrazione di %s... ", username);

        BatchResult registrationResult;
        registrationResult = RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);

        if (registrationResult.getExtraResult()) {
            System.out.println("conclusa con successo.");
        } else {
            System.out.printf("conclusa con insuccesso (%s).\n", registrationResult.getExtraMessage());
        }

        for (int recapitoIndex = 1; recapitoIndex < recapitoList.size(); recapitoIndex++) {
            System.out.printf("Recapito #%d registrato con %s.\n", recapitoIndex,
                    (registrationResult.getBatchResult()[recapitoIndex - 1] == 1) ? "successo" : "insuccesso");
        }

        if (registrationResult.getExtraResult()) {
            ActiveUser.setRole(credenziali.getRole());
            ActiveUser.setUsername(credenziali.getUsername());
        }

        dispatch(registrationResult.getExtraResult());
    }

    private static void dispatch(boolean result) {
        if (result) {
            switch (ActiveUser.getRole()) {
                case BASE, GESTORE -> ViewUtente.begin();
            }
        } else {
            main(null);
        }
    }
}
