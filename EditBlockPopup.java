import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EditBlockPopup {
    public static void show(StackPane block, String courseName, String facultyName, int startMinutes, int endMinutes) {
        Stage popup = new Stage();
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #f5f5f5;");

        Label title = new Label("Edit Class Block");
        title.setStyle("-fx-font-size: 20px;");

        TextField facultyField = new TextField(facultyName);
        TextField startTimeField = new TextField(String.valueOf(startMinutes));
        TextField endTimeField = new TextField(String.valueOf(endMinutes));

        Button saveButton = new Button("Save Changes");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            String newFaculty = facultyField.getText();
            int newStart = Integer.parseInt(startTimeField.getText());
            int newEnd = Integer.parseInt(endTimeField.getText());


            Label label = (Label) block.getChildren().get(0);

            StringBuilder updatedText = new StringBuilder();
            updatedText.append(courseName).append("\n");
            updatedText.append(newFaculty).append("\n");
            updatedText.append(formatTimeFromMinutes(newStart)).append("-").append(formatTimeFromMinutes(newEnd));

            label.setText(updatedText.toString());

            popup.close();
        });

        cancelButton.setOnAction(e -> popup.close());

        root.getChildren().addAll(title, facultyField, startTimeField, endTimeField, saveButton, cancelButton);

        Scene scene = new Scene(root, 300, 300);
        popup.setScene(scene);
        popup.setTitle("Edit Class Block");
        popup.show();
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
