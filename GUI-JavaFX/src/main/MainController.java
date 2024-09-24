package main;

import engine.api.EngineManagerJavafx;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.File;

public class MainController {

    private EngineManagerJavafx engineManager;

    @FXML
    private Button loadNewFileButton;
    @FXML
    private Label filePathLabel;
    @FXML
    private Label notificationMessageOfRecentActionOutcomeLabel;
    @FXML
    private Label notificationHeadlineOfRecentActionOutcomeLabel;

    public void setEngineManager(EngineManagerJavafx engineManager) {
        this.engineManager = engineManager;
    }

    @FXML
    public void handleLoadNewFile() {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );

        // Show the file chooser and wait for user selection
        File selectedFile = fileChooser.showOpenDialog(loadNewFileButton.getScene().getWindow());

        // If a file is selected, display its name in the label
        if (selectedFile != null) {
            notificationHeadlineOfRecentActionOutcomeLabel.setText("Message of recent attempt to upload a file:");
            try {
                engineManager.getSheetFromFile(selectedFile);
                notificationMessageOfRecentActionOutcomeLabel.setText("Recent file loaded successfully");
                filePathLabel.setText(selectedFile.getAbsoluteFile().toString());
            } catch (Exception e) {
                notificationMessageOfRecentActionOutcomeLabel.setText(e.getMessage());
            }
        }
    }
}
