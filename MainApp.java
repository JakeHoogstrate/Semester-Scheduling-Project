import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #f0f0f0;");


        Button addCourseButton = new Button("➕ Add Course");
        Button addFacultyButton = new Button("➕ Add Faculty");

        Button editFacultyButton = new Button("📝 Edit Faculty");
        Button editCourseButton = new Button("📝 Edit Course");

        Button generateScheduleButton = new Button("🧠 Generate Schedule");
        Button exportButton = new Button("💾 Export Schedule");
        Button exitButton = new Button("🚪 Exit");


        addCourseButton.setPrefWidth(250);
        addFacultyButton.setPrefWidth(250);
        generateScheduleButton.setPrefWidth(250);
        exportButton.setPrefWidth(250);
        exitButton.setPrefWidth(250);
        editFacultyButton.setPrefWidth(250);
        editCourseButton.setPrefWidth(250);


        addCourseButton.setOnAction(e -> AddCourseScreen.show(primaryStage));
        addFacultyButton.setOnAction(e -> AddFacultyScreen.show(primaryStage));
        generateScheduleButton.setOnAction(e -> GenerateScheduleScreen.show(primaryStage));
        exportButton.setOnAction(e -> ExportScreen.show(primaryStage));
        exitButton.setOnAction(e -> primaryStage.close());


        editFacultyButton.setOnAction(e -> EditFacultyScreen.show(primaryStage));
        editCourseButton.setOnAction(e -> EditCourseScreen.show(primaryStage));


        root.getChildren().addAll(
                addCourseButton,
                addFacultyButton,
                editFacultyButton,
                editCourseButton,
                generateScheduleButton,
                exportButton,
                exitButton
        );


        Scene scene = new Scene(root, 400, 550);
        primaryStage.setTitle("Faculty Course Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
