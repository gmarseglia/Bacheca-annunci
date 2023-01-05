package View;

import Model.*;
import Utility.RndData;
import Utility.TimerController;
import DAO.DAO;
import DAO.BatchResult;

import java.util.ArrayList;
import java.util.List;

public class PopulateDB {

    private static final int nOfUtenti = 1000;
    private static final int nOfChatPerUtente = 4;
    private static final int nOfMessagesPerChat = 4;
    private static final int nOfRecapitoPerUtente = 2;
    private static final int nOfCategoriaPadre = 10;
    private static final int nOfCategoriaLayers = 3;
    private static final int nOfCategoriaPerLayers = 2;
    private static final double percentageOfUtenteWithAnnuncio = .5;
    private static final int nOfAnnuncioPerUtente = 12;
    private static final int N_SEGUE_PER_USER = 6;


    private static BatchResult insertedResult;

    public static void main(String[] args) {

        List<Utente> listOfUtente = new ArrayList<>();
        List<Credenziali> listOfCredenziali = new ArrayList<>();
        List<Anagrafica> listOfAnagrafica = new ArrayList<>();
        List<MessaggioPrivato> listOfMessaggioPrivato = new ArrayList<>();
        List<Recapito> listOfRecapito = new ArrayList<>();
        List<Recapito> listOfRecapitoPreferito = new ArrayList<>();
        List<Categoria> listOfCategoria = new ArrayList<>();
        List<Annuncio> listOfAnnuncio = new ArrayList<>();
        List<Segue> listOfSegue = new ArrayList<>();

        /*
            Delete all bound to `utente`
         */
        genericProcedure("\nDeleting all in `utente`... ", DAO::deleteAllUtente);
        genericProcedure("Resetting auto_increment in `annuncio... ", DAO::resetAutoincrement);

        System.out.println("\nBegin generate phase.\n");

        /*
            Generate Utente, Credenziali, Anagrafica, Recapito
         */
        generateProcedure(nOfUtenti, "users with data", () -> {
            for (int j = 0; j < nOfUtenti; j++) {
                Utente lastUtente = new Utente(RndData.randomString(10));

                Credenziali lastCredential = new Credenziali(lastUtente.getUsername(), RndData.randomString(10), (j % 20 == 0) ? Role.GESTORE : Role.BASE);

                Anagrafica lastAnagrafica = new Anagrafica(RndData.getRandomCF(), RndData.randomString(10), RndData.randomString(10), (j % 2 == 0) ? Sesso.DONNA : Sesso.UOMO, RndData.randomDate(), RndData.randomString(15), RndData.randomString(15), lastUtente.getUsername());
                if (j % 20 == 0) lastAnagrafica.setIndirizzoFatturazione(RndData.randomString(15));

                for (int recapitoIndex = 0; recapitoIndex < nOfRecapitoPerUtente; recapitoIndex++) {
                    Recapito lastRecapito = new Recapito(RndData.randomString(10), TipoRecapito.values()[RndData.getRandomInt(0, TipoRecapito.values().length - 1)], lastAnagrafica);
                    listOfRecapito.add(lastRecapito);
                    if (recapitoIndex == 0) listOfRecapitoPreferito.add(lastRecapito);
                }

                lastUtente.setCredenziali(lastCredential);
                lastUtente.setAnagrafica(lastAnagrafica);

                listOfUtente.add(lastUtente);
                listOfCredenziali.add(lastCredential);
                listOfAnagrafica.add(lastAnagrafica);
            }
        });

        /*
            Generate MessaggioPrivato
         */
        generateProcedure(nOfUtenti * nOfChatPerUtente * nOfMessagesPerChat, "MessaggioPrivato", () -> {
            for (int userIndex = 0; userIndex < nOfUtenti; userIndex++) {
                for (int chatIndex = 0; chatIndex < nOfChatPerUtente / 2; chatIndex++) {
                    for (int messagesIndex = 0; messagesIndex < nOfMessagesPerChat; messagesIndex++) {
                        MessaggioPrivato lastMessaggioPrivato1 = new MessaggioPrivato(listOfUtente.get(userIndex), listOfUtente.get((userIndex + 1 + chatIndex) % nOfUtenti), RndData.randomDateTime(), RndData.randomStringWithBlanks(100));
                        MessaggioPrivato lastMessaggioPrivato2 = new MessaggioPrivato(listOfUtente.get((userIndex + 1 + chatIndex) % nOfUtenti), listOfUtente.get(userIndex), lastMessaggioPrivato1.getInviato().plusMinutes(5), RndData.randomStringWithBlanks(100));
                        listOfMessaggioPrivato.add(lastMessaggioPrivato1);
                        listOfMessaggioPrivato.add(lastMessaggioPrivato2);
                    }
                }
            }
        });

        /*
            Generate Categoria
         */
        int nOfCategorie = nOfCategoriaPadre;
        int nOfLastLayer = nOfCategoriaPadre;
        for (int layerIndex = 0; layerIndex < nOfCategoriaLayers - 1; layerIndex++) {
            nOfLastLayer = nOfLastLayer * nOfCategoriaPerLayers;
            nOfCategorie += nOfLastLayer;
        }
        generateProcedure(nOfCategorie, "categoria", () -> generateListOfCategoria(listOfCategoria));

        /*
            Generate Annuncio
         */
        generateProcedure((int) (nOfUtenti * percentageOfUtenteWithAnnuncio * nOfAnnuncioPerUtente), "annuncio", () -> generateListOfAnnuncio(listOfAnnuncio, listOfUtente, listOfCategoria));

        /*
            Generate Segue
         */
        generateProcedure(nOfUtenti * N_SEGUE_PER_USER, "Segue", () -> generateListOfSegue(listOfSegue, listOfUtente, listOfAnnuncio));

        System.out.println("\nBegin insert phase.\n");

        insertBatchProcedure("utente", listOfUtente, () -> insertedResult = DAO.insertBatchUtenteOnlyUsername(Role.ROOT, listOfUtente));

        insertBatchProcedure("credenziali", listOfCredenziali, () -> insertedResult = DAO.insertBatchCredenziali(Role.ROOT, listOfCredenziali));

        insertBatchProcedure("anagrafica", listOfAnagrafica, () -> insertedResult = DAO.insertBatchAnagrafica(Role.ROOT, listOfAnagrafica));

        insertBatchProcedure("recapito", listOfRecapito, () -> insertedResult = DAO.insertBatchRecapito(Role.ROOT, listOfRecapito));

        insertBatchProcedure("recapito_preferito", listOfRecapitoPreferito, () -> insertedResult = DAO.insertBatchRecapitoPreferito(Role.ROOT, listOfRecapitoPreferito));

        insertBatchProcedure("messaggio_privato", listOfMessaggioPrivato, () -> insertedResult = DAO.insertBatchMessaggioPrivato(Role.ROOT, listOfMessaggioPrivato));

        insertBatchProcedure("categoria", listOfCategoria, () -> insertedResult = DAO.insertBatchCategoria(Role.ROOT, listOfCategoria));

        insertBatchProcedure("annuncio", listOfAnnuncio, () -> insertedResult = DAO.insertBatchAnnuncio(Role.ROOT, listOfAnnuncio));

        insertBatchProcedure("segue", listOfSegue, () -> insertedResult = DAO.insertBatchSegue(Role.ROOT, listOfSegue));

        DAO.closeConnection();
    }

