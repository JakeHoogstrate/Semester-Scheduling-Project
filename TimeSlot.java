public class TimeSlot {
    public enum DayPattern { MWF, TTH }

    private DayPattern pattern;
    private int startTime;

    public TimeSlot(DayPattern pattern, int startTime) {
        this.pattern = pattern;
        this.startTime = startTime;
    }

    public DayPattern getPattern() {
        return pattern;
    }

    public DayPattern getDayPattern() {
        return pattern;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        if (pattern == DayPattern.MWF) {
            return addMinutes(startTime, 50);
        } else {
            return addMinutes(startTime, 75);
        }
    }

    private int addMinutes(int time, int mins) {
        int hours = time / 100;
        int minutes = time % 100;
        minutes += mins;
        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }
        return hours * 100 + minutes;
    }



    public boolean overlapsWith(TimeSlot other) {
        if (this.pattern != other.pattern) return false;
        return this.startTime < other.getEndTime() && other.getStartTime() < this.getEndTime();
    }

    @Override
    public String toString() {
        return pattern + " " + String.format("%04d", startTime);
    }
}
