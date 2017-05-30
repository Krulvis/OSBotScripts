package api.util;

/**
 * Created by Krulvis on 12-Mar-17.
 */
public class Timer {

    private long start;
    public long end;
    public long time;
    public long stopped;

    public Timer(long time) {
        start = System.currentTimeMillis();
        this.time = time;
        this.stopped = 0L;
        this.end = (start + time);
    }

    public Timer() {
        this(0L);
    }

    public long getRemaining() {
        return end - System.currentTimeMillis();
    }

    public String getRemainingString() {
        return formatTime(getRemaining());
    }

    public boolean isFinished() {
        return System.currentTimeMillis() > end;
    }

    public void restart() {
        stop();
        reset();
    }

    public void reset() {
        start = System.currentTimeMillis();
        end = start + time;
    }

    public void finish() {
        end = System.currentTimeMillis();
    }

    public void stop() {
        end = (end - start + System.currentTimeMillis());
        stopped = System.currentTimeMillis();
    }

    public boolean isRunning() {
        return start != 0L;
    }

    public long getElapsedTime() {
        if (stopped != 0L) {
            return stopped - start;
        }
        return System.currentTimeMillis() - start;
    }

    public int getElapsedMinutes() {
        return (int) getElapsedTime() / 60000;
    }

    public static int getXPForLevel(int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = (int) Math.floor(points / 4);
        }
        return 0;
    }

    public String getTimeToNextLevel(int xpPerHour, int level, long xp) {
        long elapsed;
        if (xpPerHour < 1) {
            elapsed = 0;
        } else {
            elapsed = (long) (((getXPForLevel(level + 1) - xp) * 3600000D) / xpPerHour);
        }
        return formatTime(elapsed);
    }

    public int getPerHour(long gained) {
        if (gained == 0) {
            return 0;
        }
        return (int) Math.ceil(gained * 3600000.0D / (System.currentTimeMillis() - start));
    }

    @Override
    public String toString() {
        return toString(getElapsedTime());
    }

    public static String toString(long time) {
        return formatTime(time);
    }

    public static String formatTime(long time) {
        if (time <= 0)
            return "--:--:--";
        final StringBuilder t = new StringBuilder();
        final long totalSec = time / 1000;
        final long totalMin = totalSec / 60;
        final long totalHour = totalMin / 60;
        final long totalDay = totalHour / 24;
        final int second = (int) totalSec % 60;
        final int minute = (int) totalMin % 60;
        final int hour = (int) totalHour % 24;
        final int day = (int) totalDay;
        if (day > 0) {
            t.append(day);
            t.append(":");
        }
        if (hour < 10)
            t.append("0");
        t.append(hour);
        t.append(":");
        if (minute < 10)
            t.append("0");
        t.append(minute);
        t.append(":");
        if (second < 10)
            t.append("0");
        t.append(second);
        return t.toString();
    }
}
