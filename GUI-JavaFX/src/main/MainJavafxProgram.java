package main;

import engine.api.EngineManagerJavafx;
import engine.impl.EngineManagerJavafxImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;

public class MainJavafxProgram extends Application {

    private EngineManagerJavafx engineManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {
        engineManager = new EngineManagerJavafxImpl();

        primaryStage.setTitle("Sheet-Cell");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("app.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        MainController mainController = fxmlLoader.getController();
        mainController.setEngineManager(engineManager);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}