import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, Course> courseMap = CourseJsonLoader.loadCourses("courses_data.json");
        Map<String, Faculty> facultyMap = FacultyJsonLoader.loadFaculty("faculty_data.json", courseMap);





        int i = 0;
        while (i == 0) {
            System.out.println("Would you like to add a course? (y/n)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("n")) {
                i = 1;
            } else {
                System.out.println("Course name:");
                String name = scanner.nextLine();

                System.out.println("ID (Course code without the 'CS' enter 371 for CS371):");
                int id = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Season (Enter 1 for Fall only, 2 for Spring only, or 0 for both):");
                int season = scanner.nextInt();

                System.out.println("Number of credits:");
                int credits = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Minimum required number of sections:");
                int minSections = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Maximum number of sections:");
                int maxSections = scanner.nextInt();
                scanner.nextLine();

                Course course = new Course(name,id, season, credits,minSections,maxSections);
                courseMap.put(name, course);
                CourseJsonWriter.addCourse(course);
            }
        }

        int j = 0;
        while (j == 0) {
            System.out.println("Would you like to add a faculty member? (y/n)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("n")) {
                j = 1;
            } else {
                System.out.println("Faculty name:");
                String fname = scanner.nextLine();

                System.out.println("Number of classes they must teach:");
                int classes = scanner.nextInt();
                scanner.nextLine();

                System.out.println("Time they would like to not start before (military time 4 digit ex: 1430= 2:30 pm):");
                int timeStartPref = scanner.nextInt();

                System.out.println("Time they would like to not end after (military time 4 digit ex: 1430= 2:30 pm):");
                int timeEndPref = scanner.nextInt();
                scanner.nextLine();

                Faculty faculty = new Faculty(fname,classes, timeStartPref, timeEndPref);
                facultyMap.put(fname, faculty);

                System.out.println("Enter preference for each course (1-Can't teach it to 5- would love to teach it:");
                for (String courseName : courseMap.keySet()) {
                    System.out.print("Preference for \"" + courseName + "\": ");
                    int preference = scanner.nextInt();
                    scanner.nextLine();
                    faculty.setCoursePreference(courseName, preference);
                }
                FacultyJsonWriter.addFaculty(faculty);
            }
        }

        List<Course> courseList = new ArrayList<>(courseMap.values());
        List<Faculty> facultyList = new ArrayList<>(facultyMap.values());

        System.out.println("Which semester are you scheduling for? (1 = Fall, 2 = Spring)");
        int targetSemester = scanner.nextInt();
        scanner.nextLine();

        List<Course> filteredCourses = new ArrayList<>();
        for (Course c : courseList) {
            if (c.getSeason() == 0 || c.getSeason() == targetSemester) {
                filteredCourses.add(c);
            }
        }

        List<CourseSection> sectionsToAssign = new ArrayList<>();
        for (Course c : filteredCourses) {
            int goodFacultyCount = 0;
            for (Faculty f : facultyList) {
                int pref = f.getCoursePreferences().getOrDefault(c.getName(), 1);
                if (pref >= 4) goodFacultyCount++;
            }
            int totalSections = c.getMinSections() + Math.min(goodFacultyCount, c.getMaxSections() - c.getMinSections());
            for (int s = 1; s <= totalSections; s++) {
                sectionsToAssign.add(new CourseSection(c, s));
            }
        }

        List<TimeSlot> timeSlots = generateAllTimeSlots();
        TimeAwareAssignmentSolver timeSolver = new TimeAwareAssignmentSolver(facultyList, sectionsToAssign, timeSlots);
        Map<String, TimeAwareAssignmentSolver.Assignment> assignments = timeSolver.solve();

        System.out.println("\nCourse Assignments:");
        for (CourseSection section : sectionsToAssign) {
            TimeAwareAssignmentSolver.Assignment result = assignments.get(section.getLabel());
            String output = (result == null) ? "Unassigned" : result.toString();
            System.out.println(section.getLabel() + " → " + output);
        }
    }

    public static List<TimeSlot> generateAllTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();

        // Generate MWF slots: every hour from 8:00 AM to 5:00 PM
        for (int time = 800; time <= 1700; time += 100) {
            slots.add(new TimeSlot(TimeSlot.DayPattern.MWF, time));
        }

        // Generate TTh slots: special weird start times
        int[] tthStartTimes = {800, 925, 1050, 1215, 1340, 1505, 1630};
        for (int time : tthStartTimes) {
            slots.add(new TimeSlot(TimeSlot.DayPattern.TTH, time));
        }

        return slots;
    }


    public static int addMinutes(int militaryTime, int minutes) {
        int hours = militaryTime / 100;
        int mins = militaryTime % 100;

        mins += minutes;
        while (mins >= 60) {
            hours += 1;
            mins -= 60;
        }
        return hours * 100 + mins;
    }

}
