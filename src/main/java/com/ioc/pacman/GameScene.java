package com.ioc.pacman;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GameScene extends Scene {
    private final int START_POINT_X = 320;
    private final int START_POINT_Y = 407;
    private ImageView pacmanImageView;
    private int[][] pixels;

    public GameScene() {
        super(new StackPane(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);

        pixels = ReadBackgroundPixels.readPixelMatrixFromExcel("src/main/resources/com/ioc/pacman/matrixPixels.xlsx", Pacman.SCENE_HEIGHT + 10, Pacman.SCENE_WEIGHT);

        customizeScene();
        setKeyHandlers();
    }

    private void customizeScene() {
        StackPane root = (StackPane) getRoot();

        HBox backgroundGame = new HBox();
        backgroundGame.setAlignment(Pos.BOTTOM_RIGHT);

        Image backgroundImage = null;
        Image pacmanGIF = null;
        try {
            backgroundImage = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanBackground.png"));
            pacmanGIF = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanGIF4.gif"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Label nameLabel = new Label("  Name: " + Pacman.player.getName());
        nameLabel.setTextFill(Color.ORANGE);
        nameLabel.setFont(Font.font(20));

        Label scoreLabel = new Label();
        scoreLabel.textProperty().bind(Bindings.concat("Score: ", Pacman.player.scoreProperty(), "  "));
        scoreLabel.setTextFill(Color.ORANGE);
        scoreLabel.setFont(Font.font(20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        backgroundGame.getChildren().addAll(nameLabel, spacer, scoreLabel);

        backgroundGame.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        Pane pacmanMove = new Pane();
        pacmanImageView = new ImageView(pacmanGIF);
        pacmanImageView.setX(START_POINT_X);
        pacmanImageView.setY(START_POINT_Y);
        //System.out.println(pacmanImageView.getX() + "  " + pacmanImageView.getY());
        pacmanMove.getChildren().add(pacmanImageView);

        root.getChildren().addAll(backgroundGame, pacmanMove);
    }

    private void setKeyHandlers() {
        this.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            switch (key.getCode()) {
                case UP:
                    movePacmanUp();
                    break;
                case DOWN:
                    movePacmanDown();
                    break;
                case LEFT:
                    movePacmanLeft();
                    break;
                case RIGHT:
                    movePacmanRight();
                    break;
                case ESCAPE:
                    showMenu();
                    break;
                default:
                    break;
            }
        });
    }

    private void movePacmanUp() {
        pacmanImageView.setRotate(-90.0);

        double coordX = pacmanImageView.getX();
        double coordY = pacmanImageView.getY() - 3;
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setY(coordY);
        }
    }

    private void movePacmanDown() {
        pacmanImageView.setRotate(90.0);

        double coordX = pacmanImageView.getX();
        double coordY = pacmanImageView.getY() + 3;
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setY(coordY);
        }
    }

    private void movePacmanLeft() {
        pacmanImageView.setRotate(-180.0);

        double coordX = pacmanImageView.getX() - 3;
        double coordY = pacmanImageView.getY();
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setX(coordX);
        }
    }

    private void movePacmanRight() {
        pacmanImageView.setRotate(0.0);

        //pacmanImageView.setX(pacmanImageView.getX() + 3);

        double coordX = pacmanImageView.getX() + 3;
        double coordY = pacmanImageView.getY();
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setX(coordX);
        }
    }

    private int checkMove(double coordX, double coordY) {

        int minCoordX = (int) coordX;
        int minCoordY = (int) coordY;

        int maxCoordX = minCoordX + 25;
        int maxCoordY =  minCoordY + 30;

        for(int X = minCoordX; X <= maxCoordX; X++) {
            for(int Y = minCoordY; Y <= maxCoordY; Y++) {
                if(pixels[X][Y] == 1) {
                    return -1;
                }
                //System.out.print(pixels[X][Y]);
            }
            //System.out.println();
        }

//        System.out.println();
//        System.out.println();
//        System.out.println();
        return 0;
    }


    private void showMenu() {
        System.out.println("Menu");
    }
}