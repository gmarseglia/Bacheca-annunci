package Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class RndData {

    private static final Random random = new Random();
    private static final String lowercaseITA = "abdefgilmnoprstuvz ";
    private static final String lowercase = "abdefghijklmnopqrstuvwxyz ";
    private static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    private static final String uppercaseITA = "ABCDEFGILMNOPRSTUVZ ";
    private static final String digits = "0123456789";

    private static final int earliestYear = 1960;
    private static final int lastYear = 2005;

    private static final int firstServiceYear = 2020;
    private static final int lastServiceYear = 2022;


    private static final boolean useITA = true;

    public static int getRandomInt(int min, int max) {
        int result = RndData.random.nextInt(max - min + 1);
        return (result + min);
    }

    public static char getRandomLowercaseChar() {
        String targetLowercase = (useITA) ? lowercaseITA : lowercase;
        return targetLowercase.charAt(getRandomInt(0, targetLowercase.length() - 2));
    }
    public static char getRandomLowercaseChar(boolean canBlank) {
        String targetLowercase = (useITA) ? lowercaseITA : lowercase;
        return targetLowercase.charAt(getRandomInt(0, targetLowercase.length() - (canBlank ? 1 : 2) ));
    }

    public static char getRandomUppercaseChar() {
        String targetUppercase = (useITA) ? uppercaseITA : uppercase;
        return targetUppercase.charAt(getRandomInt(0, targetUppercase.length() - 2));
    }

    public static char getRandomDigitChar() {
        return digits.charAt(getRandomInt(0, digits.length() - 1));
    }

    public static String getRandomCF() {
        String result = "";
        for (int i = 0; i < 6; i++) result += getRandomUppercaseChar();
        for (int i = 0; i < 2; i++) result += getRandomDigitChar();
        for (int i = 0; i < 1; i++) result += getRandomUppercaseChar();
        for (int i = 0; i < 2; i++) result += getRandomDigitChar();
        for (int i = 0; i < 1; i++) result += getRandomUppercaseChar();
        for (int i = 0; i < 3; i++) result += getRandomDigitChar();
        for (int i = 0; i < 1; i++) result += getRandomUppercaseChar();
        return result;
    }

    public static String randomString(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += (i == 0) ? getRandomUppercaseChar() : getRandomLowercaseChar();
        }
        return result;
    }
    public static String randomStringWithBlanks(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            result += (i == 0) ? getRandomUppercaseChar() : getRandomLowercaseChar(true);
        }
        return result;
    }

    public static LocalDate randomDate() {
        LocalDate earliest = LocalDate.ofYearDay(earliestYear, 1);
        LocalDate last = LocalDate.ofYearDay(lastYear, 365);

        return LocalDate.ofEpochDay(random.nextLong(earliest.toEpochDay(), last.toEpochDay()));
    }

    public static LocalDateTime randomDateTime(){
        LocalDateTime earliest = LocalDateTime.of(
                LocalDate.ofYearDay(firstServiceYear, 1), LocalTime.of(0,0,0));
        LocalDateTime last = LocalDateTime.of(
                LocalDate.ofYearDay(lastServiceYear, 365), LocalTime.of(23,59,59));

        LocalDate day = LocalDate.ofEpochDay(random.nextLong(
                earliest.toLocalDate().toEpochDay(), last.toLocalDate().toEpochDay()
        ));

        LocalTime time = LocalTime.ofSecondOfDay(random.nextLong(
                earliest.toLocalTime().toSecondOfDay(), last.toLocalTime().toSecondOfDay()
        ));

        return LocalDateTime.of(day, time);
    }


}
