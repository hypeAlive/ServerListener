package net.alive.serverlistener.utils;

import java.util.TimeZone;

public class TimeUtil {

    /**
     * Rundet einen Zeitstempel auf die nächste Zehnerminute.
     *
     * @param timestamp Der Zeitstempel in Millisekunden.
     * @return Der gerundete Zeitstempel in Millisekunden.
     */
    public static long roundToTenMinutes(long timestamp) {
        return (timestamp / (10 * 60 * 1000)) * (10 * 60 * 1000);
    }

    /**
     * Extrahiert die Minuten aus dem Timer-String.
     *
     * @param timerString Der Timer-String.
     * @return Die Minuten als Integer.
     */
    public static int getMinutes(String timerString){
        if(timerString.equals("Jetzt"))
            return -1;

        String[] parts = timerString.split(" ");
        if (parts.length > 2) {
            return Integer.parseInt(parts[2]);
        }

        return 0;
    }

    /**
     * Extrahiert die Stunden aus dem Timer-String.
     *
     * @param timerString Der Timer-String.
     * @return Die Stunden als Integer.
     */
    public static int getHours(String timerString){
        if(timerString.equals("Jetzt"))
            return -1;

        String[] parts = timerString.split(" ");

        return Integer.parseInt(parts[0]);
    }

    /**
     * Gibt den Startzeitstempel für den Timer-String zurück.
     *
     * @param timerString Der Timer-String.
     * @return Der Startzeitstempel in Millisekunden.
     */
    public static long getStartTimeStamp(String timerString) {
        int hours = getHours(timerString);
        int minutes = getMinutes(timerString);

        if(hours == -1 || minutes == -1)
            return -1;

        long elapsedSeconds = (hours * 3600L + minutes * 60L) * 1000;
        long day = 86400000;
        elapsedSeconds = day - elapsedSeconds;
        long startTimeStamp = System.currentTimeMillis() - elapsedSeconds;

        startTimeStamp = (startTimeStamp / (60 * 1000)) * (60 * 1000);

        return startTimeStamp;
    }

    /**
     * Überprüft, ob zwei Zeitstempel innerhalb eines Zeitfensters (in Minuten) gleich sind.
     *
     * @param timestamp1     Der erste Zeitstempel in Millisekunden.
     * @param timestamp2     Der zweite Zeitstempel in Millisekunden.
     * @param windowMinutes  Das Zeitfenster in Minuten.
     * @return true, wenn die Zeitstempel innerhalb des Zeitfensters gleich sind, andernfalls false.
     */
    public static boolean timestampsEqual(long timestamp1, long timestamp2, int windowMinutes) {
        final long timeWindow = (long) windowMinutes * 60 * 1000; // 1 Minuten in Millisekunden
        long timeDifference = Math.abs(timestamp1 - timestamp2);
        return timeDifference <= timeWindow;
    }

    /**
     * Überprüft, ob zwei Zeitstempel innerhalb eines Standardzeitfensters von 1 Minute gleich sind.
     *
     * @param timestamp1 Der erste Zeitstempel in Millisekunden.
     * @param timestamp2 Der zweite Zeitstempel in Millisekunden.
     * @return true, wenn die Zeitstempel innerhalb des Zeitfensters von 1 Minute gleich sind, andernfalls false.
     */
    public static boolean timestampsEqual(long timestamp1, long timestamp2) {
        return timestampsEqual(timestamp1, timestamp2, 1);
    }

}
