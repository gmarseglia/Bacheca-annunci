package View;

import Controller.BaseController;
import Controller.GestoreController;
import Model.*;
import Model.Exception.AnnuncioVendutoException;
import Utility.RndData;
import Utility.ScannerUtility;

import java.time.LocalDateTime;
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
        CONTROLLARE_MODIFICHE_SEGUITI,  //A0400
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
            OPERATION result = switch (input) {
                case "1" -> INSERIRE_ANNUNCIO;
                case "2" -> VENDERE_ANNUNCIO;
                case "3" -> CERCARE_PER_UTENTE;
                case "4" -> CERCARE_PER_CATEGORIA;
                case "5" -> CERCARE_PER_DESCRIZIONE;
                case "6" -> DETTAGLI_ANNUNCIO;
                case "7" -> SEGUIRE_ANNUNCIO;
                case "8" -> STOP_SEGUIRE_ANNUNCIO;
                case "9" -> CONTROLLARE_MODIFICHE_SEGUITI;
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

            return result;
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
            case CONTROLLARE_MODIFICHE_SEGUITI -> controllareModificheSeguiti();
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

    private static void creareReport() {
        /*
        #TODO
         */
    }

    private static void cercaPerDescrizione() {
        /*
        #TODO
         */
    }

    private static void cercaPerCategoria() {
        /*
        #TODO
         */
    }

    private static void cercaPerUtente() {
        /*
        #TODO
         */
    }

    private static void controllareModificheSeguiti() {
        /*
        #TODO
         */
    }

    private static void stopSeguireAnnuncio() {
        /*
        #TODO
         */
    }

    private static void seguireAnnuncio() {
        /*
        #TODO
         */
    }

    private static void dettagliInserzionista() {
        /*
        #TODO
         */
    }

    private static void vendereAnnuncio() {
        /*
        #TODO
         */
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

        MessaggioPrivato messaggioPrivato = new MessaggioPrivato(
                ActiveUser.getUsername(), destinatario, null, testo);
        boolean messageResult = BaseController.scrivereMessaggioPrivato(messaggioPrivato);
        System.out.printf("Messaggio inviato con %s.\n", messageResult ? "successo" : "insuccesso");
    }

    private static void dettagliAnnuncio() {
        long annuncioID;

        /*
        #TODO: ask users to give info
         */
        annuncioID = 1;

        Annuncio annuncio = new Annuncio(annuncioID);
        List<Commento> commentoList = new ArrayList<>();
        boolean dettaglioResult = BaseController.dettagliAnnuncio(annuncio, commentoList);

        //#TODO: improve
        if (dettaglioResult) {
            System.out.println(annuncio);
            for (Commento commento : commentoList) {
                System.out.println(commento);
            }
        } else {
            System.out.println("Dettagli annuncio: insuccesso.\n");
        }
    }

    protected static void inserireAnnuncio() {
        String descrizione;
        long prezzoInCent;
        String categoria;

        /*
        #TODO: Ask users to give info
         */
        descrizione = "Descrizione dell'annuncio.\n";
        prezzoInCent = 1050;
        categoria = "cat1";

        Annuncio targetAnnuncio = new Annuncio(ActiveUser.getUsername(), descrizione, prezzoInCent, categoria, LocalDateTime.now());

        boolean result = BaseController.inserireAnnuncio(targetAnnuncio);
        System.out.printf("L'annuncio %s è stato inserito con %s,\n",
                result ? targetAnnuncio.getID() : "",
                result ? "successo" : "insuccesso");
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
            System.out.printf("Il commento è stato scritto con %s.\n",
                    insertResult ? "successo" : "insuccesso");
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

        System.out.printf("La categoria %s%s è stata creata con %s.\n",
                categoria.getNome(),
                categoria.getPadre() != null ? " figlia di " + categoria.getPadre() : "",
                createResult ? "successo" : "insuccesso");
    }

}
