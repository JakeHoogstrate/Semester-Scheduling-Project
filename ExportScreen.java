import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;

public class ExportScreen {

    private static Map<String, String> lastLoadedSchedule = null;

    public static void show(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #fff9c4;");

        Label title = new Label("📦 Export Saved Schedule");
        title.setStyle("-fx-font-size: 22px;");

        Label scheduleLabel = new Label();
        scheduleLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 14px;");
        scheduleLabel.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(scheduleLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        Button loadButton = new Button("🔄 Load Saved Schedule");
        Button downloadButton = new Button("⬇️ Download as .txt");
        Button backButton = new Button("🔙 Back");

        loadButton.setOnAction(e -> {
            lastLoadedSchedule = loadSavedSchedule();
            if (lastLoadedSchedule.isEmpty()) {
                scheduleLabel.setText("⚠️ No saved schedule found!");
            } else {
                StringBuilder display = new StringBuilder();
                for (Map.Entry<String, String> entry : lastLoadedSchedule.entrySet()) {
                    display.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n\n");
                }
                scheduleLabel.setText(display.toString());
            }
        });

        downloadButton.setOnAction(e -> {
            if (lastLoadedSchedule == null || lastLoadedSchedule.isEmpty()) {
                showAlert("⚠️ Please load a schedule first!");
            } else {
                exportScheduleAsText(lastLoadedSchedule);
                showAlert("✅ Schedule successfully downloaded!");
            }
        });

        backButton.setOnAction(e -> {
            MainApp mainApp = new MainApp();
            mainApp.start(primaryStage);
        });

        root.getChildren().addAll(title, loadButton, downloadButton, scrollPane, backButton);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
    }

    private static Map<String, String> loadSavedSchedule() {
        try (FileReader reader = new FileReader("saved_schedule.json")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("❌ Could not load saved_schedule.json.");
            return Map.of();
        }
    }

    private static void exportScheduleAsText(Map<String, String> scheduleData) {
        try (Writer writer = new FileWriter("exported_schedule.txt")) {
            for (Map.Entry<String, String> entry : scheduleData.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Download Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
