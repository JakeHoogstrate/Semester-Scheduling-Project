public class CourseSection {
    private Course course;
    private int sectionNumber;

    public CourseSection(Course course, int sectionNumber) {
        this.course = course;
        this.sectionNumber = sectionNumber;
    }

    public Course getCourse() {
        return course;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public String getLabel() {
        return course.getName() + " (Sec " + sectionNumber + ")";
    }
}
