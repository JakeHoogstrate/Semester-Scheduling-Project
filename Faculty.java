import java.util.*;

public class Faculty {
    private String name; //Name of faculty
    private int classes; //Number of classes they must teach
    private int timeStartPref; //Prefer not to start before (military time 4 digit ex: 1430= 2:30 pm)
    private int timeEndPref; //Prefer not to end after (military time 4 digit ex: 1430= 2:30 pm)
    Map<String, Integer> coursePreferences; //preferences on how much they want to teach each course (1-5)

    public Faculty(String name,int classes,int timeStartPref,int timeEndPref){
        this.name=name;
        this.classes=classes;
        this.timeStartPref=timeStartPref;
        this.timeEndPref=timeEndPref;
        this.coursePreferences = new HashMap<>();

    }
    public void setCoursePreference(String courseName, int preference) {
        coursePreferences.put(courseName, preference);
    }

    public int getClasses() {
        return classes;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getCoursePreferences() {
        return coursePreferences;
    }

    public int getTimeStartPref() {
        return timeStartPref;
    }

    public int getTimeEndPref(){
        return timeEndPref;
    }


    private List<TimeSlot> assignedSlots = new ArrayList<>();

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

}
