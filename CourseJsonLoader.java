import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseJsonLoader {

    public static Map<String, Course> loadCourses(String filePath) {
        Map<String, Course> courseMap = new HashMap<>();

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonCourse>>() {}.getType();
            List<JsonCourse> courseList = gson.fromJson(reader, listType);

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
        String name;
        int id;
        int season;
        int credits;
        int minSections;
        int maxSections;
    }
}
