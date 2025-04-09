import java.util.*;

public class TimeAwareAssignmentSolver {
    private List<Faculty> facultyList;
    private List<CourseSection> sectionList;
    private List<TimeSlot> allTimeSlots;
    private Map<String, Assignment> finalAssignments = new HashMap<>();

    public TimeAwareAssignmentSolver(List<Faculty> facultyList, List<CourseSection> sectionList, List<TimeSlot> timeSlots) {
        this.facultyList = facultyList;
        this.sectionList = sectionList;
        this.allTimeSlots = timeSlots;
    }

    public Map<String, Assignment> solve() {
        List<AssignmentOption> options = new ArrayList<>();

        for (int fIdx = 0; fIdx < facultyList.size(); fIdx++) {
            Faculty faculty = facultyList.get(fIdx);
            for (int sIdx = 0; sIdx < sectionList.size(); sIdx++) {
                CourseSection section = sectionList.get(sIdx);
                int pref = faculty.getCoursePreferences().getOrDefault(section.getCourse().getName(), 1);
                if (pref == 1) continue; // Skip worst-case preferences

                for (TimeSlot slot : allTimeSlots) {
                    if (faculty.isAvailable(slot)) {
                        options.add(new AssignmentOption(fIdx, sIdx, slot, 5 - pref));
                    }
                }
            }
        }

        options.sort(Comparator.comparingInt(o -> o.cost));

        Set<Integer> assignedSections = new HashSet<>();
        int[] facultyAssignedCount = new int[facultyList.size()];

        for (AssignmentOption opt : options) {
            if (assignedSections.contains(opt.sectionIdx)) continue;
            Faculty faculty = facultyList.get(opt.facultyIdx);
            if (facultyAssignedCount[opt.facultyIdx] >= faculty.getClasses()) continue;

            CourseSection section = sectionList.get(opt.sectionIdx);
            if (faculty.isAvailable(opt.timeSlot)) {
                faculty.assignSlot(opt.timeSlot);
                facultyAssignedCount[opt.facultyIdx]++;
                assignedSections.add(opt.sectionIdx);
                finalAssignments.put(section.getLabel(), new Assignment(faculty.getName(), opt.timeSlot));
            }
        }

        return finalAssignments;
    }

    public static class Assignment {
        private String facultyName;
        private TimeSlot timeSlot;

        public Assignment(String facultyName, TimeSlot timeSlot) {
            this.facultyName = facultyName;
            this.timeSlot = timeSlot;
        }

        public String toString() {
            return facultyName + " @ " + timeSlot.toString();
        }
    }

    private static class AssignmentOption {
        int facultyIdx;
        int sectionIdx;
        TimeSlot timeSlot;
        int cost;

        AssignmentOption(int facultyIdx, int sectionIdx, TimeSlot timeSlot, int cost) {
            this.facultyIdx = facultyIdx;
            this.sectionIdx = sectionIdx;
            this.timeSlot = timeSlot;
            this.cost = cost;
        }
    }
}
