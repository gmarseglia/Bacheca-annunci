package View.Test;

import Controller.GestoreController;
import Model.ActiveUser;
import Model.Categoria;
import Model.Role;

import java.util.ArrayList;
import java.util.List;

public class CreareCategoriaTest {

    public static void main(String[] args) {
        List<Categoria> categoriaList = new ArrayList<>();
        Categoria cat1 = new Categoria("cat1");
        Categoria cat11 = new Categoria("cat1.1", cat1.getID());
        Categoria cat111 = new Categoria("cat1.1.1", cat11.getID());
        Categoria cat12 = new Categoria("cat1.2", cat1.getID());
        categoriaList.add(cat1);
        categoriaList.add(cat11);
        categoriaList.add(cat111);
        categoriaList.add(cat12);

        ActiveUser.setRole(Role.GESTORE);
        boolean result;
        for (Categoria targetCategoria : categoriaList) {
            result = GestoreController.creareCategoria(targetCategoria);
            System.out.printf("Inserimento di %s : %s\n",
                    targetCategoria.getNome(),
                    result ? "OK" : "NOT OK");
        }

    }
}
