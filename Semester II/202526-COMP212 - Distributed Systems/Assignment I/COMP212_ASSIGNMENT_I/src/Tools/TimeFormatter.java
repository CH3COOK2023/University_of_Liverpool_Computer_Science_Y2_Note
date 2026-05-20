package Tools;

public class TimeFormatter {
    public static String format(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d Hours, %02d mins, %02d secs", hours, minutes, seconds);
    }
}
