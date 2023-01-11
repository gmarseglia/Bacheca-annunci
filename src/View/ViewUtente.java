package View;

import Controller.BaseController;
import Controller.GestoreController;
import DAO.DBResult;
import Model.*;
import Model.Exception.AnnuncioVendutoException;
import Utility.RndData;
import Utility.ScannerUtility;

import java.util.ArrayList;
import java.util.List;

public class ViewUtente {

    protected enum OPERATION {
        INSERIRE_ANNUNCIO,              //A0000
        DETTAGLI_ANNUNCIO,              //A0100
        CERCARE_PER_CATEGORIA,          //A0200
        CERCARE_PER_UTENTE,             //A0202
        CERCARE_PER_DESCRIZIONE,        //A0203
        SEGUIRE_ANNUNCIO,               //A0300
        STOP_SEGUIRE_ANNUNCIO,          //A0301
        CONTROLLARE_SEGUITI,  //A0400
        VENDERE_ANNUNCIO,               //A0500
        DETTAGLI_INSERZIONISTA,         //A0600
        INVIARE_MESSAGGIO,              //M0000
        MESSAGGI_CON_UTENTE,            //M0100
        VISUALIZZARE_CHAT,              //M0101
        SCRIVERE_COMMENTO,              //C0000
        CREARE_CATEGORIA,               //G0000
        CREARE_REPORT,                  //R0001
        TERMINARE_APPLICAZIONE;


        public static OPERATION dispatchMap(String input) {
            return switch (input) {
                case "1" -> INSERIRE_ANNUNCIO;
                case "2" -> VENDERE_ANNUNCIO;
                case "3" -> CERCARE_PER_UTENTE;
                case "4" -> CERCARE_PER_CATEGORIA;
                case "5" -> CERCARE_PER_DESCRIZIONE;
                case "6" -> DETTAGLI_ANNUNCIO;
                case "7" -> SEGUIRE_ANNUNCIO;
                case "8" -> STOP_SEGUIRE_ANNUNCIO;
                case "9" -> CONTROLLARE_SEGUITI;
                case "a", "A" -> DETTAGLI_INSERZIONISTA;
                case "b", "B" -> SCRIVERE_COMMENTO;
                case "c", "C" -> VISUALIZZARE_CHAT;
                case "d", "D" -> MESSAGGI_CON_UTENTE;
                case "e", "E" -> INVIARE_MESSAGGIO;
                case "f", "F" -> CREARE_CATEGORIA;
                case "g", "G" -> CREARE_REPORT;
                case "u", "U" -> TERMINARE_APPLICAZIONE;
                default -> null;
            };
        }
    }

    private static final String MAIN_DISPATCH = """
            Operazioni possibili:
            (1) Inserire un annuncio.
            (2) Indicare un annuncio inserito come venduto.
            (3) Cercare gli annunci di un utente.
            (4) Cercare gli annunci disponibili per categoria.
            (5) Cercare gli annunci disponibili per descrizione.
            (6) Visualizzare i dettagli di un annuncio.
            (7) Aggiungere un annuncio disponibile ai "seguiti".
            (8) Rimuovere un annuncio dai "seguiti".
            (9) Controllare quali annunci "seguiti" hanno subito modifiche.
            (A) Visualizzare i dettagli di un utente.
            (B) Scrivere un commento sotto un annuncio disponibile.
            (C) Visualizzare utenti con cui sono stati scambiati messaggi privati.
            (D) Visualizzare tutti i messaggi privati scambiati con un utente.
            (E) Inviare un messaggio privato ad un utente.
            (F) Creare una categoria.
            (G) Generare report sulla percentuale di vendita degli utenti.
            (U) Uscire dall'applicazione.
            """;

