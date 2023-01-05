package View;

import java.util.spi.AbstractResourceBundleProvider;

public class Base {

    protected enum OPERATION {
        //A0000
        INSERIRE_ANNUNCIO,
        //G0000
        CREARE_CATEGORIA
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
                gestoreDispatch(operation);
                break;
            default:
                if (false) begin(); //#TODO
        }
    }

    protected static void gestoreDispatch(OPERATION operation) {
        System.out.println("Impossibile eseguire operazione con gli attuali privilegi.");
        if (false) begin(); //#TODO:
    }

    protected static void inserireAnnuncio() {
        //#TODO: need inserireCategoria first
    }
}
