package Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BuilderAnnuncio {
    public static Annuncio newFromResultSet(ResultSet rs) throws SQLException {
        return new Annuncio(
                rs.getLong(1),
                rs.getString(2),
                rs.getString(3),
                rs.getFloat(4),
                rs.getString(5),
                rs.getTimestamp(6).toLocalDateTime(),
                rs.getTimestamp(7).toLocalDateTime(),
                (rs.getTimestamp(8) == null) ? null : rs.getTimestamp(8).toLocalDateTime()
        );
    }

}
