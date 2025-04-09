import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @Test
    //Tests that courses are properly made
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
    //Tests that faculty are properly set up
    public void testFacultySetup() {
        Faculty f = new Faculty("Alice", 2, 900, 1500);
        assertEquals("Alice", f.getName());
        assertEquals(2, f.getClasses());
        assertEquals(900, f.getTimeStartPref());
        assertEquals(1500, f.getTimeEndPref());
    }

    @Test
    //Tests that setting preferences works
    public void testCoursePreference() {
        Faculty f = new Faculty("Bob", 3, 800, 1700);
        f.setCoursePreference("Machine Learning", 5);
        assertEquals(5, f.getCoursePreferences().get("Machine Learning"));
    }

    @Test
    //Test to see if scheduling works
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
    //Tests if Faculty will be assigned to a class they rated 1
    public void testAvoidUnpreferredCourses() {
        Course c = new Course("Ethics", 300, 1, 3, 1, 1);
        CourseSection section = new CourseSection(c, 1);
        Faculty f = new Faculty("ReluctantProf", 1, 800, 1700);
        f.setCoursePreference("Ethics", 1); // 1 = can't teach it

        List<Faculty> facultyList = List.of(f);
        List<CourseSection> sectionList = List.of(section);
        List<TimeSlot> slots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sectionList, slots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        assertNull(result.get(section.getLabel()), "Faculty with 1-rating should not be assigned");
    }

    @Test
    //Tests to make sure faculty doesn't have overlapping classes
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
    //Tests to make sure max sections are not exceeded
    public void testDoNotExceedMaxSections() {
        Course c = new Course("Databases", 205, 1, 3, 1, 2);
        Faculty f1 = new Faculty("ProfA", 1, 800, 1700);
        Faculty f2 = new Faculty("ProfB", 1, 800, 1700);
        Faculty f3 = new Faculty("ProfC", 1, 800, 1700);

        for (Faculty f : List.of(f1, f2, f3)) {
            f.setCoursePreference("Databases", 5);
        }

        List<Course> courseList = List.of(c);
        List<CourseSection> sectionList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            sectionList.add(new CourseSection(c, i));
        }

        List<Faculty> facultyList = List.of(f1, f2, f3);
        List<TimeSlot> slots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sectionList, slots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        long assignedCount = result.keySet().stream()
                .filter(label -> label.startsWith("Databases"))
                .count();

        assertTrue(assignedCount <= 2, "Should not exceed max sections for a course");
    }

    @Test
    //Test to make sure minimum sections are followed
    public void testMinimumSectionsAssigned() {
        Course c = new Course("Security", 400, 1, 3, 2, 3); // Min = 2
        Faculty f1 = new Faculty("F1", 1, 800, 1700);
        Faculty f2 = new Faculty("F2", 1, 800, 1700);

        for (Faculty f : List.of(f1, f2)) {
            f.setCoursePreference("Security", 5);
        }

        List<Faculty> facultyList = List.of(f1, f2);
        List<CourseSection> sections = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            sections.add(new CourseSection(c, i));
        }

        List<TimeSlot> slots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sections, slots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        long assigned = result.keySet().stream()
                .filter(label -> label.contains("Security"))
                .count();

        assertTrue(assigned >= 2, "Minimum number of course sections should be assigned");
    }

    @Test
    //Test if faculty teach at least number of required classes
    public void testFacultyTeachesRequiredNumberOfClasses() {
        Course c1 = new Course("Databases", 101, 1, 3, 1, 1);
        Course c2 = new Course("Networking", 102, 1, 3, 1, 1);
        Course c3 = new Course("Security", 103, 1, 3, 1, 1);

        Faculty f = new Faculty("ReliableProf", 3, 800, 1700); // Wants 3 classes
        f.setCoursePreference("Databases", 5);
        f.setCoursePreference("Networking", 5);
        f.setCoursePreference("Security", 5);

        List<CourseSection> sections = List.of(
                new CourseSection(c1, 1),
                new CourseSection(c2, 1),
                new CourseSection(c3, 1)
        );

        List<Faculty> faculty = List.of(f);
        List<TimeSlot> slots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(faculty, sections, slots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        long assigned = result.values().stream()
                .filter(a -> a.getFaculty().getName().equals("ReliableProf"))
                .count();

        assertEquals(3, assigned, "Faculty should be assigned all classes they are required to teach");
    }

    @Test
    //Test if schedule respects time preferences
    public void testAssignedTimeSlotsWithinPreferences() {
        Course c1 = new Course("AI", 401, 1, 3, 1, 1);
        CourseSection section = new CourseSection(c1, 1);

        Faculty f = new Faculty("MorningProf", 1, 900, 1300); // Prefers 9am–1pm
        f.setCoursePreference("AI", 5);

        List<Faculty> facultyList = List.of(f);
        List<CourseSection> sectionList = List.of(section);
        List<TimeSlot> timeSlots = Main.generateAllTimeSlots();

        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sectionList, timeSlots);
        Map<String, TimeAwareAssignmentSolver.Assignment> result = solver.solve();

        TimeAwareAssignmentSolver.Assignment a = result.get(section.getLabel());

        assertNotNull(a, "Faculty should be assigned to the section");
        int time = a.getTimeSlot().getStartTime();
        assertTrue(time >= f.getTimeStartPref() && time <= f.getTimeEndPref(),
                "Assigned time must be within faculty's preferred window");
    }

    @Test
    public void testFacultyNotAssignedOutsidePreferences() {
        // TODO: Implement FacultyNotAssignedOutsidePreferences
        // This test ensures that faculty are only assigned classes within their preferred time window.
        fail("Not yet implemented: testFacultyNotAssignedOutsidePreferences");
    }

    @Test
    public void testManualFacultyEditUpdatesCorrectly() {
        // TODO: Implement ManualFacultyEditUpdatesCorrectly
        // This test ensures that edits to a faculty member’s preferences and availability are reflected.
        fail("Not yet implemented: testManualFacultyEditUpdatesCorrectly");
    }

    @Test
    public void testRemoveFacultyReflectsInAssignment() {
        // TODO: Implement RemoveFacultyReflectsInAssignment
        // This test ensures that a removed faculty member is not assigned any classes.
        fail("Not yet implemented: testRemoveFacultyReflectsInAssignment");
    }

    @Test
    public void testExportFormatStructure() {
        // TODO: Implement ExportFormatStructure
        // This test ensures that exported data is correctly structured and readable.
        fail("Not yet implemented: testExportFormatStructure");
    }

    @Test
    public void testMultipleSchedulesGeneration() {
        // TODO: Implement MultipleSchedulesGeneration
        // This test checks that a new schedule can be regenerated from the same data.
        fail("Not yet implemented: testMultipleSchedulesGeneration");
    }

    @Test
    public void testFacultyBalanceAcrossAssignments() {
        // TODO: Implement FacultyBalanceAcrossAssignments
        // This test ensures that no faculty is assigned more classes than they are set to teach.
        fail("Not yet implemented: testFacultyBalanceAcrossAssignments");
    }

    @Test
    public void testNoDuplicateAssignments() {
        // TODO: Implement NoDuplicateAssignments
        // This test ensures each course section is assigned only once.
        fail("Not yet implemented: testNoDuplicateAssignments");
    }

    @Test
    public void testCoursesRespectMinMaxSections() {
        // TODO: Implement CoursesRespectMinMaxSections
        // This test ensures each course respects its min/max section constraints.
        fail("Not yet implemented: testCoursesRespectMinMaxSections");
    }

    @Test
    public void testGlobalPreferencesAreApplied() {
        // TODO: Implement GlobalPreferencesAreApplied
        // This test checks that general scheduling preferences are followed.
        fail("Not yet implemented: testGlobalPreferencesAreApplied");
    }

    @Test
    public void testScheduleConflictHighlighting() {
        // TODO: Implement ScheduleConflictHighlighting
        // This test should simulate a conflict and check that it is flagged.
        fail("Not yet implemented: testScheduleConflictHighlighting");
    }

}
