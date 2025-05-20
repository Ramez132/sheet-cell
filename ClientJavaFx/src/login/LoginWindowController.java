package login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import management.window.ManagementWindowController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;

public class LoginWindowController {

    @FXML private ManagementWindowController managementWindowController;
    @FXML TextField userNameLoginTextField;
    @FXML Label messageToUserLabel;
    Scene managementWindowScene;
    Stage primaryStage;

    @FXML
    public void initialize() { }

    @FXML
    void handleLoginButtonClicked() {

        String userName = userNameLoginTextField.getText();
        if (userName.isEmpty()) {
            messageToUserLabel.setText("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        messageToUserLabel.setText("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        messageToUserLabel.setText("Something went wrong: " + responseBody);
                        messageToUserLabel.getStyleClass().add("error-trying-to-login");
                    });
                } else {
                    Platform.runLater(() -> {
                        managementWindowController.setUserName(userName);
                        managementWindowController.setActive();
                        goToManagementWindow();
                    });
                }
            }
        });
    }

    public void setManagementWindowScene(Scene managementWindowScene) {
        this.managementWindowScene = managementWindowScene;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setManagementWindowController(ManagementWindowController managementWindowController) {
        this.managementWindowController = managementWindowController;
    }

    private void goToManagementWindow() {
        primaryStage.setScene(managementWindowScene);
    }

}
