package Controller;

import Model.ActiveUser;
import Model.Categoria;
import DAO.DAO;
import Model.ReportEntry;

import java.util.List;

public class GestoreController {
    public static boolean creareCategoria(Categoria categoria) {
        return DAO.insertCategoria(ActiveUser.getRole(), categoria);
    }

    public static boolean generareReport(List<ReportEntry> reportEntryList) {
        return DAO.selectReport(ActiveUser.getRole(), reportEntryList);
    }
}
