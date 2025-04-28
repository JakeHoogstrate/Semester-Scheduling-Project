import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.scene.Node;
import com.google.gson.Gson;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenerateScheduleScreen {

    private static final Map<String, String> courseColors = new HashMap<>();
    private static final List<String> pastelPalette = Arrays.asList(
            "#FFB3BA", "#FFDFBA", "#FFFFBA", "#BAFFC9", "#BAE1FF",
            "#e6ccff", "#ccffe6", "#ffe6cc", "#e6f0ff", "#f0e6ff"
    );

    private static Map<String, String> savedSchedule = new LinkedHashMap<>();

    public static void show(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: linear-gradient(to bottom, #ffffff, #e0f7fa);");

        Label title = new Label("Faculty Course Schedule");
        title.setFont(new Font("Arial Rounded MT Bold", 28));

        ComboBox<String> semesterChoice = new ComboBox<>();
        semesterChoice.getItems().addAll("Fall", "Spring");
        semesterChoice.setValue("Fall");

        Button generateButton = new Button("Generate Schedule");
        Button saveButton = new Button("💾 Save Schedule");
        Button backButton = new Button("Back to Main Menu");

        Pane grid = new Pane();
        grid.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(800);

        generateButton.setOnAction(e -> {
            grid.getChildren().clear();
            courseColors.clear();
            savedSchedule.clear();
            buildScheduleGrid(grid, semesterChoice.getValue());
        });

        saveButton.setOnAction(e -> {
            saveScheduleToJson();
        });

        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });

        root.getChildren().addAll(title, semesterChoice, generateButton, saveButton, scrollPane, backButton);

        Scene scene = new Scene(root, 1300, 900);
        primaryStage.setScene(scene);
    }

    private static void buildScheduleGrid(Pane pane, String semesterSelected) {
        Map<String, Course> courseMap = CourseJsonLoader.loadCourses("courses_data.json");
        Map<String, Faculty> facultyMap = FacultyJsonLoader.loadFaculty("faculty_data.json", courseMap);

        List<Course> courseList = new ArrayList<>(courseMap.values());
        List<Faculty> facultyList = new ArrayList<>(facultyMap.values());

        int targetSemester = semesterSelected.equals("Fall") ? 1 : 2;

        List<Course> filteredCourses = new ArrayList<>();
        for (Course c : courseList) {
            if (c.getSeason() == 0 || c.getSeason() == targetSemester) {
                filteredCourses.add(c);
            }
        }

        List<CourseSection> sectionsToAssign = new ArrayList<>();
        for (Course c : filteredCourses) {
            int goodFacultyCount = 0;
            for (Faculty f : facultyList) {
                int pref = f.getCoursePreferences().getOrDefault(c.getName(), 1);
                if (pref >= 4) goodFacultyCount++;
            }
            int totalSections = c.getMinSections() + Math.min(goodFacultyCount, c.getMaxSections() - c.getMinSections());
            for (int s = 1; s <= totalSections; s++) {
                sectionsToAssign.add(new CourseSection(c, s));
            }
        }

        List<TimeSlot> timeSlots = Main.generateAllTimeSlots();
        TimeAwareAssignmentSolver solver = new TimeAwareAssignmentSolver(facultyList, sectionsToAssign, timeSlots);
        Map<String, TimeAwareAssignmentSolver.Assignment> assignments = solver.solve();

        int startMinutes = 480; // 8:00 AM
        int endMinutes = 1080;  // 6:00 PM
        int pixelsPerMinute = 2;
        double paneWidth = 1200;
        double columnWidth = paneWidth / 5;
        double paneHeight = (endMinutes - startMinutes) * pixelsPerMinute;

        pane.setPrefWidth(paneWidth);
        pane.setPrefHeight(paneHeight);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        for (int t = startMinutes; t <= endMinutes; t += 60) {
            Label timeLabel = new Label(formatTimeFromMinutes(t));
            timeLabel.setLayoutX(5);
            timeLabel.setLayoutY((t - startMinutes) * pixelsPerMinute - 8);
            timeLabel.setFont(new Font(10));
            pane.getChildren().add(timeLabel);
        }

        for (CourseSection section : sectionsToAssign) {
            TimeAwareAssignmentSolver.Assignment assignment = assignments.get(section.getLabel());
            if (assignment != null) {
                TimeSlot slot = assignment.getTimeSlot();
                Faculty faculty = assignment.getFaculty();

                List<String> daysAssigned = getDaysFromSlot(slot.getPattern());
                int slotStart = militaryToMinutes(slot.getStartTime());
                int slotEnd = militaryToMinutes(slot.getEndTime());

                for (String day : daysAssigned) {
                    int colIndex = Arrays.asList(days).indexOf(day);

                    StackPane block = createClassBlock(section, faculty, slot);


                    block.setOnMouseClicked(event -> {
                        EditBlockPopup.show(
                                block,
                                "CS" + section.getCourse().getId() + " (Sec " + section.getSectionNumber() + ")",
                                faculty.getName(),
                                militaryToMinutes(slot.getStartTime()),
                                militaryToMinutes(slot.getEndTime())
                        );

                    });

                    block.setLayoutX(colIndex * columnWidth + 50);
                    block.setLayoutY((slotStart - startMinutes) * pixelsPerMinute);
                    block.setPrefWidth(columnWidth - 60);
                    block.setPrefHeight((slotEnd - slotStart) * pixelsPerMinute);

                    pane.getChildren().add(block);

                    String key = day + " " + formatTimeFromMinutes(slotStart);
                    String value = "CS" + section.getCourse().getId() + " (" + faculty.getName() + "), " +
                            formatTimeFromMinutes(slotStart) + "-" + formatTimeFromMinutes(slotEnd) +
                            ", Credits: " + section.getCourse().getCredits();
                    savedSchedule.put(key, value);
                }
            }
        }
    }

    private static StackPane createClassBlock(CourseSection section, Faculty faculty, TimeSlot slot) {
        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: " + getColorForCourse(section.getCourse().getName()) + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #333333;");
        pane.setEffect(new DropShadow(5, Color.GRAY));

        StringBuilder text = new StringBuilder();
        text.append("CS").append(section.getCourse().getId());
        text.append(" (Sec ").append(section.getSectionNumber()).append(")").append("\n");
        text.append(faculty.getName()).append("\n");
        text.append(formatTimeFromMinutes(militaryToMinutes(slot.getStartTime())))
                .append("-")
                .append(formatTimeFromMinutes(militaryToMinutes(slot.getEndTime())))
                .append("\n");
        text.append("Credits: ").append(section.getCourse().getCredits());

        Label label = new Label(text.toString());
        label.setFont(new Font("Verdana", 10));
        label.setWrapText(true);

        pane.getChildren().add(label);

        return pane;
    }



    private static void saveScheduleToJson() {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter("saved_schedule.json")) {
            gson.toJson(savedSchedule, writer);
            System.out.println("✅ Schedule successfully downloaded to saved_schedule.json!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getColorForCourse(String courseName) {
        if (!courseColors.containsKey(courseName)) {
            String color = pastelPalette.get(courseColors.size() % pastelPalette.size());
            courseColors.put(courseName, color);
        }
        return courseColors.get(courseName);
    }

    private static List<String> getDaysFromSlot(TimeSlot.DayPattern pattern) {
        switch (pattern) {
            case MWF: return Arrays.asList("Monday", "Wednesday", "Friday");
            case TTH: return Arrays.asList("Tuesday", "Thursday");
            default: return Collections.emptyList();
        }
    }

    private static int militaryToMinutes(int military) {
        int hour = military / 100;
        int minutes = military % 100;
        return hour * 60 + minutes;
    }

    private static String formatTimeFromMinutes(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        String ampm = (hour >= 12) ? "PM" : "AM";
        if (hour > 12) hour -= 12;
        if (hour == 0) hour = 12;
        return String.format("%d:%02d%s", hour, min, ampm);
    }


}
