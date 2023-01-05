package DAO;

import Model.*;

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

    public static boolean loginUtente(Role role, Credenziali credenziali) {
        boolean result = false;
        try {
            openRoleConnection(role);

            String query = "SELECT `ruolo` FROM `credenziali` " +
                    "WHERE `username`=? AND `password`=?;";

            ResultSet rs;
            PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, credenziali.getUsername());
            ps.setString(2, credenziali.getPassword());
            rs = ps.executeQuery();

            if (!rs.first()) return false;

            Role selectedRole = switch (rs.getString(1)) {
                case "base" -> Role.BASE;
                case "gestore" -> Role.GESTORE;
                default -> throw new RuntimeException("Invalid role");
            };
            credenziali.setRole(selectedRole);

            result = true;

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean registrazioneUtente(Utente utente, Credenziali credenziali, Anagrafica anagrafica, Recapito recapitoPreferito) {
        boolean valueReturn = false;

        try {
            // #1: connect
            openRoleConnection(ActiveUser.getRole());

            // #2: create statement
            String callQuery = "{call `registrazione_utente` (" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement cs = conn.prepareCall(callQuery);
            stmt = conn.createStatement();

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

            cs.execute();

            cs.close();

            valueReturn = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return valueReturn;
    }

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

    public static boolean resetAutoincrement() {
        boolean valueReturn = false;

        try {
            // #1: connect
            openRoleConnection(Role.ROOT);

            // #2: create statement
            stmt = conn.createStatement();
//            System.out.println("Statement created.\n");

            // #3: query
            query = String.format("ALTER TABLE `annuncio` AUTO_INCREMENT = 1");

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
                psmnt.setString(1, messaggioPrivato.getMittente().getUsername());
                psmnt.setString(2, messaggioPrivato.getDestinatario().getUsername());
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

    public static BatchResult insertBatchRecapito(List<Recapito> listOfRecapito) {
        int[] singlesResult = null;
        PreparedStatement psmnt;
        try {
            openRoleConnection(ActiveUser.getRole());

            conn.setAutoCommit(false);

            String query = "INSERT IGNORE INTO `recapito` " +
                    "(`valore`, `anagrafica`, `tipo`)" +
                    "VALUES (?, ?, ?)";
            psmnt = conn.prepareStatement(query);

            for (Recapito recapito : listOfRecapito) {
                psmnt.setString(1, recapito.getValore());
                psmnt.setString(2, recapito.getAnagraficaID());
                String tipo = switch (recapito.getTipo()) {
                    case CELLULARE -> "cellulare";
                    case TELEFONO -> "telefono";
                    case EMAIL -> "email";
                    default -> throw new RuntimeException("No type found in Recapito");
                };
                psmnt.setString(3, tipo);
                psmnt.addBatch();
            }

            singlesResult = psmnt.executeBatch();
            conn.commit();

            psmnt.close();

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

    public static boolean insertCategoria(Role role, Categoria categoria) {
        List<Categoria> categoriaList = new ArrayList<>();
        categoriaList.add(categoria);
        BatchResult batchResult = insertBatchCategoria(role, categoriaList);
        return batchResult.getAllTrue();
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
                psmnt.setFloat(3, annuncio.getPriceInCents() / 100);
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

    public static void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
