package Controller;

import DAO.DAO;
import DAO.DBResult;
import DAO.DBResultBatch;
import Model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class ViewController {
    protected static String getGenericSQLExceptionMessage(SQLException e) {
        return getExceptionMessage(null, e);
    }

    protected static String getExceptionMessage(String customMessage, SQLException e) {
        String message;
        if (customMessage != null) {
            message = customMessage;
        } else if (Objects.equals(e.getSQLState(), "42000")) {
            message = "L'utente non dispone dei permessi necessari";
        } else {
            message = e.getMessage();
        }

        return String.format("%s, [%s]", message, e.getSQLState());
    }


    public static DBResultBatch registrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, List<Recapito> recapitoList) {
        DBResult registrationDbResult = new DBResult(false);
        try {
            registrationDbResult.setResult(DAO.callRegistrazioneUtente(utente, credenziali, anagrafica, recapitoList.get(0)));
        } catch (SQLException e) {
            registrationDbResult.setMessage(switch (e.getSQLState()) {
                case "23000" -> getExceptionMessage("Username, codice fiscale o recapito preferito già registrati", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }

        DBResultBatch finalResult = new DBResultBatch(false);
        try {
            finalResult.setBatchResult(DAO.insertBatchRecapito(recapitoList.subList(1, recapitoList.size())));
        } catch (SQLException e) {
            finalResult.setBatchMessage(switch (e.getSQLState()) {
                case "23000" -> getExceptionMessage("Recapito già registrato", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        finalResult.setExtraResult(registrationDbResult.getResult());
        finalResult.setExtraMessage(registrationDbResult.getMessage());
        return finalResult;
    }

    public static DBResult login(Credenziali credenziali) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectCredenziali(ActiveUser.getRole(), credenziali));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "S1000" -> getExceptionMessage("Credenziali di accesso non valide", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static DBResult inserireAnnuncio(Annuncio annuncio) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertAnnuncio(ActiveUser.getRole(), annuncio));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "23000" -> getExceptionMessage("Categoria o inserzionista non esistente", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // C0000
    public static DBResult scrivereCommento(Long numero, String testo) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertCommento(ActiveUser.getRole(), ActiveUser.getUsername(), numero, testo));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "23000" -> getExceptionMessage("L'annuncio non esiste", e);
                case "45001" -> getExceptionMessage("L'annuncio non è più disponibile", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static DBResult dettagliAnnuncio(Annuncio annuncio, List<Commento> commentoList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.getDettagliAnnuncio(ActiveUser.getRole(), ActiveUser.getUsername(), annuncio, commentoList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45004" -> getExceptionMessage("L'annuncio cercato non esiste", e);
                case "45011" -> getExceptionMessage("L'annuncio cercato non è disponibile o non è esistente", e);
                default -> getGenericSQLExceptionMessage(e);
            });
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
                        getExceptionMessage(String.format("Il destinatario \"%s\" non esiste", usernameDestinatario), e);
                case "45009" -> getExceptionMessage("Il destinatario deve essere diverso dal mittente", e);
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
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
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
                        getExceptionMessage(String.format("Non sono presenti messaggi con l'utente \"%s\"", utenteID2), e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // A0500
    public static DBResult vendereAnnuncio(Long annuncioID) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.updateAnnuncioVendere(ActiveUser.getRole(), annuncioID, ActiveUser.getUsername()));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45001" -> getExceptionMessage("\"Annuncio già venduto", e);
                case "45002" -> getExceptionMessage("Utente attivo non è l'inserzionista dell'annuncio", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // A0600
    public static DBResult dettagliUtente(Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectDettagliUtente(ActiveUser.getRole(), utente, anagrafica, recapitoList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45006" -> getExceptionMessage("Utente non trovato", e);
                default -> getGenericSQLExceptionMessage(e);
            });
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
                case "23000" -> getExceptionMessage("L'annuncio è già presente fra i \"seguiti\"", e);
                case "45001" -> getExceptionMessage("L'annuncio non è più disponibile", e);
                case "45004" -> getExceptionMessage("L'annuncio non è esistente", e);
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
                case "45005" -> getExceptionMessage("L'annuncio non è presente fra i seguiti", e);
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

    public static DBResult cercareAnnunciPerInserzionista(String inserzionistaID, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncioByInserzionista(ActiveUser.getRole(), inserzionistaID, annuncioList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45002" -> getExceptionMessage("Utente non esistente", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static DBResult cercareAnnunciPerCategoria(String categoriaID, Boolean
            onlyAvailable, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);

        try {
            dbResult.setResult(DAO.selectAnnuncioByCategoria(ActiveUser.getRole(), categoriaID, onlyAvailable, annuncioList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "45003" -> getExceptionMessage("Categoria non esistente", e);
                default -> getGenericSQLExceptionMessage(e);
            });
        }

        return dbResult;
    }

    public static DBResult cercareAnnunciPerDescrizione(String descrizione, List<Annuncio> annuncioList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncioByDescrizione(ActiveUser.getRole(), descrizione, annuncioList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // T0000
    public static DBResult visualizzareCategorie(List<Categoria> categoriaList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectCategoria(ActiveUser.getRole(), categoriaList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    // A0204
    public static DBResult cercareAnnunci(Boolean onlyAvailable, List<Annuncio> foundAnnunciList) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.selectAnnuncio(ActiveUser.getRole(), onlyAvailable, foundAnnunciList));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
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
                        getExceptionMessage("O esiste già una categoria con lo stesso nome OPPURE non esiste la categoria padre", e);
                case "45010" -> getExceptionMessage("La categoria non può essere padre di se stessa", e);
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
            dbResult.setMessage(switch (e.getSQLState()) {
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }
}
