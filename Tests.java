import org.junit.jupiter.api.Test;
import java.util.*;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    public void testCourseCreation() {
        Course c = new Course("AI", 123, 1, 3, 1, 2);
        assertEquals("AI", c.getName());
        assertEquals(123, c.getId());
        assertEquals(1, c.getSeason());
        assertEquals(3, c.getCredits());
        assertEquals(1, c.getMinSections());
        assertEquals(2, c.getMaxSections());
    }

    @Test
    public void testFacultySetup() {
        Faculty f = new Faculty("Alice", 2, 900, 1500);
        assertEquals("Alice", f.getName());
        assertEquals(2, f.getClasses());
        assertEquals(900, f.getTimeStartPref());
        assertEquals(1500, f.getTimeEndPref());
    }

    @Test
    public void testCoursePreference() {
        Faculty f = new Faculty("Bob", 3, 800, 1700);
        f.setCoursePreference("Machine Learning", 5);
        assertEquals(5, f.getCoursePreferences().get("Machine Learning"));
    }

    @Test
    public void testSolveReturnsAssignments() {
        Course c = new Course("AI", 123, 1, 3, 1, 1);
        CourseSection section = new CourseSection(c, 1);
        Faculty f = new Faculty("Test", 1, 800, 1700);
        f.setCoursePreference("AI", 5);

        List<Faculty> faculty = List.of(f);
        List<CourseSection> sections = List.of(section);
        List<TimeSlot> timeSlots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(faculty, sections, timeSlots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        assertNotNull(result);
        assertTrue(result.containsKey(section.getLabel()));
        assertEquals("Test", result.get(section.getLabel()).getFaculty().getName());
    }

    @Test
    public void testAvoidUnpreferredCourses() {
        Course c = new Course("Ethics", 300, 1, 3, 1, 1);
        CourseSection section = new CourseSection(c, 1);
        Faculty f = new Faculty("ReluctantProf", 1, 800, 1700);
        f.setCoursePreference("Ethics", 1);

        List<Faculty> facultyList = List.of(f);
        List<CourseSection> sectionList = List.of(section);
        List<TimeSlot> slots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sectionList, slots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        assertNull(result.get(section.getLabel()), "Faculty with 1-rating should not be assigned");
    }

    @Test
    public void testNoTimeConflicts() {
        Course c1 = new Course("Math", 101, 1, 3, 1, 1);
        Course c2 = new Course("Science", 102, 1, 3, 1, 1);
        CourseSection s1 = new CourseSection(c1, 1);
        CourseSection s2 = new CourseSection(c2, 1);

        Faculty f = new Faculty("OverlapProf", 2, 800, 1700);
        f.setCoursePreference("Math", 5);
        f.setCoursePreference("Science", 5);

        List<Faculty> facultyList = List.of(f);
        List<CourseSection> sections = List.of(s1, s2);
        List<TimeSlot> timeSlots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sections, timeSlots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        List<TimeSlot> assignedSlots = result.values().stream()
                .filter(a -> a.getFaculty().getName().equals("OverlapProf"))
                .map(TimeAwareAssignmentSolver.Assignment::getTimeSlot)
                .toList();

        Set<TimeSlot> uniqueSlots = new HashSet<>(assignedSlots);

        assertEquals(uniqueSlots.size(), assignedSlots.size(), "Faculty should not be assigned overlapping time slots");
    }

    @Test
    public void testFacultyAssignSlotDirectly() {
        Faculty f = new Faculty("TestProf", 1, 800, 1700);
        TimeSlot ts = new TimeSlot(TimeSlot.DayPattern.MWF, 900);
        assertTrue(f.isAvailable(ts));
        f.assignSlot(ts);
        assertFalse(f.isAvailable(ts), "Faculty should not be available after assigning slot manually");
    }

    @Test
    public void testGeneratedTimeSlotsNotEmpty() {
        List<TimeSlot> slots = Main.generateAllTimeSlots();
        assertFalse(slots.isEmpty(), "Generated time slots should not be empty");
    }

    @Test
    public void testOverlappingSlots() {
        TimeSlot slot1 = new TimeSlot(TimeSlot.DayPattern.MWF, 800);
        TimeSlot slot2 = new TimeSlot(TimeSlot.DayPattern.MWF, 830);
        TimeSlot slot3 = new TimeSlot(TimeSlot.DayPattern.MWF, 1000);

        assertTrue(slot1.overlapsWith(slot2), "Slots should overlap");
        assertFalse(slot1.overlapsWith(slot3), "Slots should not overlap");
    }

    @Test
    public void testCourseSectionLabel() {
        Course c = new Course("AI", 123, 1, 3, 1, 2);
        CourseSection section = new CourseSection(c, 1);
        assertEquals("AI (Sec 1)", section.getLabel(), "Label should match format");
    }

    @Test
    public void testFacultyAvailability() {
        Faculty f = new Faculty("TestProf", 1, 800, 1700);
        TimeSlot morning = new TimeSlot(TimeSlot.DayPattern.MWF, 900);
        TimeSlot evening = new TimeSlot(TimeSlot.DayPattern.MWF, 1800);

        assertTrue(f.isAvailable(morning));
        assertFalse(f.isAvailable(evening));
    }

    @Test
    public void testSaveScheduleCreatesFile() {
        GenerateScheduleScreen.getSavedSchedule().clear();
        GenerateScheduleScreen.getSavedSchedule().put("Monday 9:00AM", "CS101 (Prof A)");
        GenerateScheduleScreen.saveScheduleToJson();

        File saved = new File("saved_schedule.json");
        assertTrue(saved.exists(), "saved_schedule.json should exist");
    }

    @Test
    public void testColorAssignmentDifferentCourses() {
        String colorA = GenerateScheduleScreen.testGetColorForCourse("Math");
        String colorB = GenerateScheduleScreen.testGetColorForCourse("Physics");
        assertNotEquals(colorA, colorB, "Courses should have different colors");
    }


    @Test
    public void testEmptyCourseLoadReturnsEmptyMap() {
        Map<String, Course> loaded = CourseJsonLoader.loadCourses("nonexistent_courses.json");
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty(), "Should return empty map when file doesn't exist");
    }

    @Test
    public void testInvalidTimeSlotCreation() {
        TimeSlot weirdSlot = new TimeSlot(TimeSlot.DayPattern.TTH, 700);
        assertTrue(weirdSlot.getStartTime() < 800, "Slot starts too early");
    }

    @Test
    public void testSaveEmptyScheduleToJson() {
        GenerateScheduleScreen.getSavedSchedule().clear();
        GenerateScheduleScreen.saveScheduleToJson();

        File saved = new File("saved_schedule.json");
        assertTrue(saved.exists(), "saved_schedule.json should still be created even if empty");
    }
}
