package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import management.window.ManagementWindowController;
import operating.window.SheetWindowController;
import util.http.HttpClientUtil;

import java.net.URL;

public class MainJavafxProgram extends Application {

    private Scene loginScene;
    private Scene sheetWindowScene;
    private Scene managementWindowScene;
    ManagementWindowController managementWindowController;
    SheetWindowController sheetWindowController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {

        primaryStage.setTitle("Sheet-Cell");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getClassLoader().getResource("login/loginWindow.fxml");
        fxmlLoader.setLocation(url);
        Parent loginWindowRoot = fxmlLoader.load(url.openStream());
        LoginWindowController loginWindowController = fxmlLoader.getController();
        loginScene = new Scene(loginWindowRoot);

        FXMLLoader fxmlLoaderForManagementWindow = new FXMLLoader();
        URL url2 = getClass().getClassLoader().getResource("management/window/managementWindow.fxml");
        fxmlLoaderForManagementWindow.setLocation(url2);
        Parent ManagementWindowRoot = fxmlLoaderForManagementWindow.load(url2.openStream());
        managementWindowController  = fxmlLoaderForManagementWindow.getController();
        managementWindowScene = new Scene(ManagementWindowRoot);

        FXMLLoader fxmlLoaderForSheetWindow = new FXMLLoader();
        URL url3 = getClass().getClassLoader().getResource("operating/window/sheetWindow.fxml");
        fxmlLoaderForSheetWindow.setLocation(url3);
        Parent sheetWindowRoot = fxmlLoaderForSheetWindow.load(url3.openStream());
        sheetWindowController = fxmlLoaderForSheetWindow.getController();
        sheetWindowScene = new Scene(sheetWindowRoot);

        loginWindowController.setManagementWindowScene(managementWindowScene);
        managementWindowController.setSheetWindowScene(sheetWindowScene);
        sheetWindowController.setManagementWindowScene(managementWindowScene);

        loginWindowController.setPrimaryStage(primaryStage);
        managementWindowController.setPrimaryStage(primaryStage);
        sheetWindowController.setPrimaryStage(primaryStage);

        loginWindowController.setManagementWindowController(managementWindowController);
        managementWindowController.setSheetWindowController(sheetWindowController);
        sheetWindowController.setManagementWindowController(managementWindowController);

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
        managementWindowController.close();
        sheetWindowController.close();
    }
}