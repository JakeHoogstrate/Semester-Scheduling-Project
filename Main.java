import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Course> courseMap = new HashMap<>();
        Map<String, Faculty> facultyMap = new HashMap<>();

        //Test Courses and Faculty
        Course cs101 = new Course("Intro to Programming", 101, 0, 3);
        Course cs201 = new Course("Data Structures", 201, 1, 4);
        Course cs301 = new Course("Algorithms", 301, 2, 4);
        courseMap.put(cs101.getName(), cs101);
        courseMap.put(cs201.getName(), cs201);
        courseMap.put(cs301.getName(), cs301);

        Faculty john = new Faculty("John", 2, 900, 1600);
        Faculty bob = new Faculty("Bob", 1, 1000, 1700);

        john.setCoursePreference("Intro to Programming", 5);
        john.setCoursePreference("Data Structures", 3);
        john.setCoursePreference("Algorithms", 2);

        bob.setCoursePreference("Intro to Programming", 2);
        bob.setCoursePreference("Data Structures", 4);
        bob.setCoursePreference("Algorithms", 1);

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

                Course course = new Course(name,id, season, credits);
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

        List<Faculty> facultyList = new ArrayList<>(facultyMap.values());
        List<Course> courseList = new ArrayList<>(courseMap.values());

        AssignmentSolver solver = new AssignmentSolver(facultyList, courseList);
        Map<String, String> assignments = solver.solve();


        System.out.println("\nCourse Assignments:");
        for (Course course : courseList) {
            String assignedTo = assignments.getOrDefault(course.getName(), "Unassigned");
            System.out.println("Course \"" + course.getName() + "\" → " + assignedTo);
        }




    }

    }
