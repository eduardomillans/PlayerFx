package utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TimeFormatter {

    public static String convertMillisToStrTime(long timeInMilliseconds) {
        String strFormatter = "%02d:%02d";
        long minutesOperation = TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) % TimeUnit.HOURS.toMinutes(1);
        long secondsOperation = TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) % TimeUnit.MINUTES.toSeconds(1);

        return String.format(strFormatter, minutesOperation, secondsOperation);
    }

    public static String formatSongDuration(long timeInMilliseconds, AtomicReference<String> durationSuffix) {
        return TimeFormatter.convertMillisToStrTime(timeInMilliseconds) + " / " + durationSuffix;
    }

}
