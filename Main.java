import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Course> courseMap = new HashMap<>();
        Map<String, Faculty> facultyMap = new HashMap<>();


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

            }
        }
    }
}