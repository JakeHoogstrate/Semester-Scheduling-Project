import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseJsonLoader {


    private static Map<String, Course> courseMap = new HashMap<>();


    public static Map<String, Course> loadCourses(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonCourse>>() {}.getType();
            List<JsonCourse> courseList = gson.fromJson(reader, listType);

            courseMap.clear();

            for (JsonCourse c : courseList) {

                Course course = new Course(c.name, c.id, c.season, c.credits, c.minSections, c.maxSections);
                courseMap.put(c.name, course);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseMap;
    }


    private static class JsonCourse {
        String name;          // Course name
        int id;               // Course ID
        int season;           // Season (1 for Fall, 2 for Spring, 0 for both)
        int credits;          // Number of credits
        int minSections;      // Minimum sections
        int maxSections;      // Maximum sections
    }


    public static Map<String, Course> getCourseMap() {
        return courseMap;
    }
}
