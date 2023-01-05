package Controller;

import Model.ActiveUser;
import Model.Categoria;
import DAO.DAO;

public class GestoreController {
    public static boolean creareCategoria(Categoria categoria) {
        return DAO.insertCategoria(ActiveUser.getRole(), categoria);
    }
}
