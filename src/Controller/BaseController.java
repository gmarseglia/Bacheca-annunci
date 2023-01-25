package Controller;

import DAO.DAO;
import DAO.DBResult;
import Model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class BaseController {
    protected static String getGenericSQLExceptionMessage(SQLException e) {
        if (Objects.equals(e.getSQLState(), "42000"))
            return String.format("L'utente non dispone dei permessi necessari, [%s]", e.getMessage());
        else return e.getSQLState() + ", " + e.getMessage();
    }

    public static DBResult inserireAnnuncio(Annuncio annuncio) {
        DBResult result = new DBResult(false);
        try {
            result.setResult(DAO.insertAnnuncio(ActiveUser.getRole(), annuncio));
        } catch (SQLException e) {
            switch (e.getSQLState()) {
                case "23000" -> result.setMessage(String.format("Categoria non esistente, [%s].", e.getMessage()));
                default -> result.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return result;
    }

    // C0000
    public static DBResult scrivereCommento(Long numero, String testo) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertCommento(ActiveUser.getRole(), ActiveUser.getUsername(), numero, testo));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45001" -> String.format("L'annuncio non è più disponibile [%s]", e.getMessage());
                case "23000" -> String.format("L'annuncio non esiste [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
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

    // M0000
    public static DBResult scrivereMessaggioPrivato(String usernameDestinatario, String testo) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertMessaggio(ActiveUser.getRole(), ActiveUser.getUsername(), usernameDestinatario, testo));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45006", "23000" ->
                        String.format("Il destinatario \"%s\" non esiste [%s]", usernameDestinatario, e.getMessage());
                case "45009" -> String.format("Il destinatario deve essere diverso dal mittente [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // M0101
    public static DBResult visualizzareUtentiConMessaggi(List<String> utenteIDList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectUtentiConMessaggi(ActiveUser.getRole(), ActiveUser.getUsername(), utenteIDList));
        } catch (SQLException e) {
            dbResult.setMessage(getGenericSQLExceptionMessage(e));
        }
        return dbResult;
    }

    // M0100
    public static DBResult visualizzareMessaggi(String utenteID2, List<MessaggioPrivato> messaggioPrivatoList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectMessaggiTraUtenti(ActiveUser.getRole(), ActiveUser.getUsername(), utenteID2, messaggioPrivatoList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45008" ->
                        String.format("Non sono presenti messaggi con l'utente \"%s\" [%s]", utenteID2, e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // A0500
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

    // A0600
    public static DBResult dettagliUtente(Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectDettagliUtente(ActiveUser.getRole(), utente, anagrafica, recapitoList));
        } catch (SQLException e) {
            switch (e.getSQLState()) {
                case "45006" -> dbResult.setMessage(String.format("Username non registrato [%s]", e.getMessage()));
                default -> dbResult.setMessage(getGenericSQLExceptionMessage(e));
            }
        }
        return dbResult;
    }

    // A0300
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

    // A0301
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

    // A0400
    public static DBResult controllareAnnunciSeguiti(List<Annuncio> annunciSegutiModificatiList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnunciSeguitiModificati(ActiveUser.getRole(), ActiveUser.getUsername(), annunciSegutiModificatiList, true, true));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
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

    // T0000
    public static DBResult visualizzareCategorie(List<Categoria> categoriaList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectCategoria(ActiveUser.getRole(), categoriaList));
        } catch (SQLException e) {
            dbResult.setMessage(getGenericSQLExceptionMessage(e));
        }
        return dbResult;
    }

    // A0204
    public static DBResult cercareAnnunci(Boolean onlyAvailable, List<Annuncio> foundAnnunciList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncio(ActiveUser.getRole(), onlyAvailable, foundAnnunciList));
        } catch (SQLException e) {
            dbResult.setMessage(getGenericSQLExceptionMessage(e));
        }
        return dbResult;
    }

    // G0000
    public static DBResult creareCategoria(String nomeCategoria, String nomePadre) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertCategoria(ActiveUser.getRole(), nomeCategoria, nomePadre));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "23000" ->
                        String.format("O esiste già una categoria con lo stesso nome OPPURE non esiste la categoria padre, [%s]", e.getMessage());
                case "45010" -> String.format("La categoria non può essere padre di se stessa, [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // R0001
    public static DBResult generareReport(List<ReportEntry> reportEntryList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectReport(ActiveUser.getRole(), reportEntryList));
        } catch (SQLException e) {
            dbResult.setMessage(getGenericSQLExceptionMessage(e));
        }
        return dbResult;
    }
}
