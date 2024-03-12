package com.ioc.pacman;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.java.games.input.*;
import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.comtel2000.keyboard.control.*;


public class StartScene extends Scene {
    private final KeyboardPane kb;
    private final Controller controller;
    private final VBox root;
    private Robot robot;
    private Thread controllerThread;
    public StartScene() {
        super(new VBox(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);
        root = (VBox) getRoot();

        customizeScene();

        kb = new KeyboardPane();
        this.controller = getController();

        Platform.runLater(() -> {
            try {
                this.robot = new Robot();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });

        setController();
    }

    private Controller getController() {
        System.setProperty("net.java.games.input.useDefaultPlugin", "true");

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (Controller controller : controllers) {
            if (controller.getName().contains("Wireless Controller")) {
                System.out.println("PS4 controller found: " + controller.getName());

                return controller;
            }
        }

        return null;
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

    private boolean yRotationPressed = false;
    private void interpretControllerInput(Component component, float value) {
        if (component.isAnalog()) {
            if (component.getName().equals("X Axis") && (value < -0.7 || value > 0.7)) {
                int movement = (int) (value * 3.5);
                Platform.runLater(() -> {
                    double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
                    double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
                    robot.mouseMove((int) mouseX + movement, (int) mouseY);
                });
            } else if (component.getName().equals("Y Axis") && (value < -0.3 || value > 0.7)) {
                int movement = (int) (value * 3.5);
                Platform.runLater(() -> {
                    robot.mouseMove((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY() + movement);
                });
            } else if (component.getName().equals("Y Rotation")) {
                Platform.runLater(() -> {
                    boolean currentlyPressed = value > 0.8;
                    if (currentlyPressed != yRotationPressed) {
                        yRotationPressed = currentlyPressed;
                        if (yRotationPressed) {
                            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        } else {
                            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                });
            }
        }
    }

    private void customizeScene() {
        root.setSpacing(20);
        root.setAlignment(Pos.CENTER);

        addStartSceneBackgroundImage();

        addComponents();
    }

    private void addStartSceneBackgroundImage() {
        Image backgroundImage = null;
        try {
            backgroundImage = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanLogo.png"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        root.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
    }

    private void addComponents() {
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
                kb.setVisible(false);
                root.getChildren().remove(errorNameLabel);

                if (controllerThread != null && controllerThread.isAlive()) {
                    controllerThread.stop();
                }

                Pacman.player = new Player(nameInput.getText(), 0);

                int[][] pixels = ReadBackgroundPixels.readPixelMatrixFromExcel("src/main/resources/com/ioc/pacman/matrixPixels.xlsx", Pacman.SCENE_HEIGHT + 10, Pacman.SCENE_WEIGHT);

                Pacman.gameScene = new GameScene(pixels, controller);
                Pacman.startGame();
            }
        });

        nameInput.setOnMouseClicked(event -> {
            showVirtualKeyboard();
        });
    }

    private void showVirtualKeyboard() {
        try {
            kb.load();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        KeyBoardPopup popup = new KeyBoardPopup(kb);

        popup.show(root.getScene().getWindow());
    }
}
