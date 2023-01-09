package View.Test;

import Controller.BaseController;
import Controller.GestoreController;
import Controller.RegistrationController;
import DAO.DAO;
import Model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewControllerTest {

    public static void main(String[] args) throws InterruptedException {
        ActiveUser.setRole(Role.ROOT);

        System.out.print("Reset del DB... ");
        DAO.deleteAllUtente();
        DAO.deleteAllCategoria(ActiveUser.getRole());
        DAO.resetAutoincrement();
        System.out.print("completato.\n");

        String username = "user";
        String username2 = "user2";
        String username3 = "user3";
        String password = "pass";
        Role role = Role.GESTORE;
        Role role2 = Role.BASE;
        String codiceFiscale = "KRTMRA98L10H501E";
        String codiceFiscale2 = "KRTMRA98L10H501F";
        String codiceFiscale3 = "KRTMRA98L10H501G";
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
        recapitoList.add(new Recapito("339123457", TipoRecapito.CELLULARE, codiceFiscale));
        List<Recapito> recapitoList2 = new ArrayList<>();
        recapitoList2.add(new Recapito("prova@email.com", TipoRecapito.EMAIL, codiceFiscale2));
        List<Recapito> recapitoList3 = new ArrayList<>();
        recapitoList3.add(new Recapito("prova3@email.com", TipoRecapito.EMAIL, codiceFiscale2));

        Utente utente = new Utente(username);
        Utente utente2 = new Utente(username2);
        Utente utente3 = new Utente(username3);
        Credenziali credenziali = new Credenziali(username, password, role);
        Credenziali credenziali2 = new Credenziali(username2, password, role2);
        Credenziali credenziali3 = new Credenziali(username3, password, role2);
        Anagrafica anagrafica = new Anagrafica(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username);
        if (indirizzoFatturazione != null) anagrafica.setIndirizzoFatturazione(indirizzoFatturazione);
        Anagrafica anagrafica2 = new Anagrafica(codiceFiscale2, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username2);
        Anagrafica anagrafica3 = new Anagrafica(codiceFiscale3, nome, cognome, sesso, dataNascita, comuneNascita, indirizzoResidenza, username3);

        boolean registrationResult;
        registrationResult = RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList);
        registrationResult &= RegistrationController.registrazioneUtente(utente2, credenziali2, anagrafica2, recapitoList2);
        registrationResult &= RegistrationController.registrazioneUtente(utente3, credenziali3, anagrafica3, recapitoList3);
        printResult("Registrazione multipla", registrationResult);

        ActiveUser.setRole(Role.GESTORE);
        ActiveUser.setUsername(username);

        boolean dettagliUtenteResult;
        Utente utenteDettagli = new Utente(utente.getID());
        Anagrafica anagraficaDettagli = new Anagrafica();
        List<Recapito> recapitoDettaglioList = new ArrayList<>();
        dettagliUtenteResult = BaseController.dettagliUtente(utenteDettagli, anagraficaDettagli, recapitoDettaglioList);
        printResult("Dettagli utente", dettagliUtenteResult);

        Categoria categoria = new Categoria("categoria_1");
        boolean categoriaResult = GestoreController.creareCategoria(categoria);
        printResult("Creazione categoria", categoriaResult);

        Annuncio annuncio = new Annuncio(username, "Descrizione annuncio.", 10 * 100, categoria.getID(), null);
        boolean annuncioResult = BaseController.inserireAnnuncio(annuncio);
        printResult("Inserimento annuncio", annuncioResult);

        for (int commIndex = 0; commIndex < 2; commIndex++) {
            Commento commento = new Commento(username, annuncio.getID(), null,
                    String.format("Testo del commento %d.", commIndex));
            boolean commentoResult;
            try {
                commentoResult = BaseController.scrivereCommento(commento);
                printResult(String.format("Scrivere commento %d", commIndex), commentoResult);
            } catch (AnnuncioVendutoException e) {
                printResult("Scrivere commento %d", false, "Annuncio già venduto");
            }
            Thread.sleep(1100);
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

        MessaggioPrivato messaggioPrivato = new MessaggioPrivato(
                utente.getUsername(), utente2.getUsername(), null, "Messaggio privato.");
        MessaggioPrivato messaggioPrivato1 = new MessaggioPrivato(
                utente3.getUsername(), utente.getUsername(), null, "Messaggio privato 2");
        MessaggioPrivato messaggioPrivato2 = new MessaggioPrivato(
                utente.getUsername(), utente3.getUsername(), null, "Messaggio privato 2");
        boolean messageResult = BaseController.scrivereMessaggioPrivato(messaggioPrivato);
        Thread.sleep(1100);
        messageResult &= BaseController.scrivereMessaggioPrivato(messaggioPrivato1);
        Thread.sleep(1100);
        messageResult &= BaseController.scrivereMessaggioPrivato(messaggioPrivato2);
        Thread.sleep(1100);
        messageResult &= BaseController.scrivereMessaggioPrivato(messaggioPrivato1);
        printResult("Scrivere Messaggio Privato", messageResult);

        List<String> utenteIDList = new ArrayList<>();
        boolean chatResult = BaseController.visualizzareChat(utente.getID(), utenteIDList);
        printResult("Visualizzare chat", chatResult, utenteIDList);

        List<MessaggioPrivato> messaggioPrivatoList = new ArrayList<>();
        boolean messageSelectResult = BaseController.visualizzareMessaggi(utente.getID(), utente3.getID(), messaggioPrivatoList);
        printResultList("Visualizzare messaggi fra utenti", messageSelectResult, messaggioPrivatoList);

        Segue segue = new Segue(utente.getID(), annuncio.getID());
        boolean segueResult;
        try {
            segueResult = BaseController.seguireAnnuncio(segue);
            printResult("Seguire annuncio", segueResult);
        } catch (AnnuncioVendutoException e) {
            printResult("Seguire annuncio", false, "Annuncio già venduto");
        }

        boolean vendereResult;
        try {
            vendereResult = BaseController.vendereAnnuncio(annuncio.getID());
            Annuncio annuncioVenduto = new Annuncio(annuncio.getID());
            BaseController.dettagliAnnuncio(annuncioVenduto, new ArrayList<>());
            printResult("Vendere annuncio #1", vendereResult, annuncioVenduto);
        } catch (AnnuncioVendutoException e) {
            printResult("Vendere annuncio #1", false, "Annuncio già venduto.");
        }

        boolean vendere2Result;
        try {
            vendere2Result = BaseController.vendereAnnuncio(annuncio.getID());
            Annuncio annuncioGiaVenduto = new Annuncio(annuncio.getID());
            BaseController.dettagliAnnuncio(annuncioGiaVenduto, new ArrayList<>());
            printResult("Vendere annuncio", vendere2Result, annuncioGiaVenduto);
        } catch (AnnuncioVendutoException e) {
            printResult("Vendere annuncio #2", false, "Annuncio già venduto.");
        }


    }

    private static void printResult(String operation, boolean result, Object o, String explain) {
        List<Object> iterable = new ArrayList<>();
        iterable.add(o);
        printResultList(operation, result, iterable, explain);
    }

    private static void printResult(String operation, boolean result, String explain) {
        printResult(operation, result, null, explain);
    }

    private static void printResult(String operation, boolean result) {
        printResult(operation, result, "");
    }

    private static void printResult(String operation, boolean result, Object object) {
        printResult(operation, result, object, "");
    }

    private static void printResultList(String operation, boolean result, Iterable iterable) {
        printResultList(operation, result, iterable, "");
    }

    private static void printResultList(String operation, boolean result, Iterable iterable, String explain) {
        System.out.printf("--> %s: %s.\n", operation, result ? "OK" :
                (explain.equals("")) ? "NOT OK" :
                        String.format("NOT OK due to '%s'", explain));
        if (iterable != null) {
            for (Object o : iterable)
                if (o != null) System.out.println(o);
        }
    }


}