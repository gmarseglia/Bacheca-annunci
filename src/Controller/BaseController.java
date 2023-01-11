package Controller;

import DAO.DAO;
import DAO.DBResult;
import Model.*;
import Model.Exception.AnnuncioVendutoException;
import Model.Exception.CustomSQLException;

import java.sql.SQLException;
import java.util.List;


public class BaseController {
    private static String getGenericSQLExceptionMessage(SQLException e) {
        return e.getSQLState() + ", " + e.getMessage();
    }

    public static DBResult inserireAnnuncio(Annuncio annuncio) {
        DBResult result = new DBResult(false);
        try {
            result.setResult(DAO.insertAnnuncio(ActiveUser.getRole(), annuncio));
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                result.setMessage(String.format("Categoria non esistente, [%s].", e.getMessage()));
            } else {
                result.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return result;
    }

    public static boolean scrivereCommento(Commento commento) throws AnnuncioVendutoException {
        return DAO.insertCommento(ActiveUser.getRole(), commento);
    }

    public static DBResult dettagliAnnuncio(Annuncio annuncio, List<Commento> commentoList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.getDettagliAnnuncio(ActiveUser.getRole(), annuncio, commentoList));
        } catch (SQLException e) {
            if (e.getSQLState().equals("45004")) {
                dbResult.setMessage(String.format("L'annuncio cercato non esiste, [%s]", e.getMessage()));
            } else {
                dbResult.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return dbResult;
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

    public static DBResult vendereAnnuncio(Long annuncioID) {
        DBResult result = new DBResult(false);
        try {
            result.setResult(DAO.updateAnnuncioVendere(ActiveUser.getRole(), annuncioID, ActiveUser.getUsername()));
        } catch (SQLException e) {
            if (e.getSQLState().equals("45001")) {
                result.setMessage(String.format("Annuncio già venduto, [%s].", e.getMessage()));
            } else if (e.getSQLState().equals("45002")) {
                result.setMessage(String.format("Utente attivo non è l'inserzionista dell'annuncio, [%s].", e.getMessage()));
            } else {
                result.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return result;
    }

    public static boolean dettagliUtente(Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) {
        return DAO.selectDettagliUtente(ActiveUser.getRole(), utente, anagrafica, recapitoList);
    }

    public static DBResult seguireAnnuncio(Long annuncioID) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertSegue(ActiveUser.getRole(), new Segue(ActiveUser.getUsername(), annuncioID)));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "23000" -> String.format("L'annuncio è già presente fra i \"seguiti\" [%s]", e.getMessage());
                case "45001" -> String.format("L'annuncio non è più disponibile [%s]", e.getMessage());
                case "45004" -> String.format("L'annuncio non è esistente [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static DBResult stopSeguireAnnuncio(Long annuncioID) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.deleteSegue(ActiveUser.getRole(), ActiveUser.getUsername(), annuncioID));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45005" -> String.format("L'annuncio non è presente fra i seguiti [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static boolean controllareAnnunciSeguiti(String utenteID, List<Annuncio> annunciSegutiModificatiList) {
        return DAO.selectAnnunciSeguitiModificati(ActiveUser.getRole(), utenteID, annunciSegutiModificatiList, true, true);
    }

    public static DBResult cercareAnnunciPerInserzionista(String inserzionistaID, Boolean onlyAvailable, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncioByInserzionista(ActiveUser.getRole(), inserzionistaID, onlyAvailable != null && onlyAvailable, annuncioList));
        } catch (SQLException e) {
            if (e.getSQLState().equals("45002")) {
                dbResult.setMessage(String.format("Utente non esistente [%s]", e.getMessage()));
            } else {
                dbResult.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return dbResult;
    }

    public static DBResult cercareAnnunciPerCategoria(String categoriaID, Boolean onlyAvailable, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);

        try {
            dbResult.setResult(DAO.selectAnnuncioByCategoria(ActiveUser.getRole(), categoriaID, onlyAvailable, annuncioList));
        } catch (SQLException e) {
            if (e.getSQLState().equals("45003")) {
                dbResult.setMessage(String.format("Categoria non esistente [%s]", e.getMessage()));
            } else {
                dbResult.setMessage(getGenericSQLExceptionMessage(e));
            }
        }

        return dbResult;
    }

    public static DBResult cercareAnnunciPerDescrizione(String descrizione, Boolean onlyAvailable, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncioByDescrizione(ActiveUser.getRole(), descrizione, onlyAvailable, annuncioList));
        } catch (SQLException e) {
            dbResult.setMessage(getGenericSQLExceptionMessage(e));
        }
        return dbResult;
    }
}
