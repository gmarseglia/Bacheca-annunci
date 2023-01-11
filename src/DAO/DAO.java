package DAO;

import Model.*;
import Model.Exception.AnnuncioVendutoException;
import Model.Exception.CustomSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bacheca_annunci";

    private static Role LAST_ROLE = null;
    private static final String ROOT_USER = "giuseppe";
    private static final String ROOT_PASS = "medium14";

    private static final String BASE_USER = "base";
    private static final String BASE_PASS = "base";

    private static final String REGISTRATORE_USER = "registratore";
    private static final String REGISTRATORE_PASS = "registratore";

    private static Connection conn;
    private static Statement stmt = null;
    private static String query;

    private static ResultSet rs = null;

    // CONNESSIONE
    private static void openRoleConnection(Role role) throws SQLException {
        if (conn == null || LAST_ROLE != role) {
            try {
                conn.close();
            } catch (Exception e) {

            }

            String targetUser, targetPass;

            switch (role) {
                case BASE:
                    targetUser = BASE_USER;
                    targetPass = BASE_PASS;
                    break;
                case REGISTRATORE:
                    targetUser = REGISTRATORE_USER;
                    targetPass = REGISTRATORE_PASS;
                    break;
                case ROOT:
                default:
                    targetUser = ROOT_USER;
                    targetPass = ROOT_PASS;
            }

            conn = DriverManager.getConnection(DB_URL, targetUser, targetPass);
            LAST_ROLE = role;
        }
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // UTENTE
    public static boolean insertUtenteByUsername(String username) {
        boolean valueReturn = false;

        try {
            // #1: connect
            openRoleConnection(Role.ROOT);

            // #2: create statement
            stmt = conn.createStatement();
//            System.out.println("Statement created.\n");

            // #3: query
            query = String.format("INSERT INTO `utente` (username) VALUES ('%s')", username);

            // #4: execute query
            stmt.executeUpdate(query);

            valueReturn = true;

        } catch (Exception e) {
            System.out.println("Exception caught!\n");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
//                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return valueReturn;
    }

    public static boolean selectDettagliUtente(Role role, Utente utente, Anagrafica anagrafica, List<Recapito> recapitoList) {
        boolean result = false;
        try {
            openRoleConnection(role);

            String call = "{call `dettagli_utente`(?)}";
            CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            cs.setString(1, utente.getID());

            ResultSet rs = cs.executeQuery();

            if (!rs.first()) throw new RuntimeException("Utente non trovato.");

            utente.setAnnunci_inseriti(rs.getInt(1));
            utente.setAnnunci_venduti(rs.getInt(2));

            anagrafica.setCodiceFiscale(rs.getString(3));
            //#TODO

            if (cs.getMoreResults()) {
                rs = cs.getResultSet();

                //#TODO
                if (rs.first()) {
                    do {
                        Recapito recapito = new Recapito(rs.getString(1), null, null);
                        recapitoList.add(recapito);
                    } while (rs.next());
                }
            }

            result = true;

            cs.close();

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BatchResult insertBatchUtenteOnlyUsername(Role role, List<Utente> utenti) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `utente` (`username`) VALUES (?)";
            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Utente utente : utenti) {
                psmnt.setString(1, utente.getUsername());
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    public static List<String> getAllUsernames() {

        List<String> returnList = new ArrayList<String>();

        try {
            // #1: connect
            openRoleConnection(Role.ROOT);

            // #2: create statement
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            System.out.println("Statement created.\n");

            // #3: query
            query = "SELECT username FROM `utente`";

            // #4: execute query
            rs = stmt.executeQuery(query);

//            if (!rs.first()) {
//                throw new Exception("No result found!");
//            }

            if (rs.first()) {
                do {
                    returnList.add(rs.getString("username"));
                } while (rs.next());
            }

        } catch (Exception e) {
            System.out.println("Exception caught!\n");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
//                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return returnList;
    }

    public static boolean deleteAllUtente() {

        boolean valueReturn = false;

        try {
            // #1: connect
            openRoleConnection(Role.ROOT);

            // #2: create statement
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // #3: query
            query = "DELETE FROM utente";

            // #4: execute query
            stmt.executeUpdate(query);

            valueReturn = true;

        } catch (Exception e) {
            System.out.println("Exception caught!\n");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return valueReturn;
    }

    // ANAGRAFICA
    public static BatchResult insertBatchAnagrafica(Role role, List<Anagrafica> listOfAnagrafica) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `anagrafica` " +
                    "(`codice_fiscale`, `nome`, `cognome`, " +
                    "`sesso`, `data_nascita`, `comune_nascita`," +
                    "`indirizzo_residenza`, `indirizzo_fatturazione`, `utente`)" +
                    "VALUES (?, ?, ?," +
                    "?, ?, ?," +
                    "?, ?, ?)";

            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Anagrafica anagrafica : listOfAnagrafica) {
                psmnt.setString(1, anagrafica.getCodiceFiscale());
                psmnt.setString(2, anagrafica.getNome());
                psmnt.setString(3, anagrafica.getCognome());
                psmnt.setString(4, (anagrafica.getSesso() == Sesso.DONNA) ? "Donna" : "Uomo");
                psmnt.setDate(5, Date.valueOf(anagrafica.getDataNascita()));
                psmnt.setString(6, anagrafica.getComuneNascita());
                psmnt.setString(7, anagrafica.getIndirizzoResidenza());
                psmnt.setString(8, anagrafica.getIndirizzoFatturazione());
                psmnt.setString(9, anagrafica.getUsernameUtente());
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }


    // CREDENZIALI
    public static BatchResult insertBatchCredenziali(Role role, List<Credenziali> credentials) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `credenziali` (`username`, `password`, `ruolo`)" +
                    "VALUES (?, ?, ?)";
            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Credenziali credential : credentials) {
                psmnt.setString(1, credential.getUsername());
                psmnt.setString(2, credential.getPassword());
                psmnt.setString(3, (credential.getRole() == Role.BASE) ? "base" : "gestore");
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
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
                    default -> throw new RuntimeException("No type found in Recapito");
                };
                ps.setString(3, tipo);
                ps.addBatch();
            }
            ps.closeOnCompletion();

            batchResult = (ps.executeBatch());
            conn.commit();

        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return batchResult;
    }

    public static BatchResult insertBatchRecapitoPreferito(Role role, List<Recapito> listOfRecapito) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `recapito_preferito` " +
                    "(`anagrafica`, `recapito`)" +
                    "VALUES (?, ?)";
            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Recapito recapito : listOfRecapito) {
                psmnt.setString(1, recapito.getAnagraficaID());
                psmnt.setString(2, recapito.getValore());
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    // MESSAGGI PRIVATI
    public static boolean insertMessaggio(Role role, MessaggioPrivato messaggioPrivato) {
        boolean result = false;
        try {
            openRoleConnection(role);

            String updateQuery = "INSERT INTO `messaggio_privato` " +
                    "(`mittente`, `destinatario`, `testo`) " +
                    "VALUES (?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, messaggioPrivato.getMittente());
            ps.setString(2, messaggioPrivato.getDestinatario());
            ps.setString(3, messaggioPrivato.getTesto());
            ps.executeUpdate();

            result = true;

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BatchResult insertBatchMessaggioPrivato(Role role, List<MessaggioPrivato> listOfMessaggioPrivato) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `messaggio_privato` " +
                    "(`mittente`, `destinatario`, `inviato`, `testo`)" +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement psmnt = conn.prepareStatement(query);

            for (MessaggioPrivato messaggioPrivato : listOfMessaggioPrivato) {
                psmnt.setString(1, messaggioPrivato.getMittente());
                psmnt.setString(2, messaggioPrivato.getDestinatario());
                psmnt.setTimestamp(3, Timestamp.valueOf(messaggioPrivato.getInviato()));
                psmnt.setString(4, messaggioPrivato.getTesto());
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    public static boolean selectUtentiConMessaggi(Role role, String targetUtente, List<String> utenteIDList) {
        boolean result = false;
        try {
            openRoleConnection(role);
            String query = "SELECT `destinatario` FROM `messaggio_privato` WHERE `mittente`=? " +
                    "UNION " +
                    "SELECT `mittente` FROM `messaggio_privato` WHERE `destinatario`=?;";
            PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, targetUtente);
            ps.setString(2, targetUtente);

            ResultSet rs = ps.executeQuery();

            if (!rs.first()) throw new RuntimeException("Empty result set.");

            do {
                utenteIDList.add(rs.getString(1));
            } while (rs.next());

            result = true;

            ps.close();
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean selectMessaggiTraUtenti(Role role, String utente1ID, String utente2ID, List<MessaggioPrivato> messaggioPrivatoList) {
        boolean result = false;
        try {
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

            ResultSet rs = ps.executeQuery();

            if (!rs.first()) throw new RuntimeException("Empty result set.");

            do {
                MessaggioPrivato messaggioPrivato = new MessaggioPrivato(
                        rs.getString(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(), rs.getString(4)
                );
                messaggioPrivatoList.add(messaggioPrivato);
            } while (rs.next());

            result = true;

            ps.close();
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
        return result;
    }

    // CATEGORIA
    public static boolean insertCategoria(Role role, Categoria categoria) {
        List<Categoria> categoriaList = new ArrayList<>();
        categoriaList.add(categoria);
        BatchResult batchResult = insertBatchCategoria(role, categoriaList);
        return batchResult.getAllTrue();
    }

    public static BatchResult insertBatchCategoria(Role role, List<Categoria> listOfCategoria) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `categoria` VALUES (?, ?)";

            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Categoria categoria : listOfCategoria) {
                psmnt.setString(1, categoria.getNome());
                if (categoria.getPadre() == null) {
                    psmnt.setNull(2, Types.VARCHAR);
                } else {
                    psmnt.setString(2, categoria.getPadre());
                }
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    public static boolean deleteAllCategoria(Role role) {
        boolean result = false;
        try {
            openRoleConnection(role);

            String query = "DELETE FROM `categoria`";
            Statement s = conn.createStatement();
            s.executeUpdate(query);

            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ANNUNCIO
    public static boolean resetAutoincrement() {
        boolean valueReturn = false;

        try {
            // #1: connect
            openRoleConnection(Role.ROOT);

            // #2: create statement
            stmt = conn.createStatement();
//            System.out.println("Statement created.\n");

            // #3: query
            query = "ALTER TABLE `annuncio` AUTO_INCREMENT = 1";

            // #4: execute query
            stmt.executeUpdate(query);

            valueReturn = true;

        } catch (Exception e) {
            System.out.println("Exception caught!\n");
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
//                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return valueReturn;
    }

    public static boolean insertAnnuncio(Role role, Annuncio annuncio) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{call `inserire_annuncio`(?, ?, ?, ?, ?)}";
        CallableStatement cs = conn.prepareCall(callQuery);

        cs.setString(1, annuncio.getInserzionista());
        cs.setString(2, annuncio.getDescrizione());
        cs.setFloat(3, annuncio.getPrice());
        cs.setString(4, annuncio.getCategoria());
        cs.registerOutParameter(5, Types.INTEGER);
        cs.closeOnCompletion();

        cs.execute();

        annuncio.setNumero(cs.getLong(5));

        return true;
    }

    public static BatchResult insertBatchAnnuncio(Role role, List<Annuncio> listOfAnnuncio) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `annuncio`" +
                    "(`inserzionista`, `descrizione`, `prezzo`," +
                    "`categoria`, `inserito`) " +
                    "VALUES " +
                    "(?, ?, ?, " +
                    "?, ?)";

            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Annuncio annuncio : listOfAnnuncio) {
                psmnt.setString(1, annuncio.getInserzionista());
                psmnt.setString(2, annuncio.getDescrizione());
                psmnt.setFloat(3, annuncio.getPrice());
                psmnt.setString(4, annuncio.getCategoria());
                psmnt.setTimestamp(5, Timestamp.valueOf(annuncio.getInserito()));
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    public static boolean getDettagliAnnuncio(Role role, Annuncio annuncio, List<Commento> commentoList) throws SQLException {
        openRoleConnection(role);

        String callQuery = "{call `dettagli_annuncio`(?)}";
        CallableStatement cs = conn.prepareCall(callQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        cs.setLong(1, annuncio.getID());
        cs.closeOnCompletion();
        ResultSet rs = cs.executeQuery();

        if (rs.first()) {
            annuncio = BuilderAnnuncio.newFromResultSet(rs);
//            annuncio.setNumero(rs.getInt(1));
//            annuncio.setInserzionista(rs.getString(2));
//            annuncio.setDescrizione(rs.getString(3));
//            annuncio.setPrice((rs.getFloat(4)));
//            annuncio.setCategoria(rs.getString(5));
//            annuncio.setInserito(rs.getTimestamp(6).toLocalDateTime());
//            annuncio.setModificato(rs.getTimestamp(7).toLocalDateTime());
//            annuncio.setVenduto((rs.getTimestamp(8) == null) ? null : rs.getTimestamp(8).toLocalDateTime());

            do {
                Commento commento = new Commento(
                        rs.getString(9),
                        annuncio.getID(),
                        rs.getTimestamp(10).toLocalDateTime(),
                        rs.getString(11)
                );
                commentoList.add(commento);
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

        if(ps.executeUpdate() == 0){
            CustomSQLException e = new CustomSQLException();
            e.setState("45005");
            e.setMessage("Annuncio non presente fra i seguiti");
            throw e;
        }

        return true;
    }

    public static BatchResult insertBatchSegue(Role role, List<Segue> listOfSegue) {
        int[] singlesResult = null;
        try {
            openRoleConnection(role);

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `segue` (`utente`, `annuncio`) VALUES (?, ?)";

            PreparedStatement psmnt = conn.prepareStatement(query);

            for (Segue segue : listOfSegue) {
                psmnt.setString(1, segue.getUtente());
                psmnt.setLong(2, segue.getAnnuncio());
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new BatchResult(singlesResult);
    }

    public static boolean selectAnnunciSeguitiModificati(Role role, String utenteID,
                                                         List<Annuncio> annunciSeguitiModificatiList,
                                                         boolean updateLastCheck, boolean deleteSold) {
        boolean result = false;
        try {
            openRoleConnection(role);

            String call = "{call `controllare_annunci_seguiti` (?, ?, ?)};";
            CallableStatement cs = conn.prepareCall(call, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            cs.setString(1, utenteID);
            cs.setBoolean(2, updateLastCheck);
            cs.setBoolean(3, deleteSold);

            ResultSet rs = cs.executeQuery();
            result = true;

            if (rs.first()) {
                do {
                    Annuncio newAnnuncio = new Annuncio(rs.getLong(1), rs.getString(2), rs.getString(3),
                            (long) (rs.getFloat(4) * 100), rs.getString(5),
                            rs.getTimestamp(6).toLocalDateTime(), rs.getTimestamp(7).toLocalDateTime(),
                            (rs.getTimestamp(8) == null) ? null : rs.getTimestamp(8).toLocalDateTime());
                    annunciSeguitiModificatiList.add(newAnnuncio);
                } while (rs.next());
            }

            cs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // COMMENTO
    public static boolean insertCommento(Role role, Commento commento) throws AnnuncioVendutoException {
        boolean result = false;
        try {
            openRoleConnection(role);

            String callQuery = "{call `scrivere_commento`(?, ?, ?)}";
            CallableStatement cs = conn.prepareCall(callQuery);
            cs.setString(1, commento.getUtente());
            cs.setInt(2, (int) commento.getAnnuncio());
            cs.setString(3, commento.getTesto());

            cs.executeUpdate();

            result = true;

            cs.close();
        } catch (SQLException e) {
            if (e.getSQLState().equals("45001")) {
                throw new AnnuncioVendutoException();
            } else {
                e.printStackTrace();
            }
        }
        return result;
    }

    // REPORT
    public static boolean selectReport(Role role, List<ReportEntry> reportEntryList) {
        boolean result = false;
        try {
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
            result = true;

            if (rs.first()) {
                do {
                    reportEntryList.add(new ReportEntry(rs.getString(1), rs.getFloat(2)));
                } while (rs.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
