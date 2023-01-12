package Model.Exception;

import java.sql.SQLException;

public class CustomSQLException extends SQLException {
    private String state;
    private String message;

    @Override
    public String getSQLState() {
        return state;
    }

    public void setSQLState(String state) {
        this.state = state;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
