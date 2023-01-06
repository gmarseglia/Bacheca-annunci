package View;

import Controller.BaseController;
import Controller.GestoreController;
import Model.ActiveUser;
import Model.Annuncio;
import Model.Categoria;
import Model.Role;
import Utility.RndData;

import java.time.LocalDateTime;

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
        CREARE_REPORT                   //R0001

    }

    public static void begin() {
        /*
        #TODO: while cycle to dispatch
         */
        OPERATION operation;
        operation = OPERATION.INSERIRE_ANNUNCIO;
        dispatch(operation);
    }

    protected static void dispatch(OPERATION operation) {
        switch (operation) {
            case INSERIRE_ANNUNCIO:
                inserireAnnuncio();
                break;
            case CREARE_CATEGORIA:
            case CREARE_REPORT:
                gestoreDispatch(operation);
                break;
            default:
                if (false) begin(); //#TODO
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
            default -> {
                if (false) begin(); //#TODO
            }
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

    private static void creareCategoria() {
        String nome, padre;

        boolean random = false;
        if (random) {
            nome = RndData.randomString(15);
            padre = RndData.randomString(15);
        } else {
            nome = "cat3";
            padre = null;
        }

        Categoria categoria = new Categoria(nome, padre);

        boolean createResult = GestoreController.creareCategoria(categoria);

        System.out.printf("La categoria %s%s è stata creata con %s.\n",
                categoria.getNome(),
                categoria.getPadre() != null ? " figlia di " + categoria.getPadre() : "",
                createResult ? "successo" : "insuccesso");
    }

    private static void creareReport() {
        //#TODO
    }


}
