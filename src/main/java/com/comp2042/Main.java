package com.comp2042;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("TetrisJFX");

        // Create scene once with 800x600 to accommodate both game modes
        scene = new Scene(new Pane(), 800, 600);
        primaryStage.setScene(scene);

        // Load single player mode initially
        loadSinglePlayerMode();
        primaryStage.show();
    }

    public void loadSinglePlayerMode() throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        // Swap the root node (keeps same scene)
        scene.setRoot(root);

        new GameController(c);

        // Wire up 2-player button to switch modes
        c.setModeSwitch(() -> {
            try {
                loadTwoPlayerMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadTwoPlayerMode() throws Exception {
        URL location = getClass().getClassLoader().getResource("twoPlayerLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        TwoPlayerGuiController c = fxmlLoader.getController();

        // Swap the root node (keeps same scene)
        scene.setRoot(root);

        // Wire up home button to switch back to single player mode
        c.setModeSwitch(() -> {
            try {
                loadSinglePlayerMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
