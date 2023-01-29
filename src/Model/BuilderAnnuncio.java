package Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BuilderAnnuncio {
    public static Annuncio newAvailableFromResultSet(ResultSet rs) throws SQLException {
        return new Annuncio(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getTimestamp(5).toLocalDateTime(),
                (rs.getTimestamp(6) == null) ? null : rs.getTimestamp(6).toLocalDateTime(),
                null
        );
    }

    public static Annuncio newFromResultSet(ResultSet rs) throws SQLException {
        return new Annuncio(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getTimestamp(5).toLocalDateTime(),
                rs.getTimestamp(6).toLocalDateTime(),
                (rs.getTimestamp(7) == null) ? null : rs.getTimestamp(7).toLocalDateTime()
        );
    }

}
