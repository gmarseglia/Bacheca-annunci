package Controller;

import DAO.DAO;
import Model.ActiveUser;
import Model.Annuncio;

public class BaseController {
    public static boolean inserireAnnuncio(Annuncio annuncio) {
        return DAO.insertAnnuncio(ActiveUser.getRole(), annuncio);
    }
}
