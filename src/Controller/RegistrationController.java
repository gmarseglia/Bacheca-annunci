package Controller;

import DAO.DAO;
import DAO.BatchResult;
import Model.*;

import java.sql.SQLException;
import java.util.List;

public class RegistrationController {
    public static BatchResult registrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, List<Recapito> recapitoList) {
        BatchResult result;
        boolean registrationResult = DAO.registrazioneUtente(utente, credenziali, anagrafica, recapitoList.get(0));
        result = DAO.insertBatchRecapito(recapitoList.subList(1, recapitoList.size()));
        result.setExtraResult(registrationResult);
        return result;
    }

    public static BatchResult registrazioneRecapitiFacoltativi(List<Recapito> recapitoList) {
        BatchResult batchResult;
        batchResult = DAO.insertBatchRecapito(recapitoList.subList(1, recapitoList.size()));
        return batchResult;
    }

    public static boolean login(Credenziali credenziali) throws SQLException {
        return DAO.selectCredenziali(ActiveUser.getRole(), credenziali);
    }
}
