package net.alive.serverlistener.utils;

public class TimeUtil {

    public static long getStartTimeStamp(String timerString) {
        String[] parts = timerString.split(" ");
        int hours = 0;
        int minutes = 0;
        if (parts.length > 2) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[2]);
        } else {
            hours = Integer.parseInt(parts[0]);
        }
        long elapsedSeconds = hours * 3600L + minutes * 60L;
        long startTimeStamp = System.currentTimeMillis() - elapsedSeconds * 1000;
        startTimeStamp = (startTimeStamp / (24 * 3600 * 1000)) * (24 * 3600 * 1000);
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
