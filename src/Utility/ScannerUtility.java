package Utility;

import Model.Exception.InputInterruptedRuntimeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ScannerUtility {

    private static final Scanner scanner = new Scanner(System.in);
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static String getFirstNChar(int n) {
        String nextLine = scanner.nextLine();
        return nextLine.substring(0, Math.min(nextLine.length(), n));
    }

    public static String getFirstChar() {
        return getFirstNChar(1);
    }

    public static String askFirstChar(String ask) {
        String resultString;
        do {
            System.out.printf("%s -> ", ask);
            resultString = getFirstChar();
        } while (resultString.length() < 1);
        return resultString;
    }

    public static String getString() {
        try {
            String returnString = scanner.nextLine();
            return returnString.split("[ \\t]")[0];
        } catch (NoSuchElementException e) {
            throw new InputInterruptedRuntimeException();
        }
    }

    public static String getText() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new InputInterruptedRuntimeException();
        }
    }

    public static String askString(String ask, int limit) {
        String resultString;
        do {
            System.out.printf("%s -> ", ask);
            resultString = getString();
            if (resultString.length() > limit)
                System.out.printf("Stringa inserita è troppo lunga, lunghezza massima %d.\n", limit);
        } while (resultString.length() < 1 || resultString.length() > limit);
        return resultString;
    }

    public static String askText(String ask, int limit) {
        String resultString;
        do {
            System.out.printf("%s -> ", ask);
            resultString = getText();
            if (resultString.length() > limit)
                System.out.printf("Stringa inserita è troppo lunga, lunghezza massima %d.\n", limit);
        } while (resultString.length() < 1 || resultString.length() > limit);
        return resultString;
    }

    public static LocalDate askLocalDate(String ask) {
        LocalDate resultDate = null;
        String input;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        do {
            System.out.printf("%s nel formato dd-MM-yyyy (es. 14-03-2000) -> ", ask);
            input = getString();
            try {
                resultDate = LocalDate.parse(input, formatter);
            } catch (Exception e) {
                resultDate = null;
            }
        } while (resultDate == null);

        return resultDate;
    }

    public static Float askFloat(String ask) {
        Float result;
        do {
            System.out.printf("%s (es. 10.20) ->", ask);
            try {
                result = Float.parseFloat(getString());
            } catch (RuntimeException e) {
                result = null;
            }
        } while (result == null);

        return result;
    }

    public static void askAny() {
        System.out.println("Inviare qualsiasi carattere per proseguire.");
        getFirstChar();
    }

    public static Long askLong(String ask) {
        Long result;
        do {
            System.out.printf("%s) ->", ask);
            try {
                result = Long.parseLong(getString());
            } catch (RuntimeException e) {
                result = null;
            }
        } while (result == null);

        return result;
    }
}