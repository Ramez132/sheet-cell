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
        URL url = getClass().getResource("main.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        MainController mainController = fxmlLoader.getController();
        mainController.setEngineManager(engineManager);

//      Parent load = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}


//
//import javafx.application.Application;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
////video: 100188 - Java FX Hello World [JAD, JavaFX] | Powered by SpeaCode
//public class FirstTryJavaFXProgram extends Application {
//    private int clickCounter;
//    private Button btn;
//
//    @Override
//    public void start(Stage primaryStage) {
//        System.out.println("called on " + Thread.currentThread().getName());
//        btn = new Button();
//        btn.setText("Say 'Hello World'");
//        doSomethingWithButton();
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//
//        Scene scene = new Scene(root, 300, 250);
//
//        primaryStage.setTitle("Hello World!");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//        System.out.println("java fx is done");
//    }
//
//    private void doSomethingWithButton() {
//        EventHandler<ActionEvent> actionEventEventHandler =
//                event -> this.updateClickCounter();
//
//        btn.setOnAction(actionEventEventHandler);
//    }
//
//    private void updateClickCounter(){
//        ++clickCounter;
//        btn.setText("Clicked " + clickCounter + " Times");
//    }
//
//    public static void main(String[] args) {
//        new Thread(() -> System.out.println("blabla")).start();
//        launch(args);
//        System.out.println("main ended");
//    }
//
//}