package View.Test;

import Controller.BaseController;
import Controller.GestoreController;
import Controller.RegistrationController;
import DAO.DAO;
import Model.*;
import Model.Exception.AnnuncioVendutoException;

import java.sql.SQLException;
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

        boolean registrationResult = true;
        registrationResult &= RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList).getExtraResult();
        registrationResult &= RegistrationController.registrazioneUtente(utente2, credenziali2, anagrafica2, recapitoList2).getExtraResult();
        registrationResult &= RegistrationController.registrazioneUtente(utente3, credenziali3, anagrafica3, recapitoList3).getExtraResult();
        printResult("Registrazione multipla", registrationResult);

        printResult("Registrazione che infrange KEY",
                RegistrationController.registrazioneUtente(utente, credenziali, anagrafica, recapitoList).getExtraResult());

        ActiveUser.setRole(Role.GESTORE);
        ActiveUser.setUsername(username);

        boolean dettagliUtenteResult;
        Utente utenteDettagli = new Utente(utente.getID());
        Anagrafica anagraficaDettagli = new Anagrafica();
        List<Recapito> recapitoDettaglioList = new ArrayList<>();
        dettagliUtenteResult = BaseController.dettagliUtente(utenteDettagli, anagraficaDettagli, recapitoDettaglioList);
        printResult("Dettagli utente", dettagliUtenteResult);

        Categoria categoria = new Categoria("cat_1");
        boolean categoriaResult = GestoreController.creareCategoria(categoria);
        printResult("Creazione cat_1", categoriaResult);

        Categoria categoria1 = new Categoria("cat_1-1", categoria.getID());
        printResult("Creazione cat_1-1", GestoreController.creareCategoria(categoria1));

        Categoria cat2 = new Categoria("cat_2");
        printResult("Creazione cat_2", GestoreController.creareCategoria(cat2));

        Annuncio annuncio_1 = new Annuncio(username, "Descrizione annuncio.", 10 * 100, categoria.getID(), null);
        boolean annuncioResult = BaseController.inserireAnnuncio(annuncio_1).getResult();
        printResult("Inserimento annuncio_1", annuncioResult);

        Annuncio annuncio_1_1 = new Annuncio(username, "Descrizione annuncio.", 10 * 100, cat2.getID(), null);
        printResult("Inserimento annuncio_1", BaseController.inserireAnnuncio(annuncio_1_1).getResult());

        Annuncio annuncio_2 = new Annuncio(utente2.getUsername(), "Questo è un annuncio.", 10 * 100, categoria.getID(), null);
        printResult("Inserimento annuncio_2", BaseController.inserireAnnuncio(annuncio_2).getResult());

        Annuncio annuncio_3 = new Annuncio(utente.getUsername(), "Questo è un dragoooooo.", 10 * 100, categoria1.getID(), null);
        printResult("Inserimento annuncio_3", BaseController.inserireAnnuncio(annuncio_3).getResult());

        List<Annuncio> annunciSeguitiModificati = new ArrayList<>();
        boolean controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList("Controllo seguiti vuoto", controlloSeguiti1Result, annunciSeguitiModificati);


        boolean segueResult;

        segueResult = BaseController.seguireAnnuncio(annuncio_1.getID()).getResult();
        printResult("Seguire annuncio da utente", segueResult);


        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList("Controllo seguiti annuncio pulito", controlloSeguiti1Result, annunciSeguitiModificati);
        Thread.sleep(1100);

        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList(" Secondo controllo seguiti annuncio pulito", controlloSeguiti1Result, annunciSeguitiModificati);

        for (int commIndex = 0; commIndex < 1; commIndex++) {
            Commento commento = new Commento(username, annuncio_1.getID(), null,
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

        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList(" Controllo seguiti annuncio dopo commenti", controlloSeguiti1Result, annunciSeguitiModificati);
        Thread.sleep(1100);

        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList(" Secondo controllo seguiti annuncio dopo commenti", controlloSeguiti1Result, annunciSeguitiModificati);

        Annuncio annuncioDettaglio = new Annuncio(annuncio_1.getID());
        List<Commento> commentoListDettaglio = new ArrayList<>();
        boolean dettaglioResult = BaseController.dettagliAnnuncio(annuncioDettaglio, commentoListDettaglio).getResult();
        if (dettaglioResult) {
            System.out.println("--> Dettagli annuncio: OK");
            System.out.println(annuncioDettaglio);
            for (Commento commento : commentoListDettaglio) {
                System.out.println(commento);
            }
        } else {
            System.out.println("--> Dettagli annuncio: NOT OK");
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

        Segue segue2 = new Segue(utente2.getID(), annuncio_1.getID());
        boolean segueResult2;
        String oldUsername = ActiveUser.getUsername();
        ActiveUser.setUsername(utente2.getID());
        segueResult2 = BaseController.seguireAnnuncio(annuncio_1.getID()).getResult();
        printResult("Seguire annuncio da utente2", segueResult2);
        ActiveUser.setUsername(oldUsername);


        boolean deleteSegue2Result = BaseController.stopSeguireAnnuncio(segue2);
        printResult("Stop seguire annuncio utente 2", deleteSegue2Result);

        boolean vendereResult;

        vendereResult = BaseController.vendereAnnuncio(annuncio_1.getID()).getResult();
        Annuncio annuncioVenduto = new Annuncio(annuncio_1.getID());
        BaseController.dettagliAnnuncio(annuncioVenduto, new ArrayList<>());
        printResult("Vendere annuncio #1", vendereResult, annuncioVenduto);


        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList(" Controllo seguiti annuncio dopo vendita", controlloSeguiti1Result, annunciSeguitiModificati);
        Thread.sleep(1100);

        annunciSeguitiModificati = new ArrayList<>();
        controlloSeguiti1Result = BaseController.controllareAnnunciSeguiti(utente.getID(), annunciSeguitiModificati);
        printResultList(" Secondo controllo seguiti annuncio dopo vendita", controlloSeguiti1Result, annunciSeguitiModificati);

        boolean vendere2Result;

        vendere2Result = BaseController.vendereAnnuncio(annuncio_1.getID()).getResult();
        Annuncio annuncioGiaVenduto = new Annuncio(annuncio_1.getID());
        BaseController.dettagliAnnuncio(annuncioGiaVenduto, new ArrayList<>());
        printResult("Vendere annuncio", vendere2Result, annuncioGiaVenduto);


        List<Annuncio> selectAnnuncioList = new ArrayList<>();
        printResultList("Cerca per inserzionista",
                BaseController.cercareAnnunciPerInserzionista(utente.getUsername(), true, selectAnnuncioList).getResult(),
                selectAnnuncioList);

        selectAnnuncioList.clear();
        printResultList("Cerca per categoria",
                BaseController.cercareAnnunciPerCategoria(categoria.getNome(), true, selectAnnuncioList).getResult(),
                selectAnnuncioList);

        selectAnnuncioList.clear();
        printResultList("Cerca per descrizione",
                BaseController.cercareAnnunciPerDescrizione("annuncio", true, selectAnnuncioList).getResult(),
                selectAnnuncioList);

        List<ReportEntry> reportEntryList = new ArrayList<>();
        printResultList("Report", GestoreController.generareReport(reportEntryList), reportEntryList);
    }

    private static void printResult(String operation, boolean result, Object o, String explain) {
        List<Object> iterable = null;
        if (o != null) {
            iterable = new ArrayList<>();
            iterable.add(o);
        }
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
            if (!iterable.iterator().hasNext()) {
                System.out.printf("%s is empty.\n", iterable.getClass().getName());
            } else {
                for (Object o : iterable)
                    if (o != null) System.out.println(o);
            }
        }
    }

}
