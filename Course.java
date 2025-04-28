public class Course {
    private String name; // Name of class
    private int id; // Course Code (371 for CS371)
    private int season; // Term offered 0-both, 1-Fall, 2-Spring
    private int credits; // Number of credits
    private int minSections; // Minimum required number of sections
    private int maxSections; // Maximum number of sections


    public Course(String name, int id, int season, int credits, int minSections, int maxSections) {
        this.name = name;
        this.id = id;
        this.season = season;
        this.credits = credits;
        this.minSections = minSections;
        this.maxSections = maxSections;
    }


    public String getName() {
        return name;
    }

    public int getSeason() {
        return season;
    }

    public int getMinSections() {
        return minSections;
    }

    public int getMaxSections() {
        return maxSections;
    }

    public int getId() {
        return id;
    }

    public int getCredits() {
        return credits;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setMinSections(int minSections) {
        this.minSections = minSections;
    }

    public void setMaxSections(int maxSections) {
        this.maxSections = maxSections;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
