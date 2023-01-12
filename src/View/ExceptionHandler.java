package View;

import Model.Exception.InputInterruptedRuntimeException;

import java.sql.SQLException;

public class ExceptionHandler {
    public static void handleSQLException(SQLException e) {
        System.out.printf("SQLException avvenuta: %s.", e.getMessage());
        System.exit(1);
    }

    public static void handleInputInterrupted(InputInterruptedRuntimeException e){
        System.out.println("Ricevuto CTRL+D durante la richiesta di input.");
        System.exit(0);
    }
}
