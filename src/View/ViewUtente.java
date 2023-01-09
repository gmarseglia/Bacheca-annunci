package View;

import Controller.BaseController;
import Controller.GestoreController;
import Model.*;
import Utility.RndData;

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
        STOP_SEGUIRE_VENDUTI,           //A0302
        CONTROLLARE_MODIFICHE_SEGUITI,  //A0400
        VENDERE_ANNUNCIO,               //A0500
        DETTAGLI_INSERZIONISTA,         //A0600
        INVIARE_MESSAGGIO,              //M0000
        MESSAGGI_CON_UTENTE,            //M0100
        VISUALIZZARE_CHAT,              //M0101
        SCRIVERE_COMMENTO,              //C0000
        CREARE_CATEGORIA,               //G0000
        CREARE_REPORT                   //R0001

    }

    public static void begin() {
        /*
        #TODO: while cycle to dispatch
         */
        OPERATION operation;
        operation = OPERATION.SCRIVERE_COMMENTO;
        dispatch(operation);
    }

    protected static void gestoreDispatch(OPERATION operation) {
        if (ActiveUser.getRole() != Role.GESTORE) {
            System.out.println("Impossibile svolgere le operazioni con gli attuali privilegi.\n");
            return;
        }

        switch (operation) {
            case CREARE_CATEGORIA -> creareCategoria();
            default -> {
                if (false) begin(); //#TODO
            }
        }
    }

    protected static void dispatch(OPERATION operation) {
        switch (operation) {
            case INSERIRE_ANNUNCIO -> inserireAnnuncio();
            case SCRIVERE_COMMENTO -> scrivereCommento();
            case DETTAGLI_ANNUNCIO -> dettagliAnnuncio();
            case INVIARE_MESSAGGIO -> inviareMessaggio();
            case VISUALIZZARE_CHAT -> visualizzareChat();
            case MESSAGGI_CON_UTENTE -> messaggiConUtente();
            case VENDERE_ANNUNCIO -> vendereAnnuncio();
            case DETTAGLI_INSERZIONISTA -> dettagliInserzionista();
            case CREARE_CATEGORIA -> gestoreDispatch(operation);
            case SEGUIRE_ANNUNCIO -> seguireAnnuncio();
            default -> {
                if (false) begin(); //#TODO
            }
        }
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
