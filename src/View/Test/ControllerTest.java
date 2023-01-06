package View.Test;

import Controller.BaseController;
import Controller.GestoreController;
import Controller.RegistrationController;
import DAO.DAO;
import Model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ControllerTest {

    public static void main(String[] args) throws InterruptedException {
        ActiveUser.setRole(Role.ROOT);

        System.out.print("Reset del DB... ");
        DAO.deleteAllUtente();
        DAO.deleteAllCategoria(ActiveUser.getRole());
        DAO.resetAutoincrement();
        System.out.print("completato.\n");

        String username = "user";
        String password = "pass";
        Role role = Role.GESTORE;
        String codiceFiscale = "KRTMRA98L10H501E";
        String nome = "mario";
        String cognome = "kart";
        Sesso sesso = Sesso.UOMO;
        LocalDate dataNascita = LocalDate.of(1998, 7, 10);
        String comuneNascita = "roma";
        String indirizzoResidenza = "indirizzo";
        String indirizzoFatturazione = null;
        List<Recapito> recapitoList = new ArrayList<>();
        recapitoList.add(new Recapito("email@email.com", TipoRecapito.EMAIL, codiceFiscale));
        recapitoList.add(new Recapito("339123456", TipoRecapito.CELLULARE, codiceFiscale));

        Utente utente = new Utente(username);
        Credenziali credenziali = new Credenziali(username, password, role);
        Anagrafica anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username);
        if (indirizzoFatturazione != null) anagrafica.setIndirizzoFatturazione(indirizzoFatturazione);

        boolean registrationResult;
        registrationResult = RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);
        printResult("Registrazione", registrationResult);

        ActiveUser.setRole(Role.GESTORE);
        ActiveUser.setUsername(username);

        Categoria categoria = new Categoria("categoria_1");
        boolean categoriaResult = GestoreController.creareCategoria(categoria);
        printResult("Creazione categoria", categoriaResult);

        Annuncio annuncio = new Annuncio(username, "Descrizione annuncio.\n", 10 * 100, categoria.getID(), null);
        boolean annuncioResult = BaseController.inserireAnnuncio(annuncio);
        printResult("Inserimento annuncio", annuncioResult);

        for (int commIndex = 0; commIndex < 5; commIndex++) {
            Commento commento = new Commento(username, annuncio.getID(), null,
                    String.format("Testo del commento %d.\n", commIndex));
            boolean commentoResult = BaseController.scrivereCommento(commento);
            printResult(String.format("Scrivere commento %d", commIndex), commentoResult);
            Thread.sleep(1000);
        }

        Annuncio annuncioDettaglio = new Annuncio(annuncio.getID());
        List<Commento> commentoListDettaglio = new ArrayList<>();
        boolean dettaglioResult = BaseController.dettagliAnnuncio(annuncioDettaglio, commentoListDettaglio);
        if (dettaglioResult) {
            System.out.println(annuncioDettaglio);
            for (Commento commento : commentoListDettaglio) {
                System.out.println(commento);
            }
        } else {
            System.out.println("Dettagli annuncio: NOT OK.\n");
        }
    }

    private static void printResult(String operation, boolean result) {
        System.out.printf("%s: %s.\n", operation, result ? "OK" : "NOT OK");
    }


}
