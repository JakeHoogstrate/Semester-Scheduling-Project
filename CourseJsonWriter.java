import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class CourseJsonWriter {

    private static final String FILE_PATH = "courses_data.json";

    public static void addCourse(Course course) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<JsonCourse>>() {}.getType();
        List<JsonCourse> courseList = new ArrayList<>();

        // Load existing courses
        try (Reader reader = new FileReader(FILE_PATH)) {
            courseList = gson.fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            // File doesn't exist yet — that's fine
        } catch (IOException e) {
            e.printStackTrace();
        }


        JsonCourse jc = new JsonCourse();
        jc.name = course.getName();
        jc.id = course.getId();
        jc.season = course.getSeason();
        jc.credits = course.getCredits();
        jc.minSections = course.getMinSections();
        jc.maxSections = course.getMaxSections();

        courseList.add(jc);


        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(courseList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class JsonCourse {
        String name;
        int id;
        int season;
        int credits;
        int minSections;
        int maxSections;
    }
}
