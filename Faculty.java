import java.util.*;

public class Faculty {
    private String name; // Name of faculty
    private int classes; // Number of classes they must teach
    private int timeStartPref; // Prefer not to start before (military time, e.g., 1430=2:30 pm)
    private int timeEndPref; // Prefer not to end after (military time, e.g., 1430=2:30 pm)
    private Map<String, Integer> coursePreferences; // Preferences on how much they want to teach each course (1-5)

    private List<TimeSlot> assignedSlots = new ArrayList<>();


    public Faculty(String name, int classes, int timeStartPref, int timeEndPref) {
        this.name = name;
        this.classes = classes;
        this.timeStartPref = timeStartPref;
        this.timeEndPref = timeEndPref;
        this.coursePreferences = new HashMap<>();
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setClasses(int classes) {
        this.classes = classes;
    }

    public void setTimeStartPref(int timeStartPref) {
        this.timeStartPref = timeStartPref;
    }

    public void setTimeEndPref(int timeEndPref) {
        this.timeEndPref = timeEndPref;
    }


    public String getName() {
        return name;
    }

    public int getClasses() {
        return classes;
    }

    public int getTimeStartPref() {
        return timeStartPref;
    }

    public int getTimeEndPref() {
        return timeEndPref;
    }

    public Map<String, Integer> getCoursePreferences() {
        return coursePreferences;
    }

    public void setCoursePreference(String courseName, int preference) {
        coursePreferences.put(courseName, preference);
    }


    public boolean isAvailable(TimeSlot slot) {
        if (slot.getStartTime() < this.timeStartPref || slot.getEndTime() > this.timeEndPref) {
            return false;
        }
        for (TimeSlot t : assignedSlots) {
            if (t.overlapsWith(slot)) return false;
        }
        return true;
    }


    public void assignSlot(TimeSlot slot) {
        assignedSlots.add(slot);
    }


    public String toString() {
        return "Faculty{" +
                "name='" + name + '\'' +
                ", classes=" + classes +
                ", timeStartPref=" + timeStartPref +
                ", timeEndPref=" + timeEndPref +
                ", coursePreferences=" + coursePreferences +
                '}';
    }
}
