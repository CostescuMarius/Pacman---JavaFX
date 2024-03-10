package com.ioc.pacman;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class EndScene extends Scene {
    public EndScene(String gameState) {
        super(new StackPane(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);

        customizeScene(gameState);
    }

    private void customizeScene(String gameState) {
        StackPane root = (StackPane) this.getRoot();
        root.setStyle("-fx-background-color: black;");
        root.setAlignment(Pos.CENTER);

        Text message = new Text();
        message.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        message.setFill(Color.ORANGE);

        if (gameState.equals("win")) {
            message.setText(Pacman.player.getName() + " won!\nScore: " + Pacman.player.getScore());
        } else if (gameState.equals("lose")) {
            message.setText(Pacman.player.getName() + " lost!\nScore: " + Pacman.player.getScore());
        }

        root.getChildren().add(message);
    }
}
