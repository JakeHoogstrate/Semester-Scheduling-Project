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
                        int baseCost = 5 - pref;
                        options.add(new AssignmentOption(fIdx, sIdx, slot, baseCost));
                    }
                }
            }
        }

        // Shuffle options slightly to prevent tie bias
        Collections.shuffle(options);

        Set<Integer> assignedSections = new HashSet<>();
        int[] facultyAssignedCount = new int[facultyList.size()];

        Map<Integer, Set<TimeSlot.DayPattern>> facultyDayPatterns = new HashMap<>();
        int mwfAssigned = 0;
        int tthAssigned = 0;

        for (AssignmentOption opt : options) {
            if (assignedSections.contains(opt.sectionIdx)) continue;
            Faculty faculty = facultyList.get(opt.facultyIdx);
            if (facultyAssignedCount[opt.facultyIdx] >= faculty.getClasses()) continue;

            CourseSection section = sectionList.get(opt.sectionIdx);
            if (!faculty.isAvailable(opt.timeSlot)) continue;

            TimeSlot.DayPattern pattern = opt.timeSlot.getPattern();

            // Check global balance
            boolean mwfHeavier = mwfAssigned > tthAssigned;
            if (pattern == TimeSlot.DayPattern.MWF && mwfHeavier && (mwfAssigned - tthAssigned) > 2) {
                continue; // Skip more MWF if way heavier
            }
            if (pattern == TimeSlot.DayPattern.TTH && !mwfHeavier && (tthAssigned - mwfAssigned) > 2) {
                continue; // Skip more TTH if way heavier
            }

            // Check faculty preference for day grouping
            Set<TimeSlot.DayPattern> facultyDays = facultyDayPatterns.getOrDefault(opt.facultyIdx, new HashSet<>());

            if (!facultyDays.isEmpty() && !facultyDays.contains(pattern)) {
                // Light preference: 70% chance to skip assigning a "new" day pattern
                if (Math.random() < 0.7) {
                    continue;
                }
            }

            // Assign it!
            faculty.assignSlot(opt.timeSlot);
            facultyAssignedCount[opt.facultyIdx]++;
            assignedSections.add(opt.sectionIdx);
            finalAssignments.put(section.getLabel(), new Assignment(faculty, opt.timeSlot));

            // Update tracking
            if (pattern == TimeSlot.DayPattern.MWF) mwfAssigned++;
            if (pattern == TimeSlot.DayPattern.TTH) tthAssigned++;

            facultyDays.add(pattern);
            facultyDayPatterns.put(opt.facultyIdx, facultyDays);
        }

        return finalAssignments;
    }


    // ✅ Updated Assignment class to hold Faculty object
    public static class Assignment {
        private Faculty faculty;
        private TimeSlot timeSlot;

        public Assignment(Faculty faculty, TimeSlot timeSlot) {
            this.faculty = faculty;
            this.timeSlot = timeSlot;
        }

        public Faculty getFaculty() {
            return faculty;
        }

        public TimeSlot getTimeSlot() {
            return timeSlot;
        }

        public String toString() {
            return faculty.getName() + " @ " + timeSlot.toString();
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
