package net.alive.serverlistener.utils;

import net.alive.serverlistener.CxnListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class TimeUtil {

    private static String[] MINUTE_SEARCH = { "Minute" };
    private static String[] NOW_SEARCH = { "Jetzt" };

    private static String[] HOUR_SEARCH = { "Stunde" };

    private static String[] SECOND_SEARCH = { "Sekunde" };

    /**
     * Rundet einen Zeitstempel auf die nächste Zehnerminute.
     *
     * @param timestamp Der Zeitstempel in Millisekunden.
     * @return Der gerundete Zeitstempel in Millisekunden.
     */
    public static long roundToTenMinutes(long timestamp) {

        Instant instant = Instant.ofEpochMilli(timestamp);

        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        int minute = dateTime.getMinute();

        dateTime = dateTime.withMinute(minute - minute % 10).withSecond(0);

        System.out.println("dateTime : " + dateTime);
        System.out.println("minute : " + minute);

        long newTimestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long oldTimestamp = (timestamp / (10 * 60 * 1000)) * (10 * 60 * 1000);
        long maybeNew = (timestamp + 300000) / 600000 * 600000;
        System.out.println(newTimestamp + " : " + oldTimestamp + " : " + maybeNew);

        return (timestamp / (10 * 60 * 1000)) * (10 * 60 * 1000);
    }

    /**
     * Extrahiert die Minuten aus dem Timer-String.
     *
     * @param timerString Der Timer-String.
     * @return Die Minuten als Integer.
     */
    public static int getMinutes(String timerString) {
        if (StringUtil.containsString(timerString, NOW_SEARCH))
            return -1;

        String[] parts = timerString.split(" ");
        if (parts.length > 2) {
            return Integer.parseInt(parts[2]);
        } else {
            return StringUtil.containsString(parts[1], MINUTE_SEARCH) ? Integer.parseInt(parts[0]) : 0;
        }
    }

    /**
     * Extrahiert die Stunden aus dem Timer-String.
     *
     * @param timerString Der Timer-String.
     * @return Die Stunden als Integer.
     */
    public static int getHours(String timerString) {
        if (StringUtil.containsString(timerString, NOW_SEARCH))
            return -1;

        String[] parts = timerString.split(" ");

        if (parts.length > 2) {
            return Integer.parseInt(parts[0]);
        } else {
            return StringUtil.containsString(parts[1], HOUR_SEARCH) ? Integer.parseInt(parts[0]) : 0;
        }
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

        if(StringUtil.containsString(timerString, SECOND_SEARCH))
            return -1;

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

    public static void refreshTimeSearch(){
        if(!CxnListener.CONNECTED_TO_SERVER) return;

        String[] minuteSearch = CxnListener.getTranslationsAsArray("cxnprice.translation.time.minute");
        String[] secondSearch = CxnListener.getTranslationsAsArray("cxnprice.translation.time.second");
        String[] nowSearch = CxnListener.getTranslationsAsArray("cxnprice.translation.time.now");
        String[] hourSearch = CxnListener.getTranslationsAsArray("cxnprice.translation.time.hour");

        if(minuteSearch != null)
            MINUTE_SEARCH = minuteSearch;

        if(secondSearch != null)
            SECOND_SEARCH = secondSearch;

        if(nowSearch != null)
            NOW_SEARCH = nowSearch;

        if(hourSearch != null)
            HOUR_SEARCH = hourSearch;

    }

}
