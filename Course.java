public class Course {
    private String name; //Name of class
    private int id; //Course Code (371 for CS371)
    private int season; //Term offered 0-both, 1-Fall, 2-Spring
    private int credits; //Number of credits


    public Course(String name,int id,int season,int credits){
        this.name=name;
        this.id=id;
        this.season=season;
        this.credits=credits;
    }

    public String getName() {
        return name;
    }

}
