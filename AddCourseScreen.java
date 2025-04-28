import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;

public class AddCourseScreen {
    public static void show(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #e0f7fa;");

        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");

        TextField idField = new TextField();
        idField.setPromptText("Course ID (numbers only)");

        ComboBox<String> seasonBox = new ComboBox<>();
        seasonBox.getItems().addAll("Both", "Fall", "Spring");
        seasonBox.setValue("Both");

        TextField creditsField = new TextField();
        creditsField.setPromptText("Credits");

        TextField minSectionsField = new TextField();
        minSectionsField.setPromptText("Minimum Sections");

        TextField maxSectionsField = new TextField();
        maxSectionsField.setPromptText("Maximum Sections");

        Button saveButton = new Button("Save Course");
        Button backButton = new Button("Back");

        saveButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                int id = Integer.parseInt(idField.getText());
                String seasonInput = seasonBox.getValue();
                int season = 0;
                if (seasonInput.equals("Fall")) season = 1;
                else if (seasonInput.equals("Spring")) season = 2;

                int credits = Integer.parseInt(creditsField.getText());
                int minSections = Integer.parseInt(minSectionsField.getText());
                int maxSections = Integer.parseInt(maxSectionsField.getText());

                Course course = new Course(name, id, season, credits, minSections, maxSections);

                // Add to in-memory course map
                CourseJsonLoader.getCourseMap().put(name, course);

                // Write to JSON
                CourseJsonWriter.addCourse(course);

                System.out.println("Saved course: " + name);

                // After saving, return to Main Menu
                MainApp mainApp = new MainApp();
                mainApp.start(primaryStage);

            } catch (Exception ex) {
                ex.printStackTrace();
                // Optional: Show an error popup to user
            }
        });


        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });

        root.getChildren().addAll(
                nameField,
                idField,
                seasonBox,
                creditsField,
                minSectionsField,
                maxSectionsField,
                saveButton,
                backButton
        );

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
    }
}
