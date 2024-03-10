package com.ioc.pacman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Pacman extends Application {
    public static final int SCENE_HEIGHT = 780;
    public static final int SCENE_WEIGHT = 675;

    public static Player player;
    private static final Scene startScene = new StartScene();
    public static Scene gameScene;
    public static Scene endScene;
    private static Stage stage;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Pacman.stage = stage;

        stage.setTitle("Pacman");

        stage.setScene(startScene);

        stage.show();
    }

    public static void startGame() {
        stage.setScene(gameScene);
    }
    public static void endGame() {
        stage.setScene(endScene);
    }
}