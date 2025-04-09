import java.util.*;

public class AssignmentSolver {
    private List<Faculty> facultyList;
    private List<CourseSection> sectionList;
    private int[][] costMatrix;
    private Map<String, String> finalAssignments = new HashMap<>();

    public AssignmentSolver(List<Faculty> facultyList, List<CourseSection> sectionList) {
        this.facultyList = facultyList;
        this.sectionList = sectionList;
        buildCostMatrix();
    }

    private void buildCostMatrix() {
        int numFaculty = facultyList.size();
        int numSections = sectionList.size();
        costMatrix = new int[numFaculty][numSections];

        for (int i = 0; i < numFaculty; i++) {
            Faculty f = facultyList.get(i);
            for (int j = 0; j < numSections; j++) {
                String courseName = sectionList.get(j).getCourse().getName();
                int pref = f.getCoursePreferences().getOrDefault(courseName, 1);
                costMatrix[i][j] = pref == 1 ? 1000 : (5 - pref); // discourage assigning 1-rated
            }
        }
    }

    public Map<String, String> solve() {
        int numFaculty = facultyList.size();
        int numSections = sectionList.size();
        boolean[] sectionAssigned = new boolean[numSections];
        int[] facultyAssignedCounts = new int[numFaculty];

        List<Assignment> allOptions = new ArrayList<>();
        for (int i = 0; i < numFaculty; i++) {
            for (int j = 0; j < numSections; j++) {
                allOptions.add(new Assignment(i, j, costMatrix[i][j]));
            }
        }

        allOptions.sort(Comparator.comparingInt(a -> a.cost));

        for (Assignment a : allOptions) {
            if (sectionAssigned[a.sectionIdx]) continue;
            Faculty f = facultyList.get(a.facultyIdx);
            if (facultyAssignedCounts[a.facultyIdx] < f.getClasses()) {
                CourseSection section = sectionList.get(a.sectionIdx);
                sectionAssigned[a.sectionIdx] = true;
                facultyAssignedCounts[a.facultyIdx]++;
                finalAssignments.put(section.getLabel(), f.getName());
            }
        }

        return finalAssignments;
    }

    private static class Assignment {
        int facultyIdx;
        int sectionIdx;
        int cost;

        public Assignment(int f, int s, int cost) {
            this.facultyIdx = f;
            this.sectionIdx = s;
            this.cost = cost;
        }
    }
}
