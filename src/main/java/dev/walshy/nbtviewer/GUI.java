package dev.walshy.nbtviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    private static FormController controller;

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("/main.fxml").openStream());
        controller = fxmlLoader.getController();

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("NBTViewer");
        stage.setScene(scene);
        stage.show();
    }

    public static FormController getController() {
        return controller;
    }
}
