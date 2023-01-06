package Controller;

import DAO.DAO;
import Model.ActiveUser;
import Model.Annuncio;
import Model.Commento;

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
}
