package Controller;

import DAO.DAO;
import Model.*;

import java.util.List;


public class BaseController {
    public static boolean inserireAnnuncio(Annuncio annuncio) {
        return DAO.insertAnnuncio(ActiveUser.getRole(), annuncio);
    }

    public static boolean scrivereCommento(Commento commento) {
        return DAO.insertCommento(ActiveUser.getRole(), commento);
    }

    public static boolean dettagliAnnuncio(Annuncio annuncio, List<Commento> commentoList) {
        return DAO.getDettagliAnnuncio(ActiveUser.getRole(), annuncio, commentoList);
    }

    public static boolean scrivereMessaggioPrivato(MessaggioPrivato messaggioPrivato) {
        return DAO.insertMessaggio(ActiveUser.getRole(), messaggioPrivato);
    }

    public static boolean visualizzareChat(String utenteID, List<String> utenteIDList) {
        return DAO.selectUtentiConMessaggi(ActiveUser.getRole(), utenteID, utenteIDList);
    }

    public static boolean visualizzareMessaggi(String utenteID1, String utenteID2, List<MessaggioPrivato> messaggioPrivatoList) {
        return DAO.selectMessaggiTraUtenti(ActiveUser.getRole(), utenteID1, utenteID2, messaggioPrivatoList);
    }

    public static boolean vendereAnnuncio(Long annuncioID){
        return DAO.updateAnnuncioVendere(ActiveUser.getRole(), annuncioID);
    }

    public static boolean dettagliUtente(Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList){
        return DAO.selectDettagliUtente(ActiveUser.getRole(), utente, anagrafica, recapitoList);
    }
}
