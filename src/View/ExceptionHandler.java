package View;

import Controller.DatabaseConnectionController;

public class ExceptionHandler {

    public static void handleInputInterrupted() {
        System.out.println("Ricevuto CTRL+D durante la richiesta di input.\n" +
                "Chiusura applicazione.");
        String message;
        message = DatabaseConnectionController.closeConnection();
        if (message != null) System.out.println(message);
        System.exit(0);
    }
}
