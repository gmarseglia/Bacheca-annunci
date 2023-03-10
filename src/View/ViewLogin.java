package View;


import Controller.ViewController;
import Controller.DatabaseConnectionController;
import DAO.DBResultBatch;
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
        LOGIN,          //U0100
        REGISTRATION,   //U0000 && U0001
        EXIT
    }

    public static void main(String[] args) {
        try {
            ActiveUser.setRole(Role.REGISTRATORE);

            MODE selectedMode;

            System.out.println("[Premere CTRL+D per interrompere inserimento.]");

            while (true) {
                selectedMode = null;
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
            }

        } catch (InputInterruptedRuntimeException e) {
            ExceptionHandler.handleInputInterrupted();
        }
    }

    private static void login() {
        String username, password;

        username = ScannerUtility.askString("Username", 30);
        password = ScannerUtility.askString("Password", 30);
        Credenziali credenziali = new Credenziali(username, password, null);

        System.out.printf("Login di '%s'... ", credenziali.getUsername());

        DBResult loginResult = ViewController.login(credenziali);

        if (loginResult.getResult()) {
            ActiveUser.setRole(credenziali.getRole());
            ActiveUser.setUsername(credenziali.getUsername());
        }

        if (loginResult.getResult())
            System.out.print("eseguito con successo.\n");
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
        String comuneNascita;
        String viaResidenza, civicoResidenza, capResidenza;
        String viaFatturazione = null;
        String civicoFatturazione = null;
        String capFatturazione = null;
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
        Boolean confirmAnagrafica;
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
            viaResidenza = ScannerUtility.askString("Via dell'indirizzo di residenza", 100);
            civicoResidenza = ScannerUtility.askString("Civico dell'indirizzo di residenza", 100);
            capResidenza = ScannerUtility.askString("CAP dell'indirizzo di residenza", 100);
            Boolean askFatturazione = null;
            do {
                switch (ScannerUtility.askFirstChar("Indirizzo di fatturazione ?? diverso da indirizzo di residenza? (S)i o (N)o")) {
                    case "s", "S" -> {
                        viaFatturazione = ScannerUtility.askString("Via dell'indirizzo di fatturazione", 100);
                        civicoFatturazione = ScannerUtility.askString("Civico dell'indirizzo di fatturazione", 100);
                        capFatturazione = ScannerUtility.askString("CAP dell'indirizzo di fatturazione", 100);
                        askFatturazione = true;
                    }
                    case "n", "N" -> askFatturazione = false;
                }
            } while (askFatturazione == null);

            anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, viaResidenza, civicoResidenza, capResidenza, viaFatturazione, civicoFatturazione, capFatturazione);

            System.out.printf("""

                            Codice fiscale: %s
                            Nome: %s
                            Cognome: %s
                            Sesso: %s
                            Data di nascita: %s
                            Comune di nascita: %s
                            Indirizzo di residenza: %s
                            Indirizzo di fatturazione: %s
                            """,
                    codiceFiscale, nome, cognome, (sesso == Sesso.DONNA) ? "Donna" : "Uomo",
                    dataNascita.format(DateTimeFormatter.ofPattern(ScannerUtility.DATE_FORMAT)),
                    comuneNascita, anagrafica.getIndirizzoResidenza(),
                    (anagrafica.getIndirizzoFatturazione() == null) ? anagrafica.getIndirizzoResidenza() : anagrafica.getIndirizzoFatturazione()
            );

            confirmAnagrafica = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermi? (S)i o (N)o?")) {
                    case "s", "S" -> {
                        confirmAnagrafica = true;
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
                        recapitoList.add(new Recapito(valoreRecapito, tipoRecapito, username));
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
                                recapitoList.add(new Recapito(valoreRecapito, tipoRecapito, username));
                            }
                            case "n", "N" -> confirmRecapito = false;
                        }
                    } while (confirmRecapito == null);

                } while (!confirmRecapito);
            }

        } while (confirmAltriRecapiti);


        System.out.printf("\nRegistrazione di %s... ", username);

        DBResultBatch registrationResult;
        registrationResult = ViewController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);

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

        ScannerUtility.askAny();

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
