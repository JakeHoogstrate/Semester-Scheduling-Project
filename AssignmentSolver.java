import java.util.*;

class AssignmentSolver {
    private List<Faculty> facultyList;
    private List<Course> courseList;
    private int[][] costMatrix;
    private Map<String, String> finalAssignments = new HashMap<>();

    public AssignmentSolver(List<Faculty> facultyList, List<Course> courseList) {
        this.facultyList = facultyList;
        this.courseList = courseList;
        buildCostMatrix();
    }

    private void buildCostMatrix() {
        int numFaculty = facultyList.size();
        int numCourses = courseList.size();
        costMatrix = new int[numFaculty][numCourses];

        for (int i = 0; i < numFaculty; i++) {
            Faculty f = facultyList.get(i);
            for (int j = 0; j < numCourses; j++) {
                Course c = courseList.get(j);
                int pref = f.getCoursePreferences().getOrDefault(c.getName(), 1);
                costMatrix[i][j] = pref == 1 ? 1000 : (5 - pref);
            }
        }
    }

    public Map<String, String> solve() {
        int numFaculty = facultyList.size();
        int numCourses = courseList.size();
        boolean[] courseAssigned = new boolean[numCourses];
        int[] facultyCourseCount = new int[numFaculty];


        List<Assignment> allOptions = new ArrayList<>();
        for (int i = 0; i < numFaculty; i++) {
            for (int j = 0; j < numCourses; j++) {
                allOptions.add(new Assignment(i, j, costMatrix[i][j]));
            }
        }

        allOptions.sort(Comparator.comparingInt(a -> a.cost));

        for (Assignment a : allOptions) {
            if (courseAssigned[a.courseIdx]) continue;
            Faculty f = facultyList.get(a.facultyIdx);
            if (facultyCourseCount[a.facultyIdx] < f.getClasses()) {
                courseAssigned[a.courseIdx] = true;
                facultyCourseCount[a.facultyIdx]++;
                finalAssignments.put(courseList.get(a.courseIdx).getName(), f.getName());
            }
        }

        return finalAssignments;
    }

    private static class Assignment {
        int facultyIdx;
        int courseIdx;
        int cost;

        public Assignment(int f, int c, int cost) {
            this.facultyIdx = f;
            this.courseIdx = c;
            this.cost = cost;
        }
    }
}
