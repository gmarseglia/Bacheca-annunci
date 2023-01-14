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
    public static Boolean selectCredenziali(Role role, Credenziali credenziali) throws SQLException {
        openRoleConnection(role);

        String query = "SELECT `ruolo` FROM `credenziali` " +
                "WHERE `username`=? AND `password`=?;";

        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, credenziali.getUsername());
        ps.setString(2, credenziali.getPassword());
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

        rs.first();

        Role selectedRole = switch (rs.getString(1)) {
            case "base" -> Role.BASE;
            case "gestore" -> Role.GESTORE;
            default -> throw new RuntimeException("Invalid role");
        };
        credenziali.setRole(selectedRole);

        rs.close();

        return true;
    }

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

    public static boolean selectDettagliUtente(Role role, Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) throws SQLException {
        openRoleConnection(role);

        String call = "{call `dettagli_utente`(?)}";
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
    public static int[] insertBatchRecapito(List<Recapito> listOfRecapito) throws SQLException {
        int[] batchResult;
        try {
            openRoleConnection(ActiveUser.getRole());

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `recapito` " +
                    "(`valore`, `anagrafica`, `tipo`)" +
                    "VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            for (Recapito recapito : listOfRecapito) {
                ps.setString(1, recapito.getValore());
                ps.setString(2, recapito.getAnagraficaID());
                String tipo = switch (recapito.getTipo()) {
                    case CELLULARE -> "cellulare";
                    case TELEFONO -> "telefono";
                    case EMAIL -> "email";
                };
                ps.setString(3, tipo);
                ps.addBatch();
            }
            ps.closeOnCompletion();

            batchResult = (ps.executeBatch());
            conn.commit();

        } finally {
            conn.setAutoCommit(true);
        }

        return batchResult;
    }

    // MESSAGGI PRIVATI
    public static boolean insertMessaggio(Role role, String usernameMittente, String usernameDestinatario, String testo) throws SQLException {
        openRoleConnection(role);

        String updateQuery = "INSERT INTO `messaggio_privato` " +
                "(`mittente`, `destinatario`, `testo`) " +
                "VALUES (?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(updateQuery);
        ps.setString(1, usernameMittente);
        ps.setString(2, usernameDestinatario);
        ps.setString(3, testo);
        ps.closeOnCompletion();

        if (ps.executeUpdate() == 0) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45006");
            e.setMessage("Utente non esistente");
        }

        return true;
    }

    public static boolean selectUtentiConMessaggi(Role role, String targetUtente, List<String> utenteIDList) throws SQLException, RuntimeException {
        openRoleConnection(role);
        String query = "SELECT `destinatario` FROM `messaggio_privato` WHERE `mittente`=? " +
                "UNION " +
                "SELECT `mittente` FROM `messaggio_privato` WHERE `destinatario`=?;";
        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, targetUtente);
        ps.setString(2, targetUtente);
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

        if (rs.first()) {
            do {
                utenteIDList.add(rs.getString(1));
            } while (rs.next());
        }

        return true;
    }

    public static boolean selectMessaggiTraUtenti(Role role, String utente1ID, String utente2ID, List<MessaggioPrivato> messaggioPrivatoList) throws SQLException {
        openRoleConnection(role);
        String query = "SELECT `mittente`, `destinatario`, `inviato`, `testo`" +
                " FROM `messaggio_privato` " +
                " WHERE (`mittente`=? AND `destinatario`=?) OR (`mittente`=? AND `destinatario`=?)" +
                " ORDER BY `inviato` ASC;";
        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, utente1ID);
        ps.setString(2, utente2ID);
        ps.setString(3, utente2ID);
        ps.setString(4, utente1ID);
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

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
    public static boolean selectCategoria(Role role, List<Categoria> categoriaList) throws SQLException {
        openRoleConnection(role);

        String query = "SELECT `nome`,`padre` FROM `categoria`;";
        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

        if (rs.first()) {
            do {
                categoriaList.add(new Categoria(rs.getString(1), rs.getString(2)));
            } while (rs.next());
        }

        return true;
    }

    public static boolean insertCategoria(Role role, String nomeCategoria, String nomePadre) throws SQLException {
        openRoleConnection(role);

        String update = "INSERT INTO `categoria` VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(update);

        ps.setString(1, nomeCategoria);
        ps.setString(2, nomePadre);
        ps.closeOnCompletion();

        ps.executeUpdate();

        return true;
    }

    public static boolean insertAnnuncio(Role role, Annuncio annuncio) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{call `inserire_annuncio`(?, ?, ?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);

        cs.setString(1, annuncio.getInserzionista());
        cs.setString(2, annuncio.getDescrizione());
        cs.setFloat(3, annuncio.getPrezzo());
        cs.setString(4, annuncio.getCategoria());
        cs.registerOutParameter(5, Types.INTEGER);
        cs.closeOnCompletion();

        cs.execute();

        annuncio.setNumero(cs.getLong(5));

        return true;
    }

    public static boolean getDettagliAnnuncio(Role role, Annuncio annuncio, List<Commento> commentoList) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{call `dettagli_annuncio`(?)}";
        CallableStatement cs = conn.prepareCall(callQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        cs.setLong(1, annuncio.getID());
        cs.closeOnCompletion();
        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
//            annuncio = BuilderAnnuncio.newFromResultSet(rs);
            annuncio.setNumero(rs.getLong(1));
            annuncio.setInserzionista(rs.getString(2));
            annuncio.setDescrizione(rs.getString(3));
            annuncio.setPrezzo((rs.getFloat(4)));
            annuncio.setCategoria(rs.getString(5));
            annuncio.setInserito(rs.getTimestamp(6).toLocalDateTime());
            annuncio.setModificato(rs.getTimestamp(7).toLocalDateTime());
            annuncio.setVenduto((rs.getTimestamp(8) == null) ? null : rs.getTimestamp(8).toLocalDateTime());

            do {
                if (rs.getString(9) != null) {
                    Commento commento = new Commento(
                            rs.getString(9),
                            annuncio.getID(),
                            rs.getTimestamp(10).toLocalDateTime(),
                            rs.getString(11)
                    );
                    commentoList.add(commento);
                }
            } while (rs.next());
        }

        return true;
    }

    public static boolean updateAnnuncioVendere(Role role, long annuncioID, String utenteID) throws SQLException {
        openRoleConnection(role);

        String call = "{call `vendere_annuncio`(?, ?)};";
        CallableStatement cs = conn.prepareCall(call);
        cs.setLong(1, annuncioID);
        cs.setString(2, utenteID);
        cs.closeOnCompletion();

        cs.executeUpdate();

        return true;
    }

    public static boolean selectAnnuncioByInserzionista(Role role, String inserzionistaID, boolean onlyAvailable, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String call = "{call `select_annunci_by_inserzionista` (?, ?)};";
        CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        cs.setString(1, inserzionistaID);
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

    public static boolean selectAnnuncioByDescrizione(Role role, String descrizione, Boolean onlyAvailable, List<Annuncio> annuncioList) throws SQLException {
        openRoleConnection(role);

        String query = "SELECT * " +
                "FROM `annuncio` " +
                "WHERE MATCH(`descrizione`) AGAINST (? IN NATURAL LANGUAGE MODE)" +
                "AND ((NOT ?) OR `venduto` IS NULL);";
        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, descrizione);
        ps.setBoolean(2, onlyAvailable);
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

        if (rs.first()) {
            do {
                annuncioList.add(BuilderAnnuncio.newFromResultSet(rs));
            } while (rs.next());
        }

        return true;
    }

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

    // SEGUE
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

    public static boolean deleteSegue(Role role, String utenteID, Long annuncioID) throws SQLException {
        openRoleConnection(role);

        String update = "DELETE FROM `segue` WHERE `utente`=? AND `annuncio`=?;";
        PreparedStatement ps = conn.prepareStatement(update);
        ps.setString(1, utenteID);
        ps.setLong(2, annuncioID);
        ps.closeOnCompletion();

        if (ps.executeUpdate() == 0) {
            CustomSQLException e = new CustomSQLException();
            e.setSQLState("45005");
            e.setMessage("Annuncio non presente fra i seguiti");
            throw e;
        }

        return true;
    }

    public static boolean selectAnnunciSeguitiModificati(Role role, String utenteID, List<Annuncio> annunciSeguitiModificatiList, boolean updateLastCheck, boolean deleteSold) throws SQLException {
        openRoleConnection(role);

        String call = "{call `controllare_annunci_seguiti` (?, ?, ?)};";
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

    // COMMENTO
    public static boolean insertCommento(Role role, String utente, Long numero, String testo) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{call `scrivere_commento`(?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);
        cs.setString(1, utente);
        cs.setLong(2, numero);
        cs.setString(3, testo);
        cs.closeOnCompletion();

        cs.executeUpdate();

        return true;
    }

    // REPORT
    public static boolean selectReport(Role role, List<ReportEntry> reportEntryList) throws SQLException {
        openRoleConnection(role);

        String query = "SELECT `username`, COALESCE(`annunci_venduti` / `annunci_inseriti` * 100.0, 0.0) as `percentuale`" +
                " FROM `utente`;";

        /*
        QUERY SENZA USARE ATTRIBUTI DI UTENTE
        String query = "SELECT `username`, COALESCE(count(`venduto`) / count(*) * 100.0, 0) as `percentuale`" +
        "FROM `utente` LEFT JOIN `annuncio` ON `utente`.`username`=`annuncio`.`inserzionista`" +
        "GROUP BY `username`;";
        */

        PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.closeOnCompletion();

        ResultSet rs = ps.executeQuery();

        if (rs.first()) {
            do {
                reportEntryList.add(new ReportEntry(rs.getString(1), rs.getFloat(2)));
            } while (rs.next());
        }

        return true;
    }
}
