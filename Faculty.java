public class Faculty {
    private String name; //Name of faculty
    private int classes; //Number of classes they must teach
    private int timeStartPref; //Prefer not to start before (military time 4 digit ex: 1430= 2:30 pm)
    private int timeEndPref; //Prefer not to end after (military time 4 digit ex: 1430= 2:30 pm)

    public Faculty(String name,int classes,int timeStartPref,int timeEndPref){
        this.name=name;
        this.classes=classes;
        this.timeStartPref=timeStartPref;
        this.timeEndPref=timeEndPref;

    }

}
