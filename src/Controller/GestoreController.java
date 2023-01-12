package Controller;

import Model.ActiveUser;
import DAO.DAO;
import DAO.DBResult;
import Model.ReportEntry;

import java.sql.SQLException;
import java.util.List;

public class GestoreController extends BaseController {

    public static DBResult creareCategoria(String nomeCategoria, String nomePadre) {
        DBResult dbResult = new DBResult(false);
        try {
            dbResult.setResult(DAO.insertCategoria(ActiveUser.getRole(), nomeCategoria, nomePadre));
        } catch (SQLException e) {
            dbResult.setMessage(switch (e.getSQLState()) {
                case "23000" ->
                        String.format("Esiste già una categoria con lo stesso nome o non esiste la categoria padre [%s]", e.getMessage());
                case "45010" -> String.format("La categoria non può essere padre di se stessa [%s]", e.getMessage());
                default -> getGenericSQLExceptionMessage(e);
            });
        }
        return dbResult;
    }

    public static boolean generareReport(List<ReportEntry> reportEntryList) {
        return DAO.selectReport(ActiveUser.getRole(), reportEntryList);
    }
}
