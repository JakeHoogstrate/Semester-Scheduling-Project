import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class FacultyJsonLoader {

    public static Map<String, Faculty> loadFaculty(String filePath, Map<String, Course> courseMap) {
        Map<String, Faculty> facultyMap = new HashMap<>();

        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonFaculty>>() {}.getType();
            List<JsonFaculty> facultyList = gson.fromJson(reader, listType);

            for (JsonFaculty f : facultyList) {
                Faculty faculty = new Faculty(f.name, f.numClasses, f.timeStart, f.timeEnd);
                for (Map.Entry<String, Integer> entry : f.preferences.entrySet()) {
                    if (courseMap.containsKey(entry.getKey())) {
                        faculty.setCoursePreference(entry.getKey(), entry.getValue());
                    }
                }
                facultyMap.put(f.name, faculty);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return facultyMap;
    }

    private static class JsonFaculty {
        String name;
        int numClasses;
        int timeStart;
        int timeEnd;
        Map<String, Integer> preferences;
    }
}
