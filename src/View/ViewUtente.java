package View;

import Controller.ViewController;
import Controller.DatabaseConnectionController;
import DAO.DBResult;
import Model.*;
import Utility.ScannerUtility;

import java.util.ArrayList;
import java.util.List;

public class ViewUtente {

    protected enum OPERATION {
        INSERIRE_ANNUNCIO,                  //A0000
        DETTAGLI_ANNUNCIO,                  //A0100
        CERCARE_PER_CATEGORIA,              //A0200
        CERCARE_PER_UTENTE,                 //A0202
        CERCARE_PER_DESCRIZIONE,            //A0203
        CERCARE_TUTTI_ANNUNCI,              //A0204
        SEGUIRE_ANNUNCIO,                   //A0300
        STOP_SEGUIRE_ANNUNCIO,              //A0301
        CONTROLLARE_SEGUITI,                //A0400
        VENDERE_ANNUNCIO,                   //A0500
        DETTAGLI_UTENTE,                    //A0600
        INVIARE_MESSAGGIO,                  //M0000
        MESSAGGI_CON_UTENTE,                //M0100
        VISUALIZZARE_UTENTI_CON_MESSAGGI,   //M0101
        SCRIVERE_COMMENTO,                  //C0000
        VISUALIZZARE_CATEGORIE,             //T0000
        CREARE_CATEGORIA,                   //G0000
        CREARE_REPORT,                      //R0001
        LOGOUT,
        TERMINARE_APPLICAZIONE;


        public static OPERATION dispatchMap(String input) {
            return switch (input) {
                case "0" -> INSERIRE_ANNUNCIO;
                case "1" -> VENDERE_ANNUNCIO;
                case "2" -> CERCARE_TUTTI_ANNUNCI;
                case "3" -> CERCARE_PER_UTENTE;
                case "4" -> CERCARE_PER_CATEGORIA;
                case "5" -> CERCARE_PER_DESCRIZIONE;
                case "6" -> DETTAGLI_ANNUNCIO;
                case "7" -> SEGUIRE_ANNUNCIO;
                case "8" -> STOP_SEGUIRE_ANNUNCIO;
                case "9" -> CONTROLLARE_SEGUITI;
                case "a", "A" -> DETTAGLI_UTENTE;
                case "b", "B" -> SCRIVERE_COMMENTO;
                case "c", "C" -> VISUALIZZARE_UTENTI_CON_MESSAGGI;
                case "d", "D" -> MESSAGGI_CON_UTENTE;
                case "e", "E" -> INVIARE_MESSAGGIO;
                case "f", "F" -> VISUALIZZARE_CATEGORIE;
                case "g", "G" -> CREARE_CATEGORIA;
                case "h", "H" -> CREARE_REPORT;
                case "l", "L" -> LOGOUT;
                case "u", "U" -> TERMINARE_APPLICAZIONE;
                default -> null;
            };
        }
    }

    private static final String MAIN_DISPATCH = """
            Operazioni possibili:
            (0) [INSERIRE] un ANNUNCIO.
            (1) [INDICARE come VENDUTO] un annuncio inserito.
            (2) [CERCARE TUTTI] gli annunci.
            (3) [CERCARE per UTENTE] gli annunci.
            (4) [CERCARE per CATEGORIA] gli annunci.
            (5) [CERCARE per DESCRIZIONE] gli annunci disponibili.
            (6) Visualizzare i [DETTAGLI di un ANNUNCIO].
            (7) [AGGIUNGERE ai "SEGUITI"] un annuncio disponibile .
            (8) [RIMUOVERE dai "SEGUITI"] un annuncio.
            (9) [CONTROLLARE fra i "SEGUITI"] quali annunci hanno subito modifiche.
            (A) Visualizzare i [DETTAGLI di un UTENTE].
            (B) [SCRIVERE un COMMENTO] sotto un annuncio disponibile.
            (C) Visualizzare utenti con cui sono stati [SCAMBIATI MESSAGGI PRIVATI].
            (D) Visualizzare tutti i [MESSAGGI PRIVATI scambiati CON un UTENTE].
            (E) [INVIARE un MESSAGGIO PRIVATO] ad un utente.
            (F) [VISUALIZZARE le CATEGORIE].
            (G) [CREARE una CATEGORIA].
            (H) [GENERARE REPORT] sulla percentuale di vendita degli utenti.
            (L) [LOGOUT].
            (U) [USCIRE] dall'applicazione.
            """;

