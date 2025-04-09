import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Course> courseMap = new HashMap<>();
        Map<String, Faculty> facultyMap = new HashMap<>();

        //Test Courses and Faculty
        Course cs101 = new Course("Intro to Programming", 101, 0, 3,1,2);
        Course cs201 = new Course("Data Structures", 201, 2, 4,2,3);
        Course cs301 = new Course("Algorithms", 301, 2, 4,1,3);
        Course cs302 = new Course("Software Engineering", 302, 2, 4,0,1);
        Course cs303 = new Course("Ethics", 303, 2, 4,0,1);
        Course cs304 = new Course("Software Project", 304, 2, 4,0,1);
        Course cs305 = new Course("Machine Learning", 305, 2, 4,0,1);
        courseMap.put(cs101.getName(), cs101);
        courseMap.put(cs201.getName(), cs201);
        courseMap.put(cs301.getName(), cs301);
        courseMap.put(cs302.getName(), cs302);
        courseMap.put(cs303.getName(), cs303);
        courseMap.put(cs304.getName(), cs304);
        courseMap.put(cs305.getName(), cs305);


        Faculty john = new Faculty("John", 5, 900, 1600);
        Faculty bob = new Faculty("Bob", 5, 1000, 1700);

        john.setCoursePreference("Intro to Programming", 5);
        john.setCoursePreference("Data Structures", 3);
        john.setCoursePreference("Algorithms", 2);
        john.setCoursePreference("Software Engineering", 2);
        john.setCoursePreference("Ethics", 3);
        john.setCoursePreference("Software Project", 1);
        john.setCoursePreference("Machine Learning", 5);

        bob.setCoursePreference("Intro to Programming", 2);
        bob.setCoursePreference("Data Structures", 4);
        bob.setCoursePreference("Algorithms", 1);
        bob.setCoursePreference("Software Engineering", 2);
        bob.setCoursePreference("Ethics", 2);
        bob.setCoursePreference("Software Project", 3);
        bob.setCoursePreference("Machine Learning", 1);

        facultyMap.put(john.getName(), john);
        facultyMap.put(bob.getName(), bob);


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

            }}
        }

        List<Course> courseList = new ArrayList<>(courseMap.values());
        List<Faculty> facultyList = new ArrayList<>(facultyMap.values());


        // Filter courses by semester
        System.out.println("Which semester are you scheduling for? (1 = Fall, 2 = Spring)");
        int targetSemester = scanner.nextInt();
        scanner.nextLine();

        List<Course> filteredCourses = new ArrayList<>();
        for (Course c : courseList) {
            if (c.getSeason() == 0 || c.getSeason() == targetSemester) {
                filteredCourses.add(c);
            }
        }

// Create course sections
        List<CourseSection> sectionsToAssign = new ArrayList<>();
        Map<String, Integer> sectionCounter = new HashMap<>();
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

// Solve
        AssignmentSolver solver = new AssignmentSolver(facultyList, sectionsToAssign);
        Map<String, String> assignments = solver.solve();

// Output
        System.out.println("\nCourse Assignments:");
        for (CourseSection section : sectionsToAssign) {
            String assignedTo = assignments.getOrDefault(section.getLabel(), "Unassigned");
            System.out.println(section.getLabel() + " → " + assignedTo);
        }





    }

    }
