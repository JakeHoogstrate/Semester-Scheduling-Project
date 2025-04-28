import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AddFacultyScreen {
    public static void show(Stage primaryStage) {

        CourseJsonLoader.loadCourses("courses_data.json");


        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #fbe9e7;");

        TextField nameField = new TextField();
        nameField.setPromptText("Faculty Name");

        TextField classesField = new TextField();
        classesField.setPromptText("Number of Classes");

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Preferred Start Time (e.g., 0800)");

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("Preferred End Time (e.g., 1700)");

        // Course Preferences
        Label prefLabel = new Label("Course Preferences (1=Can't Teach, 5=Love to Teach):");
        VBox coursePrefList = new VBox(10);
        coursePrefList.setStyle("-fx-border-color: black; -fx-padding: 10;");

        Map<String, ComboBox<Integer>> coursePreferenceSelectors = new HashMap<>();

        for (String courseName : CourseJsonLoader.getCourseMap().keySet()) {
            Label courseLabel = new Label(courseName);
            ComboBox<Integer> preferenceBox = new ComboBox<>();
            preferenceBox.getItems().addAll(1, 2, 3, 4, 5);
            preferenceBox.setValue(3);

            HBox courseRow = new HBox(10, courseLabel, preferenceBox);
            coursePrefList.getChildren().add(courseRow);

            coursePreferenceSelectors.put(courseName, preferenceBox);
        }

        ScrollPane scrollPane = new ScrollPane(coursePrefList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);

        Button saveButton = new Button("Save Faculty");
        Button backButton = new Button("Back");

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int numClasses = Integer.parseInt(classesField.getText());
                int startPref = Integer.parseInt(startTimeField.getText());
                int endPref = Integer.parseInt(endTimeField.getText());

                Faculty faculty = new Faculty(name, numClasses, startPref, endPref);

                for (Map.Entry<String, ComboBox<Integer>> entry : coursePreferenceSelectors.entrySet()) {
                    String courseName = entry.getKey();
                    int preference = entry.getValue().getValue();
                    faculty.setCoursePreference(courseName, preference);
                }

                FacultyJsonLoader.getFacultyMap().put(name, faculty);
                FacultyJsonWriter.addFaculty(faculty);

                System.out.println("Saved faculty: " + name);

                MainApp mainApp = new MainApp();
                mainApp.start(primaryStage);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });

        root.getChildren().addAll(
                nameField,
                classesField,
                startTimeField,
                endTimeField,
                prefLabel,
                scrollPane,
                saveButton,
                backButton
        );

        Scene scene = new Scene(root, 500, 700);
        primaryStage.setScene(scene);
    }
}
