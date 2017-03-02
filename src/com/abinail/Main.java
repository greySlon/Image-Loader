package com.abinail;

import com.abinail.viewmodel.ViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Sergii on 01.03.2017.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("viewmodel/View.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Image loader");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        ViewController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.dispose();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
