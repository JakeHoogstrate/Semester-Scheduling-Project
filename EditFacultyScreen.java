import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class EditFacultyScreen {
    public static void show(Stage primaryStage) {

        Map<String, Faculty> facultyMap = FacultyJsonLoader.loadFaculty("faculty_data.json", CourseJsonLoader.loadCourses("courses_data.json"));

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f5f5f5;");

        Label title = new Label("Edit Faculty Data");
        title.setStyle("-fx-font-size: 24px;");


        ComboBox<String> facultyChoice = new ComboBox<>();
        facultyChoice.getItems().addAll(facultyMap.keySet());
        facultyChoice.setPromptText("Select Faculty to Edit");


        TextField nameField = new TextField();
        TextField classesField = new TextField();
        TextField startTimeField = new TextField();
        TextField endTimeField = new TextField();


        nameField.setPromptText("Enter Faculty Name");
        classesField.setPromptText("Enter Number of Classes");
        startTimeField.setPromptText("Start Time (e.g., 1430 for 2:30 PM)");
        endTimeField.setPromptText("End Time (e.g., 1730 for 5:30 PM)");

        Button saveButton = new Button("Save Changes");
        Button backButton = new Button("Back");


        facultyChoice.setOnAction(e -> {
            String selectedFaculty = facultyChoice.getValue();
            if (selectedFaculty != null) {
                Faculty faculty = facultyMap.get(selectedFaculty);
                nameField.setText(faculty.getName());
                classesField.setText(String.valueOf(faculty.getClasses()));
                startTimeField.setText(String.valueOf(faculty.getTimeStartPref()));
                endTimeField.setText(String.valueOf(faculty.getTimeEndPref()));
            }
        });


        saveButton.setOnAction(e -> {
            String selectedFaculty = facultyChoice.getValue();
            if (selectedFaculty != null) {
                Faculty faculty = facultyMap.get(selectedFaculty);


                faculty.setName(nameField.getText());
                faculty.setClasses(Integer.parseInt(classesField.getText()));
                faculty.setTimeStartPref(Integer.parseInt(startTimeField.getText()));
                faculty.setTimeEndPref(Integer.parseInt(endTimeField.getText()));


                FacultyJsonWriter.updateFaculty(faculty);

                System.out.println("Faculty updated: " + faculty.getName());
            }
        });


        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });


        root.getChildren().addAll(title, facultyChoice, nameField, classesField, startTimeField, endTimeField, saveButton, backButton);

        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
    }
}
