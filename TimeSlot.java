public class TimeSlot {
    public enum DayPattern { MWF, TTH }

    private DayPattern pattern;
    private int startTime; // military time, e.g. 800, 925

    public TimeSlot(DayPattern pattern, int startTime) {
        this.pattern = pattern;
        this.startTime = startTime;
    }

    public DayPattern getPattern() {
        return pattern;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        if (pattern == DayPattern.MWF) {
            return startTime + 50;
        } else { // TTh
            return startTime + 75;
        }
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
