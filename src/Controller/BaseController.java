package Controller;

import DAO.DAO;
import DAO.DBResult;
import Model.*;
import Model.Exception.AnnuncioVendutoException;

import java.sql.SQLException;
import java.util.List;


public class BaseController {
    public static DBResult inserireAnnuncio(Annuncio annuncio) {
        DBResult result = new DBResult(false);
        try {
            result.setResult(DAO.insertAnnuncio(ActiveUser.getRole(), annuncio));
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                result.setMessage(String.format("Categoria non esistente, [%s].", e.getMessage()));
            } else {
                result.setMessage(e.getSQLState() + ", " + e.getMessage());
            }
        }
        return result;
    }

    public static boolean scrivereCommento(Commento commento) throws AnnuncioVendutoException {
        return DAO.insertCommento(ActiveUser.getRole(), commento);
    }

    public static boolean dettagliAnnuncio(Annuncio annuncio, List<Commento> commentoList) {
        return DAO.getDettagliAnnuncio(ActiveUser.getRole(), annuncio, commentoList);
    }

    public static boolean scrivereMessaggioPrivato(MessaggioPrivato messaggioPrivato) {
        return DAO.insertMessaggio(ActiveUser.getRole(), messaggioPrivato);
    }

    public static boolean visualizzareChat(String utenteID, List<String> utenteIDList) {
        return DAO.selectUtentiConMessaggi(ActiveUser.getRole(), utenteID, utenteIDList);
    }

    public static boolean visualizzareMessaggi(String utenteID1, String utenteID2, List<MessaggioPrivato> messaggioPrivatoList) {
        return DAO.selectMessaggiTraUtenti(ActiveUser.getRole(), utenteID1, utenteID2, messaggioPrivatoList);
    }

    public static boolean vendereAnnuncio(Long annuncioID) throws AnnuncioVendutoException {
        return DAO.updateAnnuncioVendere(ActiveUser.getRole(), annuncioID);
    }

    public static boolean dettagliUtente(Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) {
        return DAO.selectDettagliUtente(ActiveUser.getRole(), utente, anagrafica, recapitoList);
    }

    public static boolean seguireAnnuncio(Segue segue) throws AnnuncioVendutoException {
        return DAO.insertSegue(ActiveUser.getRole(), segue);
    }

    public static boolean stopSeguireAnnuncio(Segue segue) {
        return DAO.deleteSegue(ActiveUser.getRole(), segue);
    }

    public static boolean controllareAnnunciSeguiti(String utenteID, List<Annuncio> annunciSegutiModificatiList) {
        return DAO.selectAnnunciSeguitiModificati(ActiveUser.getRole(), utenteID, annunciSegutiModificatiList, true, true);
    }

    public static boolean cercareAnnunciPerInserzionista(String inserzionistaID, List<Annuncio> annuncioList) {
        return DAO.selectAnnuncioByInserzionista(ActiveUser.getRole(), inserzionistaID, true, annuncioList);
    }

    public static boolean cercareAnnunciPerCategoria(String categoriaID, List<Annuncio> annuncioList) {
        return DAO.selectAvailableAnnuncioByCategoria(ActiveUser.getRole(), categoriaID, true, annuncioList);
    }

    public static boolean cercareAnnunciPerDescrizione(String descrizione, List<Annuncio> annuncioList) {
        return DAO.selectAnnuncioByDescrizione(ActiveUser.getRole(), descrizione, true, annuncioList);
    }
}