    private static void genericProcedure(String title, Runnable block) {
        TimerController timer = new TimerController();
        System.out.print(title);
        timer.start();
        block.run();
        timer.stop();
        System.out.printf("took %d seconds.\n", timer.getDurationInSec());
    }

    private static void insertBatchProcedure(String tableName, @SuppressWarnings("rawtypes") List tuples, Runnable block) {
        TimerController timer = new TimerController();
        System.out.printf("Inserting %d tuples into `%s`... ", tuples.size(), tableName);
        timer.start();
        block.run();
        timer.stop();
        System.out.printf("took %d seconds and %d/%d tuples inserted.\n", timer.getDurationInSec(), insertedResult.getBatchResult(), tuples.size());
    }

    private static void generateProcedure(int nCount, String name, Runnable block) {
        TimerController timer = new TimerController();
        System.out.printf("Generating %d %s... ", nCount, name);
        timer.start();
        block.run();
        timer.stop();
        System.out.printf("took %d seconds.\n", timer.getDurationInSec());
    }


    private static void generateListOfCategoria(List<Categoria> listOfCategoria) {
        for (int padreIndex = 0; padreIndex < nOfCategoriaPadre; padreIndex++)
            generateSonCategories(listOfCategoria, null, nOfCategoriaLayers, nOfCategoriaPerLayers);
    }

    private static void generateSonCategories(List<Categoria> listOfCategories, Categoria padre, int nOfLayers, int nOfCategories) {
        Categoria newCategoria = new Categoria(RndData.randomString(10), padre);
        listOfCategories.add(newCategoria);
        if (nOfLayers > 1) {
            for (int categoriaIndex = 0; categoriaIndex < nOfCategories; categoriaIndex++) {
                generateSonCategories(listOfCategories, newCategoria, nOfLayers - 1, nOfCategories);
            }
        }
    }

    private static void generateListOfAnnuncio(List<Annuncio> listOfAnnuncio, List<Utente> listOfUtente, List<Categoria> listOfCategoria) {
        for (int utenteIndex = 0; utenteIndex < nOfUtenti * percentageOfUtenteWithAnnuncio; utenteIndex++) {
            for (int annuncioIndex = 0; annuncioIndex < nOfAnnuncioPerUtente; annuncioIndex++) {
                Annuncio lastAnnuncio = new Annuncio(listOfUtente.get(utenteIndex).getID(), RndData.randomStringWithBlanks(50), RndData.getRandomInt(100, 100000), listOfCategoria.get(RndData.getRandomInt(0, listOfCategoria.size() - 1)).getID(), RndData.randomDateTime());
                listOfAnnuncio.add(lastAnnuncio);
            }
        }
    }

    private static void generateListOfSegue(List<Segue> listOfSegue, List<Utente> listOfUtente, List<Annuncio> listOfAnnuncio) {
        for (Utente utente : listOfUtente) {
            for (int segueIndex = 0; segueIndex < N_SEGUE_PER_USER; segueIndex++) {
                Segue lastSegue = new Segue(utente.getID(), RndData.getRandomInt(0, listOfAnnuncio.size() - 1));
                listOfSegue.add(lastSegue);
            }
        }
    }
}