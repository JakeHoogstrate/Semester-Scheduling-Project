import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class CourseJsonWriter {

    private static final String FILE_PATH = "courses_data.json";


    public static void addCourse(Course course) {
        List<JsonCourse> courseList = loadCourseList();
        courseList.add(convertToJson(course));
        saveCourseList(courseList);
    }


    public static void updateCourse(Course updatedCourse) {
        List<JsonCourse> courseList = loadCourseList();


        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).name.equalsIgnoreCase(updatedCourse.getName())) {
                courseList.set(i, convertToJson(updatedCourse));
                break;
            }
        }

        saveCourseList(courseList);
    }


    public static void removeCourse(String name) {
        List<JsonCourse> courseList = loadCourseList();
        courseList.removeIf(c -> c.name.equalsIgnoreCase(name));
        saveCourseList(courseList);
    }


    private static List<JsonCourse> loadCourseList() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<JsonCourse>>() {}.getType();
        try (Reader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


    private static void saveCourseList(List<JsonCourse> courseList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(courseList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static JsonCourse convertToJson(Course course) {
        JsonCourse jsonCourse = new JsonCourse();
        jsonCourse.name = course.getName();
        jsonCourse.id = course.getId();
        jsonCourse.season = course.getSeason();
        jsonCourse.credits = course.getCredits();
        jsonCourse.minSections = course.getMinSections();
        jsonCourse.maxSections = course.getMaxSections();
        return jsonCourse;
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
