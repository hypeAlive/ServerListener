package net.alive.serverlistener.utils;

import java.util.TimeZone;

public class TimeUtil {

    public static int getMinutes(String timerString){
        if(timerString.equals("Jetzt"))
            return -1;

        String[] parts = timerString.split(" ");
        if (parts.length > 2) {
            return Integer.parseInt(parts[2]);
        }

        return 0;
    }

    public static int getHours(String timerString){
        if(timerString.equals("Jetzt"))
            return -1;

        String[] parts = timerString.split(" ");

        return Integer.parseInt(parts[0]);
    }

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

    public static boolean timestampsEqual(long timestamp1, long timestamp2, int windowMinutes) {
        final long timeWindow = (long) windowMinutes * 60 * 1000; // 1 Minuten in Millisekunden
        long timeDifference = Math.abs(timestamp1 - timestamp2);
        return timeDifference <= timeWindow;
    }

    public static boolean timestampsEqual(long timestamp1, long timestamp2) {
        return timestampsEqual(timestamp1, timestamp2, 1);
    }

}
