import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class FacultyJsonWriter {

    private static final String FILE_PATH = "faculty_data.json";

    public static void addFaculty(Faculty faculty) {
        List<JsonFaculty> facultyList = loadFacultyList();
        facultyList.add(convertToJson(faculty));
        saveFacultyList(facultyList);
    }

    public static void updateFaculty(Faculty updated) {
        List<JsonFaculty> facultyList = loadFacultyList();

        for (int i = 0; i < facultyList.size(); i++) {
            if (facultyList.get(i).name.equalsIgnoreCase(updated.getName())) {
                facultyList.set(i, convertToJson(updated));
                break;
            }
        }

        saveFacultyList(facultyList);
    }

    public static void removeFaculty(String name) {
        List<JsonFaculty> facultyList = loadFacultyList();
        facultyList.removeIf(f -> f.name.equalsIgnoreCase(name));
        saveFacultyList(facultyList);
    }

    private static List<JsonFaculty> loadFacultyList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<JsonFaculty>>() {}.getType();
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private static void saveFacultyList(List<JsonFaculty> facultyList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(facultyList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonFaculty convertToJson(Faculty f) {
        JsonFaculty jf = new JsonFaculty();
        jf.name = f.getName();
        jf.numClasses = f.getClasses();
        jf.timeStart = f.getTimeStartPref();
        jf.timeEnd = f.getTimeEndPref();
        jf.preferences = f.getCoursePreferences();
        return jf;
    }

    static class JsonFaculty {
        String name;
        int numClasses;
        int timeStart;
        int timeEnd;
        Map<String, Integer> preferences;
    }
}
