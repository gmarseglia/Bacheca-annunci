package DAO;

import Model.*;
import Model.Exception.CustomSQLException;

import java.sql.*;
import java.util.List;

public class DAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bacheca_annunci";

    private static Role LAST_ROLE = null;

    private static final String BASE_USER = "base";
    private static final String BASE_PASS = "base";

    private static final String GESTORE_USER = "gestore";
    private static final String GESTORE_PASS = "gestore";

    private static final String REGISTRATORE_USER = "registratore";
    private static final String REGISTRATORE_PASS = "registratore";

    private static Connection conn;

    // CONNESSIONE
    private static void openRoleConnection(Role role) throws SQLException {
        if (conn == null || LAST_ROLE != role) {

            if (conn != null) conn.close();

            String targetUser = null;
            String targetPass = null;

            switch (role) {
                case BASE -> {
                    targetUser = BASE_USER;
                    targetPass = BASE_PASS;
                }
                case GESTORE -> {
                    targetUser = GESTORE_USER;
                    targetPass = GESTORE_PASS;
                }
                case REGISTRATORE -> {
                    targetUser = REGISTRATORE_USER;
                    targetPass = REGISTRATORE_PASS;
                }
            }

            conn = DriverManager.getConnection(DB_URL, targetUser, targetPass);
            LAST_ROLE = role;
        }
    }

    public static void closeConnection() throws SQLException {
        if (conn != null)
            conn.close();
    }

    // LOGIN E REGISTRAZIONE
    //      U0100
    public static Boolean selectCredenziali(Role role, Credenziali credenziali) throws SQLException {
        openRoleConnection(role);

        String call = "{call `login`(?, ?)};";

        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, credenziali.getUsername());
        cs.setString(2, credenziali.getPassword());
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        rs.first();

        Role selectedRole = switch (rs.getString(1)) {
            case "base" -> Role.BASE;
            case "gestore" -> Role.GESTORE;
            default -> {
                CustomSQLException e = new CustomSQLException();
                e.setSQLState("45007");
                e.setMessage("Ruolo non valido.");
                throw e;
            }
        };

        credenziali.setRole(selectedRole);

        return true;
    }

    //      U0000
    public static boolean callRegistrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, Recapito recapitoPreferito) throws SQLException {
        // #1: connect
        openRoleConnection(ActiveUser.getRole());

        // #2: create statement
        String callQuery = "{call `registrazione_utente` (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);

        // Prepare call
        cs.setString(1, utente.getUsername());
        cs.setString(2, credenziali.getPassword());
        String userRole = switch (credenziali.getRole()) {
            case BASE -> "base";
            case GESTORE -> "gestore";
            default -> throw new RuntimeException("Invalid role");
        };
        cs.setString(3, userRole);
        cs.setString(4, anagrafica.getCodiceFiscale());
        cs.setString(5, anagrafica.getNome());
        cs.setString(6, anagrafica.getCognome());
        String userSesso = switch (anagrafica.getSesso()) {
            case UOMO -> "uomo";
            case DONNA -> "donna";
        };
        cs.setString(7, userSesso);
        cs.setDate(8, Date.valueOf(anagrafica.getDataNascita()));
        cs.setString(9, anagrafica.getComuneNascita());
        cs.setString(10, anagrafica.getIndirizzoResidenza());
        cs.setString(11, anagrafica.getIndirizzoFatturazione());

        cs.setString(12, recapitoPreferito.getValore());
        String tipoRecapito = switch (recapitoPreferito.getTipo()) {
            case EMAIL -> "email";
            case TELEFONO -> "telefono";
            case CELLULARE -> "cellulare";
        };
        cs.setString(13, tipoRecapito);
        cs.closeOnCompletion();

        cs.execute();

        return true;
    }

    //      A0600
    public static boolean selectDettagliUtente(Role role, Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `dettagli_utente`(?)}";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, utente.getID());

        ResultSet rs = cs.executeQuery();

        if (!rs.first()) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45006");
            e.setMessage("Utente non esistente");
            throw e;
        }

        utente.setAnnunci_inseriti(rs.getInt(1));
        utente.setAnnunci_venduti(rs.getInt(2));

        anagrafica.setCodiceFiscale(rs.getString(3));
        anagrafica.setNome(rs.getString(4));
        anagrafica.setCognome(rs.getString(5));
        anagrafica.setSesso(switch (rs.getString(6)) {
            case "uomo", "Uomo" -> Sesso.UOMO;
            case "donna", "Donna" -> Sesso.DONNA;
            default -> null;
        });
        anagrafica.setDataNascita(rs.getTimestamp(7).toLocalDateTime().toLocalDate());
        anagrafica.setComuneNascita(rs.getString(8));
        anagrafica.setIndirizzoResidenza(rs.getString(9));
        anagrafica.setIndirizzoFatturazione(rs.getString(10));

        Recapito recapitoPreferito = new Recapito();
        recapitoPreferito.setValore(rs.getString(11));
        recapitoPreferito.setTipo(switch (rs.getString(12)) {
            case "telefono" -> TipoRecapito.TELEFONO;
            case "cellulare" -> TipoRecapito.CELLULARE;
            case "email" -> TipoRecapito.EMAIL;
            default -> null;
        });
        recapitoList.add(recapitoPreferito);

        if (cs.getMoreResults()) {
            rs = cs.getResultSet();

            if (rs.first()) {
                do {
                    Recapito recapito = new Recapito();
                    recapito.setValore(rs.getString(1));
                    recapito.setTipo(switch (rs.getString(2)) {
                        case "telefono" -> TipoRecapito.TELEFONO;
                        case "cellulare" -> TipoRecapito.CELLULARE;
                        case "email" -> TipoRecapito.EMAIL;
                        default -> null;
                    });
                    recapitoList.add(recapito);
                } while (rs.next());
            }
        }

        cs.close();

        return true;
    }

    // RECAPITO
    //      U0001
    public static int[] insertBatchRecapito(List<Recapito> listOfRecapito) throws SQLException {
        int[] batchResult;
        try {
            openRoleConnection(ActiveUser.getRole());

            conn.setAutoCommit(false);

            String call = "{CALL `inserire_recapito`(?, ?, ?)};";
            CallableStatement cs = conn.prepareCall(call);

            for (Recapito recapito : listOfRecapito) {
                cs.setString(1, recapito.getValore());
                cs.setString(2, recapito.getAnagraficaID());
                String tipo = switch (recapito.getTipo()) {
                    case CELLULARE -> "cellulare";
                    case TELEFONO -> "telefono";
                    case EMAIL -> "email";
                };
                cs.setString(3, tipo);
                cs.addBatch();
            }
            cs.closeOnCompletion();

            batchResult = (cs.executeBatch());
            conn.commit();

        } finally {
            conn.setAutoCommit(true);
        }

        return batchResult;
    }

    // MESSAGGI PRIVATI
    //      M0000
    public static boolean insertMessaggio(Role role, String usernameMittente, String usernameDestinatario, String testo) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `insert_messaggio` (?, ?, ?)};";
        CallableStatement cs = conn.prepareCall(call);
        cs.setString(1, usernameMittente);
        cs.setString(2, usernameDestinatario);
        cs.setString(3, testo);
        cs.closeOnCompletion();

        if (cs.executeUpdate() == 0) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45006");
            e.setMessage("Utente non esistente");
        }

        return true;
    }

    //      M0101
    public static boolean selectUtentiConMessaggi(Role role, String targetUtente, List<String> utenteIDList) throws SQLException, RuntimeException {
        openRoleConnection(role);

        String call = "{CALL `select_utenti_con_messaggi` (?)};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, targetUtente);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                utenteIDList.add(rs.getString(1));
            } while (rs.next());
        }

        return true;
    }

    //      M0100
    public static boolean selectMessaggiTraUtenti(Role role, String utente1ID, String utente2ID, List<MessaggioPrivato> messaggioPrivatoList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_messaggi_con_utente` (?, ?)};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, utente1ID);
        cs.setString(2, utente2ID);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (!rs.first()) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45008");
            e.setMessage("Messaggi con l'utente non esistenti");
            throw e;
        }

        do {
            messaggioPrivatoList.add(new MessaggioPrivato(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getTimestamp(3).toLocalDateTime(),
                    rs.getString(4)
            ));
        } while (rs.next());

        return true;
    }

    // CATEGORIA
    //      T0000
    public static boolean selectCategoria(Role role, List<Categoria> categoriaList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_categoria`};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                categoriaList.add(new Categoria(rs.getString(1), rs.getString(2)));
            } while (rs.next());
        }

        return true;
    }

    //      G0000
    public static boolean insertCategoria(Role role, String nomeCategoria, String nomePadre) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `insert_categoria` (?, ?)};";
        CallableStatement cs = conn.prepareCall(call);

        cs.setString(1, nomeCategoria);
        cs.setString(2, nomePadre);
        cs.closeOnCompletion();

        cs.executeUpdate();

        return true;
    }

    //      A0000
    public static boolean insertAnnuncio(Role role, Annuncio annuncio) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{CALL `inserire_annuncio`(?, ?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);

        cs.setString(1, ActiveUser.getUsername());
        cs.setString(2, annuncio.getDescrizione());
        cs.setString(3, annuncio.getCategoria());
        cs.registerOutParameter(4, Types.INTEGER);
        cs.closeOnCompletion();

        cs.execute();

        annuncio.setInserzionista(ActiveUser.getUsername());
        annuncio.setNumero(cs.getLong(4));

        return true;
    }

    //      A0100
    public static boolean getDettagliAnnuncio(Role role, String utenteRichiesta, Annuncio annuncio, List<Commento> commentoList) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{CALL `dettagli_annuncio`(?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        cs.setString(1, utenteRichiesta);
        cs.setLong(2, annuncio.getID());
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            annuncio.setNumero(rs.getLong(1));
            annuncio.setInserzionista(rs.getString(2));
            annuncio.setDescrizione(rs.getString(3));
            annuncio.setCategoria(rs.getString(4));
            annuncio.setInserito(rs.getTimestamp(5).toLocalDateTime());
            annuncio.setModificato((rs.getTimestamp(6) == null) ? null : rs.getTimestamp(6).toLocalDateTime());
            annuncio.setVenduto((rs.getTimestamp(7) == null) ? null : rs.getTimestamp(7).toLocalDateTime());

            do {
                if (rs.getString(8) != null) {
                    Commento commento = new Commento(
                            rs.getString(8),
                            annuncio.getID(),
                            rs.getTimestamp(9).toLocalDateTime(),
                            rs.getString(10)
                    );
                    commentoList.add(commento);
                }
            } while (rs.next());
        } else {
            CustomSQLException e = new CustomSQLException();
            e.setMessage("Annuncio non disponibile o non esistente");
            e.setSQLState("45011");
            throw e;
        }

        return true;
    }


    //      A0200
    public static boolean selectAnnuncioByCategoria(Role role, String categoriaID, Boolean onlyAvailable, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_annunci_categorie_figlie` (?, ?)}";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, categoriaID);
        cs.setBoolean(2, onlyAvailable);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                annuncioList.add(BuilderAnnuncio.newFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

    //      A0202
    public static boolean selectAnnuncioByInserzionista(Role role, String inserzionistaID, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_annunci_by_inserzionista` (?)};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, inserzionistaID);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                annuncioList.add(BuilderAnnuncio.newAvailableFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

    //      A0203
    public static boolean selectAnnuncioByDescrizione(Role role, String descrizione, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_annunci_by_descrizione` (?)}";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, descrizione);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                annuncioList.add(BuilderAnnuncio.newAvailableFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

    //      A0204
    public static boolean selectAnnuncio(Role role, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `select_annunci_without_clauses`()};";
        PreparedStatement cs = conn.prepareStatement(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                annuncioList.add(BuilderAnnuncio.newAvailableFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

    // SEGUE
    //      A0300
    public static boolean insertSegue(Role role, Segue segue) throws SQLException {
        openRoleConnection(role);

        String call = "{call `seguire_annuncio`(?, ?)};";
        CallableStatement cs = conn.prepareCall(call);
        cs.setString(1, segue.getUtente());
        cs.setLong(2, segue.getAnnuncio());
        cs.closeOnCompletion();

        cs.execute();

        return true;
    }

    //      A0301
    public static boolean deleteSegue(Role role, String utenteID, Long annuncioID) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `delete_segue` (?, ?)};";
        CallableStatement cs = conn.prepareCall(call);
        cs.setString(1, utenteID);
        cs.setLong(2, annuncioID);
        cs.closeOnCompletion();

        if (cs.executeUpdate() == 0) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45005");
            e.setMessage("Annuncio non presente fra i seguiti");
            throw e;
        }

        return true;
    }

    // A0400
    public static boolean selectAnnunciSeguitiModificati(Role role, String utenteID, List<Annuncio> annunciSeguitiModificatiList, boolean updateLastCheck, boolean deleteSold) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `controllare_annunci_seguiti` (?, ?, ?)};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, utenteID);
        cs.setBoolean(2, updateLastCheck);
        cs.setBoolean(3, deleteSold);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                annunciSeguitiModificatiList.add(BuilderAnnuncio.newFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

    //      A0500
    public static boolean updateAnnuncioVendere(Role role, long annuncioID, String utenteID) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `vendere_annuncio`(?, ?)};";
        CallableStatement cs = conn.prepareCall(call);
        cs.setLong(1, annuncioID);
        cs.setString(2, utenteID);
        cs.closeOnCompletion();

        cs.executeUpdate();

        return true;
    }

    // COMMENTO
    //      C0000
    public static boolean insertCommento(Role role, String utente, Long numero, String testo) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{CALL `scrivere_commento`(?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);
        cs.setString(1, utente);
        cs.setLong(2, numero);
        cs.setString(3, testo);
        cs.closeOnCompletion();

        cs.executeUpdate();

        return true;
    }

    // REPORT
    //      R0001
    public static boolean selectReport(Role role, List<ReportEntry> reportEntryList) throws SQLException {
        openRoleConnection(role);

        String call = "{CALL `generate_report`};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.closeOnCompletion();

        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            do {
                reportEntryList.add(new ReportEntry(rs.getString(1), rs.getFloat(2), rs.getInt(3)));
            } while (rs.next());
        }

        return true;
    }
}
