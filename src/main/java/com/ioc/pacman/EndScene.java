package com.ioc.pacman;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class EndScene extends Scene {
    private final Controller controller;
    private Thread controllerThread;
    private Button playAgainButton;
    public EndScene(String gameState, Controller controller) {
        super(new VBox(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);

        this.controller = controller;
        setController();
        customizeScene(gameState);
    }

    private void setController() {
        controllerThread = new Thread(() -> {
            while (true) {
                controller.poll();
                EventQueue queue = controller.getEventQueue();
                Event event = new Event();

                while (queue.getNextEvent(event)) {
                    Component component = event.getComponent();
                    float value = event.getValue();

                    interpretControllerInput(component, value);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        controllerThread.start();
    }
    private boolean playAgainButtonPressed = false;
    private void interpretControllerInput(Component component, float value) {
        if (component.isAnalog()){
            if (component.getName().equals("Y Rotation") && value > 0.8 && !playAgainButtonPressed) {

                Platform.runLater(() -> {
                    if (!playAgainButtonPressed) {
                        playAgainButton.fire();
                        controllerThread.stop();
                        playAgainButtonPressed = true;
                    }
                });
            }
        }
    }

    private void customizeScene(String gameState) {
        VBox root = (VBox) this.getRoot();
        root.setSpacing(20);
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

        playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-text-fill: orange; " +
                "-fx-background-color: black; " +
                "-fx-border-color: orange;" +
                "-fx-font-size: 18px;");
        playAgainButton.setOnAction(event -> {
            Pacman.playAgain();
        });

        root.getChildren().addAll(message, playAgainButton);
    }


}
