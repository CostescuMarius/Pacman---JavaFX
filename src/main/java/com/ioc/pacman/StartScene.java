package com.ioc.pacman;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StartScene extends Scene {
    public StartScene() {
        super(new VBox(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);
        customizeScene();
    }

    private void customizeScene() {
        VBox root = (VBox) getRoot();;
        root.setSpacing(30);
        root.setAlignment(Pos.CENTER);

        Image backgroundImage = null;
        try {
            backgroundImage = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanLogo.png"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        root.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        Label nameLabel = new Label("Insert Name");
        nameLabel.setTextFill(Color.ORANGE);
        nameLabel.setFont(Font.font("Comic Sans MS", 25));

        TextField nameInput = new TextField();
        nameInput.setMinHeight(27);
        nameInput.setMaxWidth(200);
        nameInput.setStyle("-fx-font-weight: bold; " +
                "-fx-alignment: center; " +
                "-fx-font-size: 18px;");

        Button startButton = new Button("Start Game");
        startButton.setStyle("-fx-text-fill: orange; " +
                "-fx-background-color: black; " +
                "-fx-border-color: orange;" +
                "-fx-font-size: 18px;");

        root.getChildren().addAll(nameLabel, nameInput, startButton);

        Label errorNameLabel = new Label("Please enter your name");
        errorNameLabel.setTextFill(Color.RED);
        errorNameLabel.setFont(Font.font( 20));

        startButton.setOnAction(event -> {
            boolean labelExists = false;
            for (Node node : root.getChildren()) {
                if (node.equals(errorNameLabel)) {
                    labelExists = true;
                    break;
                }
            }
            if(nameInput.getText().isEmpty()) {
                if(!labelExists) {
                    root.getChildren().add(errorNameLabel);
                }
            } else {
                root.getChildren().remove(errorNameLabel);
                Pacman.player = new Player(nameInput.getText(), 0);
                Pacman.gameScene = new GameScene();
                Pacman.startGame();
            }
        });

    }
}
