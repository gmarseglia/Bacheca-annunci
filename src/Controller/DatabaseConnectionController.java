package Controller;

import DAO.DAO;

import java.sql.SQLException;

public class DatabaseConnectionController {
    public static String closeConnection() {
        try {
            DAO.closeConnection();
        } catch (SQLException e) {
            return String.format("Impossibile chiudere la connessione [%s, %s].", e.getSQLState(), e.getMessage());
        }
        return null;
    }
}
