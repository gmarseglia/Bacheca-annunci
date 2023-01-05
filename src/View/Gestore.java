package View;

import Controller.GestoreController;
import Model.Categoria;
import Utility.RndData;

public class Gestore extends Base {

    protected static void gestoreDispatch(OPERATION operation) {
        switch (operation) {
            case CREARE_CATEGORIA -> creareCategoria();
            default -> {
                if (false) begin(); //#TODO
            }
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
                createResult ? "successo" : "insuccesso"
                );
    }
}