    private static final String DATETIME_FORMAT = "dd-MM-yyyy 'alle' HH:mm:ss";
    private static final String DATE_FORMAT = "dd-MM-yyyy";

    public static void begin() {

        OPERATION operation;
        do {
            System.out.println();
            do {
                operation = OPERATION.dispatchMap(ScannerUtility.askFirstChar(MAIN_DISPATCH));
            } while (operation == null);

            dispatch(operation);

        } while (operation != OPERATION.LOGOUT);

        System.out.print("Log out dall'applicazione... ");
        ActiveUser.setRole(Role.REGISTRATORE);
        ActiveUser.setUsername(null);
        DatabaseConnectionController.closeConnection();
        System.out.println("terminato con successo.");
        ScannerUtility.askAny();
        ViewLogin.main(null);
    }

    protected static void dispatch(OPERATION operation) {
        switch (operation) {
            case INSERIRE_ANNUNCIO -> inserireAnnuncio();
            case VENDERE_ANNUNCIO -> vendereAnnuncio();
            case CERCARE_TUTTI_ANNUNCI -> cercareAnnunci();
            case CERCARE_PER_UTENTE -> cercaPerUtente();
            case CERCARE_PER_CATEGORIA -> cercaPerCategoria();
            case CERCARE_PER_DESCRIZIONE -> cercaPerDescrizione();
            case DETTAGLI_ANNUNCIO -> dettagliAnnuncio();
            case SEGUIRE_ANNUNCIO -> seguireAnnuncio();
            case STOP_SEGUIRE_ANNUNCIO -> stopSeguireAnnuncio();
            case CONTROLLARE_SEGUITI -> controllareSeguiti();
            case DETTAGLI_UTENTE -> dettagliUtente();
            case SCRIVERE_COMMENTO -> scrivereCommento();
            case VISUALIZZARE_UTENTI_CON_MESSAGGI -> visualizzareUtentiConMessaggi();
            case MESSAGGI_CON_UTENTE -> visualizzareMessaggiConUtente();
            case INVIARE_MESSAGGIO -> inviareMessaggio();
            case VISUALIZZARE_CATEGORIE -> visualizzareCategorie();
            case CREARE_CATEGORIA -> creareCategoria();
            case CREARE_REPORT -> creareReport();
            case TERMINARE_APPLICAZIONE -> {
                System.out.println("Chiusura della connessione con il database.");
                String message = DatabaseConnectionController.closeConnection();
                if (message != null)
                    System.out.println(message);
                System.out.println("Uscita dall'applicazione.");
                System.exit(0);
            }
        }
    }

    // A0204
    private static void cercareAnnunci() {
        Boolean confirmOp = null;
        Boolean onlyAvailable;
        do {
            onlyAvailable = null;
            do {
                switch (ScannerUtility.askFirstChar("Filtrare per solo disponibili? (S)i o (N)o")) {
                    case "s", "S" -> onlyAvailable = true;
                    case "n", "N" -> onlyAvailable = false;
                }
            } while (onlyAvailable == null);

            System.out.printf("""
                                        
                    Trovare tutti gli annunci.
                    Filtrare per solo disponibili: %s.
                    """, onlyAvailable ? "Vero" : "Falso");

            switch (ScannerUtility.askFirstChar("Procedere? (S)i, (N)o o (A)nnulla")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
                case "a", "A" -> {
                    return;
                }
            }
        } while (confirmOp == null || !confirmOp);

        List<Annuncio> foundAnnunciList = new ArrayList<>();

        System.out.print("Ricerca degli annunci... ");

        DBResult dbResult = ViewController.cercareAnnunci(onlyAvailable, foundAnnunciList);

        printResult(dbResult, () -> {
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            }
        });

