import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class EditCourseScreen {
    public static void show(Stage primaryStage) {

        Map<String, Course> courseMap = CourseJsonLoader.loadCourses("courses_data.json");

        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f5f5f5;");

        Label title = new Label("Edit Course Data");
        title.setStyle("-fx-font-size: 24px;");


        ComboBox<String> courseChoice = new ComboBox<>();
        courseChoice.getItems().addAll(courseMap.keySet());
        courseChoice.setPromptText("Select a Course to Edit");


        TextField nameField = new TextField();
        TextField idField = new TextField();
        TextField creditsField = new TextField();
        TextField minSectionsField = new TextField();
        TextField maxSectionsField = new TextField();


        nameField.setPromptText("Enter Course Name (e.g., CS101)");
        idField.setPromptText("Enter Course ID (e.g., 371 for CS371)");
        creditsField.setPromptText("Enter Number of Credits");
        minSectionsField.setPromptText("Enter Minimum Sections");
        maxSectionsField.setPromptText("Enter Maximum Sections");

        Button saveButton = new Button("Save Changes");
        Button backButton = new Button("Back");


        courseChoice.setOnAction(e -> {
            String selectedCourse = courseChoice.getValue();
            if (selectedCourse != null) {
                Course course = courseMap.get(selectedCourse);
                nameField.setText(course.getName());
                idField.setText(String.valueOf(course.getId()));
                creditsField.setText(String.valueOf(course.getCredits()));
                minSectionsField.setText(String.valueOf(course.getMinSections()));
                maxSectionsField.setText(String.valueOf(course.getMaxSections()));
            }
        });


        saveButton.setOnAction(e -> {
            String selectedCourse = courseChoice.getValue();
            if (selectedCourse != null) {
                Course course = courseMap.get(selectedCourse);


                course.setName(nameField.getText());
                course.setId(Integer.parseInt(idField.getText()));
                course.setCredits(Integer.parseInt(creditsField.getText()));
                course.setMinSections(Integer.parseInt(minSectionsField.getText()));
                course.setMaxSections(Integer.parseInt(maxSectionsField.getText()));


                CourseJsonWriter.updateCourse(course);

                System.out.println("Course updated: " + course.getName());
            }
        });


        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });


        root.getChildren().addAll(title, courseChoice, nameField, idField, creditsField, minSectionsField, maxSectionsField, saveButton, backButton);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
    }
}
