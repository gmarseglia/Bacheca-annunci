package Controller;

import DAO.DAO;
import DAO.BatchResult;
import Model.*;

import java.util.List;

public class RegistrationController {
    public static boolean registrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, List<Recapito> recapitoList) {
        boolean result;
        result = DAO.registrazioneUtente(utente, credenziali, anagrafica, recapitoList.get(0));
        return result;
    }

    public static BatchResult registrazioneRecapitiFacoltativi(List<Recapito> recapitoList) {
        BatchResult batchResult;
        batchResult = DAO.insertBatchRecapito(recapitoList.subList(1, recapitoList.size()));
        return batchResult;
    }

    public static boolean login(Credenziali credenziali) {
        return DAO.loginUtente(credenziali);
    }
}