        ScannerUtility.askAny();
    }

    // T0000
    private static void visualizzareCategorie() {
        Boolean confirmOp = null;
        do {
            switch (ScannerUtility.askFirstChar("Visualizzare tutte le categorie?\n" +
                    "Procedere? (S)i, (N)o")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
            }
        } while (confirmOp == null);

        if (!confirmOp) return;

        System.out.print("Ricerca delle categorie... ");

        List<Categoria> categoriaList = new ArrayList<>();
        DBResult dbResult = ViewController.visualizzareCategorie(categoriaList);

        printResult(dbResult, () -> {
            System.out.println("Lista delle categorie:");
            for (Categoria categoria : categoriaList)
                System.out.println(categoria.toPrettyString());
        });

        ScannerUtility.askAny();
    }

    // R0001
    private static void creareReport() {
        Boolean confirmOp = null;
        do {
            switch (ScannerUtility.askFirstChar("Visualizzare report sulle vendite degli utenti?\n" +
                    "Procedere? (S)i, (N)o")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
            }
        } while (confirmOp == null);

        if (!confirmOp) return;

        System.out.print("Creazione del report... ");

        List<ReportEntry> reportEntries = new ArrayList<>();
        DBResult dbResult = ViewController.generareReport(reportEntries);

        printResult(dbResult, () -> {
            System.out.println("\nReport per utenti:");
            for (ReportEntry reportEntry : reportEntries)
                System.out.println(reportEntry.toPrettyString());
        });

        ScannerUtility.askAny();
    }

    // A0203
    private static void cercaPerDescrizione() {
        String descrizione;
        Boolean confirmOp;

        do {
            confirmOp = null;
            descrizione = ScannerUtility.askText("Testo nella descrizione", 5000);

            System.out.printf("""
                                        
                    Trovare tutti gli annunci disponibili la cui descrizione contiene:
                    "%s"
                    """, descrizione);

            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.print("\nRicerca degli annunci per descrizione... ");
        List<Annuncio> foundAnnunciList = new ArrayList<>();
        DBResult dbResult = ViewController.cercareAnnunciPerDescrizione(descrizione, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    private static void cercaPerCategoria() {
        String categoriaID;
        Boolean onlyAvailable;
        Boolean confirmOp;

        do {
            confirmOp = null;
            categoriaID = ScannerUtility.askString("Categoria", 30);
            onlyAvailable = null;
            do {
                switch (ScannerUtility.askFirstChar("Filtrare per solo disponibili? (S)i o (N)o")) {
                    case "s", "S" -> onlyAvailable = true;
                    case "n", "N" -> onlyAvailable = false;
                }
            } while (onlyAvailable == null);
            System.out.printf("""
                                        
                    Trovare tutti gli annunci della categoria %s e delle sue categorie figlie
                    Filtrare per solo disponibili: %s.
                    """, categoriaID, onlyAvailable ? "Vero" : "Falso");

            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.printf("\nRicerca degli annunci della categoria \"%s\" e delle categorie figlie... ", categoriaID);
        List<Annuncio> foundAnnunciList = new ArrayList<>();
        DBResult dbResult = ViewController.cercareAnnunciPerCategoria(categoriaID, onlyAvailable, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // A0202
    private static void cercaPerUtente() {
        String inserzionistaID;
        Boolean confirmOp;

        do {
            confirmOp = null;
            inserzionistaID = ScannerUtility.askString("Username", 30);

            System.out.printf("""
                                        
                    Trovare tutti gli annunci disponibili di %s.
                    """, inserzionistaID);

            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.printf("\nRicerca degli annunci di \"%s\"... ", inserzionistaID);
        List<Annuncio> foundAnnunciList = new ArrayList<>();
        DBResult dbResult = ViewController.cercareAnnunciPerInserzionista(inserzionistaID, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\nLista degli annunci:\n\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // A0400
    private static void controllareSeguiti() {
        Boolean confirmOp = null;
        do {
            switch (ScannerUtility.askFirstChar("Visualizzare gli annunci \"seguiti\" che hanno subito modifiche?\n" +
                    "Procedere? (S)i, (N)o")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
            }
        } while (confirmOp == null);

        if (!confirmOp) return;

        List<Annuncio> foundAnnunciList = new ArrayList<>();

        System.out.print("Ricerca degli annunci \"seguiti\" modificati... ");

        DBResult dbResult = ViewController.controllareAnnunciSeguiti(foundAnnunciList);

        printResult(dbResult, () -> {
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            }
        });

        ScannerUtility.askAny();
    }

    // A0301
    private static void stopSeguireAnnuncio() {
        Long numero;

        numero = askNumeroAnnuncio("Numero dell'annuncio da rimuovere dai \"seguiti\"");

        System.out.printf("Rimozione dell'annuncio %s dai \"seguiti\"... ", numero);

        DBResult dbResult = ViewController.stopSeguireAnnuncio(numero);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    // A0300
    private static void seguireAnnuncio() {
        Long numero;

        numero = askNumeroAnnuncio("Numero annuncio da aggiungere ai \"seguiti\"");

        if (numero == null) return;

        System.out.printf("Aggiunta dell'annuncio %s ai \"seguiti\"... ", numero);

        DBResult dbResult = ViewController.seguireAnnuncio(numero);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    // A0600
    private static void dettagliUtente() {
        Boolean confirmOp;
        String targetUsername;

        targetUsername = ScannerUtility.askString("Username dell'utente", 30);

        System.out.printf("""
                                
                Visualizzare i dettagli dell'utente "%s"?
                """, targetUsername);
        confirmOp = null;
        do {
            switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
                case "a", "A" -> {
                    return;
                }
            }
        } while (confirmOp == null);

        System.out.printf("Ricerca dei dettagli di \"%s\"... ", targetUsername);
        Utente targetUtente = new Utente(targetUsername);
        Anagrafica anagrafica = new Anagrafica();
        List<Recapito> recapitoList = new ArrayList<>();
        DBResult dbResult = ViewController.dettagliUtente(targetUtente, anagrafica, recapitoList);

        printResult(dbResult, () -> {
            System.out.println("\nDati di " + targetUsername + ":");
            System.out.println(anagrafica.toPrettyString(DATE_FORMAT));

            System.out.println("Recapito preferito:");
            System.out.println(recapitoList.get(0).toPrettyString(false));

            System.out.println("Altri recapiti:");
            for (Recapito recapito : recapitoList.subList(1, recapitoList.size())) {
                System.out.println(recapito.toPrettyString(false));
            }
        });

        ScannerUtility.askAny();
    }

    // A0500
    private static void vendereAnnuncio() {
        Long numero;
        Boolean confirmOp;
        do {
            numero = ScannerUtility.askLong("Numero identificativo dell'annuncio da vendere");

            System.out.printf("\nNumero ID: %s\n", numero);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.printf("Vendita dell'annuncio %d... ", numero);

        DBResult dbResult = ViewController.vendereAnnuncio(numero);
        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // M0100
    private static void visualizzareMessaggiConUtente() {
        String targetUsername;
        Boolean confirmOp;
        do {
            targetUsername = ScannerUtility.askString("Username dell'utente di cui visualizzare i messaggi scambiati", 30);

            System.out.printf("\nUsername: %s\n", targetUsername);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.printf("Ricerca dei messaggi scambiati con \"%s\"... ", targetUsername);

        List<MessaggioPrivato> messaggioPrivatoList = new ArrayList<>();
        DBResult dbResult = ViewController.visualizzareMessaggi(targetUsername, messaggioPrivatoList);

        printResult(dbResult, () -> {
            System.out.println("Messaggi:");
            for (MessaggioPrivato messaggioPrivato : messaggioPrivatoList)
                System.out.println(messaggioPrivato.toPrettyString(DATETIME_FORMAT));
        });

        ScannerUtility.askAny();
    }

    // M0101
    private static void visualizzareUtentiConMessaggi() {
        Boolean confirmOp = null;
        do {
            switch (ScannerUtility.askFirstChar("Visualizzare gli utenti con cui ci sono dei messaggi?\n" +
                    "Procedere? (S)i, (N)o")) {
                case "s", "S" -> confirmOp = true;
                case "n", "N" -> confirmOp = false;
            }
        } while (confirmOp == null);

        if (!confirmOp) return;

        List<String> usernameList = new ArrayList<>();

        System.out.println("Ricerca degli utenti con messaggi scambiati... ");

        DBResult dbResult = ViewController.visualizzareUtentiConMessaggi(usernameList);

        printResult(dbResult, () -> {
            System.out.println("Utenti con messaggi scambiati:");
            for (String username : usernameList) System.out.printf("\t%s\n", username);
        });

        ScannerUtility.askAny();
    }

    // M0000
    private static void inviareMessaggio() {
        String destinatario;
        String testo;
        Boolean confirmOp;

        do {
            destinatario = ScannerUtility.askText("Destinatario del messaggio privato", 30);
            testo = ScannerUtility.askText("Testo del messaggio", 250);

            System.out.printf("""
                                                
                    Destinatario: %s
                    Testo:
                    %s
                    """, destinatario, testo);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);

        } while (!confirmOp);

        System.out.print("Invio del messaggio... ");

        DBResult dbResult = ViewController.scrivereMessaggioPrivato(destinatario, testo);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    // A0100
    private static void dettagliAnnuncio() {
        Long numero;
        Boolean confirmOp;

        do {
            numero = ScannerUtility.askLong("Numero identificativo dell'annuncio di cui visualizzare i dettagli");

            System.out.printf("\nNumero: %s\n", numero);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        Annuncio annuncio = new Annuncio(numero);
        List<Commento> commentoList = new ArrayList<>();

        System.out.printf("\nRicerca dei dettagli dell'annuncio %s... ", numero);
        DBResult dbResult = ViewController.dettagliAnnuncio(annuncio, commentoList);
        if (dbResult.getResult()) {
            System.out.println("terminata con successo.");
            System.out.println(annuncio.toPrettyString(DATETIME_FORMAT));
            for (Commento commento : commentoList)
                System.out.println(commento.toPrettyString(DATETIME_FORMAT, false));
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // A0000
    private static void inserireAnnuncio() {
        String descrizione;
        String categoria;

        Boolean confirmOp;
        do {
            descrizione = ScannerUtility.askText("Descrizione dell'annuncio", 5000);
            categoria = ScannerUtility.askString("Categoria", 60);

            System.out.printf("""
                                                
                    Descrizione: %s
                    Categoria: %s
                    """, descrizione, categoria);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);

        } while (!confirmOp);

        Annuncio targetAnnuncio = new Annuncio(ActiveUser.getUsername(), descrizione, categoria, null);

        System.out.print("Inserimento annuncio... ");
        DBResult inserimentoResult = ViewController.inserireAnnuncio(targetAnnuncio);

        if (inserimentoResult.getResult()) {
            System.out.printf("terminato con successo con numero %d.\n", targetAnnuncio.getID());
        } else {
            System.out.printf("terminato con insuccesso (%s).\n", inserimentoResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // C0000
    private static void scrivereCommento() {
        Long numero;
        String testo;
        Boolean confirmOp;

        do {
            numero = ScannerUtility.askLong("Numero dell'annuncio su cui scrivere un commento");
            testo = ScannerUtility.askText("Testo del commento", 250);

            System.out.printf("""

                    Numero dell'annuncio: %s
                    Testo del commento: "%s"
                    """, numero, testo);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        System.out.print("Inserimento del commento... ");

        DBResult dbResult = ViewController.scrivereCommento(numero, testo);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    // G0000
    private static void creareCategoria() {
        String nomeCategoria, nomePadre;

        Boolean confirmOp, confirmPadre;

        do {
            nomeCategoria = ScannerUtility.askString("Nome della categoria da creare", 60);
            nomePadre = null;

            confirmPadre = null;
            do {
                switch (ScannerUtility.askFirstChar("La categoria è figlia di un'altra categoria? (S)i o (N)o")) {
                    case "s", "S" -> confirmPadre = true;
                    case "n", "N" -> confirmPadre = false;
                }
            } while (confirmPadre == null);

            if (confirmPadre)
                nomePadre = ScannerUtility.askString("Nome della categoria padre", 60);

            System.out.printf("""
                                                    
                    Nome della categoria: %s
                    Nome della categoria padre: %s
                    """, nomeCategoria, (nomePadre == null) ? "" : nomePadre);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return;
                    }
                }
            } while (confirmOp == null);

        } while (!confirmOp);

        System.out.printf("Inserimento della categoria \"%s\"... ", nomeCategoria);

        DBResult dbResult = ViewController.creareCategoria(nomeCategoria, nomePadre);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    private static Long askNumeroAnnuncio(String ask) {
        Long numero;
        Boolean confirmOp;

        do {
            numero = ScannerUtility.askLong(ask);

            System.out.printf("\nNumero: %s\n", numero);

            confirmOp = null;
            do {
                switch (ScannerUtility.askFirstChar("Confermare? (S)i, (N)o o (A)nnullare")) {
                    case "s", "S" -> confirmOp = true;
                    case "n", "N" -> confirmOp = false;
                    case "a", "A" -> {
                        return null;
                    }
                }
            } while (confirmOp == null);
        } while (!confirmOp);

        return numero;
    }

    private static void printResult(DBResult dbResult, Runnable codeIfSuccess) {
        if (dbResult.getResult()) {
            System.out.println("terminata con successo.");
            if (codeIfSuccess != null) {
                codeIfSuccess.run();
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }
    }

    private static void printResult(DBResult dbResult) {
        printResult(dbResult, null);
    }
}