    public static void main(String[] args) {
        ActiveUser.setUsername("user");
        ActiveUser.setRole(Role.GESTORE);
        begin();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void begin() {

        OPERATION operation;
        while (true) {
            System.out.println();
            do {
                operation = OPERATION.dispatchMap(ScannerUtility.askFirstChar(MAIN_DISPATCH));
            } while (operation == null);

            dispatch(operation);
        }

    }

    protected static void dispatch(OPERATION operation) {
        switch (operation) {
            case INSERIRE_ANNUNCIO -> inserireAnnuncio();
            case VENDERE_ANNUNCIO -> vendereAnnuncio();
            case CERCARE_PER_UTENTE -> cercaPerUtente();
            case CERCARE_PER_CATEGORIA -> cercaPerCategoria();
            case CERCARE_PER_DESCRIZIONE -> cercaPerDescrizione();
            case DETTAGLI_ANNUNCIO -> dettagliAnnuncio();
            case SEGUIRE_ANNUNCIO -> seguireAnnuncio();
            case STOP_SEGUIRE_ANNUNCIO -> stopSeguireAnnuncio();
            case CONTROLLARE_SEGUITI -> controllareSeguiti();
            case DETTAGLI_INSERZIONISTA -> dettagliInserzionista();
            case SCRIVERE_COMMENTO -> scrivereCommento();
            case VISUALIZZARE_CHAT -> visualizzareChat();
            case MESSAGGI_CON_UTENTE -> messaggiConUtente();
            case INVIARE_MESSAGGIO -> inviareMessaggio();
            case CREARE_CATEGORIA, CREARE_REPORT -> gestoreDispatch(operation);
            case TERMINARE_APPLICAZIONE -> {
                System.out.println("Uscita dall'applicazione.");
                System.exit(0);
            }
        }
    }

    protected static void gestoreDispatch(OPERATION operation) {
        if (ActiveUser.getRole() != Role.GESTORE) {
            System.out.println("Impossibile svolgere le operazioni con gli attuali privilegi.\n");
            return;
        }

        switch (operation) {
            case CREARE_CATEGORIA -> creareCategoria();
            case CREARE_REPORT -> creareReport();
        }
    }

    private static void creareReport() {
        /*
        #TODO
         */
    }

    private static void cercaPerDescrizione() {
        String descrizione;
        Boolean onlyAvailable;
        Boolean confirmOp;

        do {
            confirmOp = null;
            descrizione = ScannerUtility.askText("Testo nella descrizione", 5000);
            onlyAvailable = null;
            do {
                switch (ScannerUtility.askFirstChar("Filtrare per solo disponibili? (S)i o (N)o")) {
                    case "s", "S" -> onlyAvailable = true;
                    case "n", "N" -> onlyAvailable = false;
                }
            } while (onlyAvailable == null);
            System.out.printf("""
                                        
                    Trovare tutti gli annunci la cui descrizione contiene:
                    "%s"
                    Filtrare per solo disponibili: %s.
                    """, descrizione, onlyAvailable ? "Vero" : "Falso");

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
        DBResult dbResult = BaseController.cercareAnnunciPerDescrizione(descrizione, onlyAvailable, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio);
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
        DBResult dbResult = BaseController.cercareAnnunciPerCategoria(categoriaID, onlyAvailable, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio);
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    private static void cercaPerUtente() {
        String inserzionistaID;
        Boolean onlyAvailable;
        Boolean confirmOp;

        do {
            confirmOp = null;
            inserzionistaID = ScannerUtility.askString("Username", 30);
            onlyAvailable = null;
            do {
                switch (ScannerUtility.askFirstChar("Filtrare per solo disponibili? (S)i o (N)o")) {
                    case "s", "S" -> onlyAvailable = true;
                    case "n", "N" -> onlyAvailable = false;
                }
            } while (onlyAvailable == null);
            System.out.printf("""
                                        
                    Trovare tutti gli annunci di %s
                    Filtrare per solo disponibili: %s.
                    """, inserzionistaID, onlyAvailable ? "Vero" : "Falso");

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
        DBResult dbResult = BaseController.cercareAnnunciPerInserzionista(inserzionistaID, onlyAvailable, foundAnnunciList);

        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
            for (Annuncio annuncio : foundAnnunciList) {
                System.out.println(annuncio);
            }
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    // (9)
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

        System.out.println("Ricerca degli annunci \"seguiti\" modificati... ");

        DBResult dbResult = BaseController.controllareAnnunciSeguiti(foundAnnunciList);

        printResult(dbResult, () -> {
            for (Annuncio annuncio : foundAnnunciList) {
                //TODO: Issue 21
                System.out.println(annuncio);
            }
        });

        ScannerUtility.askAny();
    }

    // (8)
    private static void stopSeguireAnnuncio() {
        Long numero;

        numero = askNumeroAnnuncio("Numero dell'annuncio da rimuovere dai \"seguiti\"");

        System.out.printf("Rimozione dell'annuncio %s dai \"seguiti\"... ", numero);

        DBResult dbResult = BaseController.stopSeguireAnnuncio(numero);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    // (7)
    private static void seguireAnnuncio() {
        Long numero;

        numero = askNumeroAnnuncio("Numero annuncio da aggiungere ai \"seguiti\"");

        if (numero == null) return;

        System.out.printf("Aggiunta dell'annuncio %s ai \"seguiti\"... ", numero);

        DBResult dbResult = BaseController.seguireAnnuncio(numero);

        printResult(dbResult);

        ScannerUtility.askAny();
    }

    private static void dettagliInserzionista() {
        /*
        #TODO
         */
    }

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

        DBResult dbResult = BaseController.vendereAnnuncio(numero);
        if (dbResult.getResult()) {
            System.out.print("terminata con successo.\n");
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    private static void messaggiConUtente() {
        /*
        #TODO
         */
    }

    private static void visualizzareChat() {
        /*
        #TODO
         */
    }

    private static void inviareMessaggio() {
        String destinatario;
        String testo;

        /*
        #TODO: ask users to give info
         */
        destinatario = "user2";
        testo = "prova";

        MessaggioPrivato messaggioPrivato = new MessaggioPrivato(ActiveUser.getUsername(), destinatario, null, testo);
        boolean messageResult = BaseController.scrivereMessaggioPrivato(messaggioPrivato);
        System.out.printf("Messaggio inviato con %s.\n", messageResult ? "successo" : "insuccesso");
    }

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
        DBResult dbResult = BaseController.dettagliAnnuncio(annuncio, commentoList);
        if (dbResult.getResult()) {
            System.out.println("terminata con successo.");
            //TODO: Issue #21
            System.out.println(annuncio);
            for (Commento commento : commentoList)
                //TODO: Issue #22
                System.out.println(commento);
        } else {
            System.out.printf("terminata con insuccesso (%s).\n", dbResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    private static void inserireAnnuncio() {
        String descrizione;
        float prezzo;
        String categoria;

        Boolean confirmOp;
        do {
            descrizione = ScannerUtility.askText("Descrizione dell'annuncio", 5000);
            prezzo = ScannerUtility.askFloat("Prezzo");
            categoria = ScannerUtility.askString("Categoria", 60);

            System.out.printf("""
                                                
                    Descrizione: %s
                    Prezzo: %.2f
                    Categoria: %s
                    """, descrizione, prezzo, categoria);

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

        Annuncio targetAnnuncio = new Annuncio(ActiveUser.getUsername(), descrizione, prezzo, categoria, null);

        System.out.print("Inserimento annuncio... ");
        DBResult inserimentoResult = BaseController.inserireAnnuncio(targetAnnuncio);

        if (inserimentoResult.getResult()) {
            System.out.printf("terminato con successo con numero %d.\n", targetAnnuncio.getID());
        } else {
            System.out.printf("terminato con insuccesso (%s).\n", inserimentoResult.getMessage());
        }

        ScannerUtility.askAny();
    }

    private static void scrivereCommento() {
        String testo;
        long annuncioID;

        /*
        #TODO: ask users to give info
         */
        testo = "Testo del commento.";
        annuncioID = 1;
        Commento commento = new Commento(ActiveUser.getUsername(), annuncioID, null, testo);

        boolean insertResult = false;
        try {
            insertResult = BaseController.scrivereCommento(commento);
            System.out.printf("Il commento è stato scritto con %s.\n", insertResult ? "successo" : "insuccesso");
        } catch (AnnuncioVendutoException e) {
            System.out.println("L'annuncio è già stato venduto, impossibile aggiungere nuovi commenti.\n");
        }

    }

    private static void creareCategoria() {
        String nome, padre;

        boolean random = false;
        if (random) {
            nome = RndData.randomString(15);
            padre = RndData.randomString(15);
        } else {
            nome = "cat1";
            padre = null;
        }

        Categoria categoria = new Categoria(nome, padre);

        boolean createResult = GestoreController.creareCategoria(categoria);

        System.out.printf("La categoria %s%s è stata creata con %s.\n", categoria.getNome(), categoria.getPadre() != null ? " figlia di " + categoria.getPadre() : "", createResult ? "successo" : "insuccesso");
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
