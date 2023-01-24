package Controller;

import DAO.DAO;
import DAO.BatchResult;
import DAO.DBResult;
import Model.*;

import java.sql.SQLException;
import java.util.List;

public class RegistrationController {
    public static BatchResult registrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, List<Recapito> recapitoList) {
        DBResult registrationResult = new DBResult(false);

        try {
            registrationResult.setResult(DAO.callRegistrazioneUtente(utente, credenziali, anagrafica, recapitoList.get(0)));
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                registrationResult.setMessage("Username, Codice Fiscale o Recapito Preferito già registrati, " + e.getMessage());
            } else {
                registrationResult.setMessage(e.getMessage() + ", " + e.getSQLState());
            }
        }

        BatchResult finalResult = new BatchResult(false);

        try {
            finalResult.setBatchResult(DAO.insertBatchRecapito(recapitoList.subList(1, recapitoList.size())));
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                finalResult.setBatchMessage("Recapito già registrato, " + e.getMessage());
            } else {
                finalResult.setBatchMessage(e.getMessage() + ", " + e.getSQLState());
            }
        }
        finalResult.setExtraResult(registrationResult.getResult());
        finalResult.setExtraMessage(registrationResult.getMessage());
        return finalResult;
    }

    public static DBResult login(Credenziali credenziali) {
        DBResult result = new DBResult(false);
        try {
            result.setResult(DAO.selectCredenziali(ActiveUser.getRole(), credenziali));
        } catch (SQLException e) {
            if (e.getSQLState().equals("S1000")) {
                result.setMessage("Credenziali di accesso non valide, " + e.getMessage());
            } else {
                result.setMessage(e.getMessage() + ", " + e.getSQLState());
            }
        }
        return result;
    }
}
