package com.ioc.pacman;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javafx.scene.input.KeyEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.util.Duration;
import net.java.games.input.*;

public class GameScene extends Scene {
    private ImageView pacmanImageView;
    private ImageView phantomsImageView[] = new ImageView[3];
    private final int[][] pixels;

    private final int MOVE_PIXELS = 10;
    private final int PACMAN_WIDTH = 30;
    private final int PACMAN_HEIGHT = 25;
    private final int START_POINT_X = 320;
    private final int START_POINT_Y = 410;
    private final long targetFrameTime = 1000000000 / 40;
    private long lastFrameTime = 0;
    private TextInputControlSkin.Direction controllerDirection = TextInputControlSkin.Direction.RIGHT;
    private ArrayList<Point> points;
    private StackPane root;
    private Pane pacmanMove;
    private Pane phantomsMove;
    private Thread controllerThread;

    public GameScene(int[][] pixels, Controller controller) {
        super(new StackPane(), Pacman.SCENE_WEIGHT, Pacman.SCENE_HEIGHT);

        this.pixels = pixels;

        customizeScene();
        setKeyHandlers();

        setController(controller);
        startGameLoop();
    }

    private void setController(Controller controller) {
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

    private void interpretControllerInput(Component component, float value) {
        if (component.isAnalog()){
            if (component.getName().equals("Y Axis")) {
                if (value < -0.4) {
                    controllerDirection = TextInputControlSkin.Direction.UP;
                } else if (value > 0.8) {
                    controllerDirection = TextInputControlSkin.Direction.DOWN;
                }
            } else if (component.getName().equals("X Axis")) {
                if (value < -0.8) {
                    controllerDirection = TextInputControlSkin.Direction.LEFT;
                } else if (value > 0.8) {
                    controllerDirection = TextInputControlSkin.Direction.RIGHT;
                }
            }
        }
    }

    private void customizeScene() {
        root = (StackPane) getRoot();

        HBox backgroundGame = new HBox();
        backgroundGame.setAlignment(Pos.BOTTOM_RIGHT);
        backgroundGame.setSpacing(15);

        Image backgroundImage = null;
        Image pacmanGIF = null;
        Image phantom1GIF = null;
        Image phantom2GIF = null;
        Image phantom3GIF = null;
        try {
            backgroundImage = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanBackground.png"));
            pacmanGIF = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/pacmanGIF.gif"));
            phantom1GIF = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/phantom1.gif"));
            phantom2GIF = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/phantom2.gif"));
            phantom3GIF = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/phantom3.gif"));
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

        backgroundGame.getChildren().add(nameLabel);

        for (int i = 0; i < Pacman.player.getHealth(); i++) {
            Image heartImage = null;
            try {
                heartImage = new Image(new FileInputStream("src/main/resources/com/ioc/pacman/health.png"));
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

            ImageView heartImageView = new ImageView(heartImage);
            heartImageView.setFitWidth(20);
            heartImageView.setFitHeight(20);
            backgroundGame.getChildren().add(heartImageView);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        backgroundGame.getChildren().addAll(spacer, scoreLabel);

        backgroundGame.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

        //add packman
        pacmanMove = new Pane();
        pacmanImageView = new ImageView(pacmanGIF);

        pacmanImageView.setX(START_POINT_X);
        pacmanImageView.setY(START_POINT_Y);

        pacmanMove.getChildren().add(pacmanImageView);

        //add Phantom1
        phantomsMove = new Pane();
        phantomsImageView[0] = new ImageView(phantom1GIF);
        phantomsImageView[0].setX(141);

        TranslateTransition moveTransition = new TranslateTransition(Duration.seconds(5), phantomsImageView[0]);
        moveTransition.setFromY(23);
        moveTransition.setToY(605);
        moveTransition.setAutoReverse(true);
        moveTransition.setCycleCount(TranslateTransition.INDEFINITE);

        moveTransition.play();

        phantomsMove.getChildren().add(phantomsImageView[0]);

        //add Phantom2
        phantomsImageView[1] = new ImageView(phantom2GIF);
        phantomsImageView[1].setX(505);

        TranslateTransition moveTransition2 = new TranslateTransition(Duration.seconds(5), phantomsImageView[1]);
        moveTransition2.setFromY(605);
        moveTransition2.setToY(23);
        moveTransition2.setAutoReverse(true);
        moveTransition2.setCycleCount(TranslateTransition.INDEFINITE);

        moveTransition2.play();

        phantomsMove.getChildren().add(phantomsImageView[1]);

        //add Phantom3
        phantomsImageView[2] = new ImageView(phantom3GIF);
        phantomsImageView[2].setY(115);

        TranslateTransition moveTransition3 = new TranslateTransition(Duration.seconds(4), phantomsImageView[2]);
        moveTransition3.setFromX(30);
        moveTransition3.setToX(620);
        moveTransition3.setAutoReverse(true);
        moveTransition3.setCycleCount(TranslateTransition.INDEFINITE);

        moveTransition3.play();

        phantomsMove.getChildren().add(phantomsImageView[2]);

        addPoints();

        root.getChildren().addAll(backgroundGame, pacmanMove, phantomsMove);
    }

    private void addPoints() {
        points = new ArrayList<>();

        points.add(new Point(30, 30));
        points.add(new Point(90, 30));
        points.add(new Point(155, 30));
        points.add(new Point(205, 30));
        points.add(new Point(250, 30));
        points.add(new Point(300, 30));
        points.add(new Point(370, 30));
        points.add(new Point(420, 30));
        points.add(new Point(470, 30));
        points.add(new Point(530, 30));
        points.add(new Point(585, 30));
        points.add(new Point(630, 30));

        points.add(new Point(30, 80));
        points.add(new Point(155, 80));
        points.add(new Point(300, 80));
        points.add(new Point(370, 80));
        points.add(new Point(510, 80));
        points.add(new Point(630, 80));

        points.add(new Point(30, 130));
        points.add(new Point(90, 130));
        points.add(new Point(155, 130));
        points.add(new Point(205, 130));
        points.add(new Point(250, 130));
        points.add(new Point(300, 130));
        points.add(new Point(350, 130));
        points.add(new Point(400, 130));
        points.add(new Point(450, 130));
        points.add(new Point(500, 130));
        points.add(new Point(550, 130));
        points.add(new Point(585, 130));
        points.add(new Point(630, 130));

        points.add(new Point(30, 165));
        points.add(new Point(155, 165));
        points.add(new Point(215, 165));
        points.add(new Point(440, 165));
        points.add(new Point(510, 165));
        points.add(new Point(630, 165));

        points.add(new Point(30, 205));
        points.add(new Point(90, 205));
        points.add(new Point(155, 205));
        points.add(new Point(230, 205));
        points.add(new Point(280, 205));
        points.add(new Point(380, 205));
        points.add(new Point(435, 205));
        points.add(new Point(510, 205));
        points.add(new Point(570, 205));
        points.add(new Point(630, 205));

        points.add(new Point(155, 240));
        points.add(new Point(300, 240));
        points.add(new Point(370, 240));
        points.add(new Point(510, 240));

        points.add(new Point(155, 275));
        points.add(new Point(230, 275));
        points.add(new Point(280, 275));
        points.add(new Point(330, 275));
        points.add(new Point(380, 275));
        points.add(new Point(435, 275));
        points.add(new Point(510, 275));

        points.add(new Point(155, 310));
        points.add(new Point(230, 310));
        points.add(new Point(435, 310));
        points.add(new Point(510, 310));

        points.add(new Point(5, 345));
        points.add(new Point(55, 345));
        points.add(new Point(105, 345));
        points.add(new Point(155, 345));
        points.add(new Point(190, 345));
        points.add(new Point(230, 345));
        points.add(new Point(435, 345));
        points.add(new Point(473, 345));
        points.add(new Point(510, 345));
        points.add(new Point(560, 345));
        points.add(new Point(610, 345));
        points.add(new Point(660, 345));

        points.add(new Point(155, 380));
        points.add(new Point(230, 380));
        points.add(new Point(435, 380));
        points.add(new Point(510, 380));

        points.add(new Point(155, 420));
        points.add(new Point(230, 420));
        points.add(new Point(280, 420));
        points.add(new Point(330, 420));
        points.add(new Point(380, 420));
        points.add(new Point(435, 420));
        points.add(new Point(510, 420));

        points.add(new Point(155, 458));
        points.add(new Point(230, 458));
        points.add(new Point(435, 458));
        points.add(new Point(510, 458));

        points.add(new Point(30, 495));
        points.add(new Point(90, 495));
        points.add(new Point(155, 495));
        points.add(new Point(205, 495));
        points.add(new Point(250, 495));
        points.add(new Point(300, 495));
        points.add(new Point(400, 495));
        points.add(new Point(450, 495));
        points.add(new Point(500, 495));
        points.add(new Point(550, 495));
        points.add(new Point(585, 495));
        points.add(new Point(630, 495));

        points.add(new Point(30, 530));
        points.add(new Point(155, 530));
        points.add(new Point(295, 530));
        points.add(new Point(367, 530));
        points.add(new Point(515, 530));
        points.add(new Point(630, 530));

        points.add(new Point(30, 570));
        points.add(new Point(75, 570));
        points.add(new Point(155, 570));
        points.add(new Point(205, 570));
        points.add(new Point(250, 570));
        points.add(new Point(300, 570));
        points.add(new Point(350, 570));
        points.add(new Point(400, 570));
        points.add(new Point(450, 570));
        points.add(new Point(500, 570));
        points.add(new Point(585, 570));
        points.add(new Point(630, 570));

        points.add(new Point(80, 605));
        points.add(new Point(155, 605));
        points.add(new Point(230, 605));
        points.add(new Point(440, 605));
        points.add(new Point(515, 605));
        points.add(new Point(580, 605));

        points.add(new Point(30, 640));
        points.add(new Point(90, 640));
        points.add(new Point(155, 640));
        points.add(new Point(240, 640));
        points.add(new Point(290, 640));
        points.add(new Point(365, 640));
        points.add(new Point(430, 640));
        points.add(new Point(520, 640));
        points.add(new Point(570, 640));
        points.add(new Point(630, 640));

        points.add(new Point(30, 675));
        points.add(new Point(300, 675));
        points.add(new Point(365, 675));
        points.add(new Point(630, 675));

        points.add(new Point(30, 715));
        points.add(new Point(90, 715));
        points.add(new Point(155, 715));
        points.add(new Point(205, 715));
        points.add(new Point(250, 715));
        points.add(new Point(300, 715));
        points.add(new Point(350, 715));
        points.add(new Point(400, 715));
        points.add(new Point(450, 715));
        points.add(new Point(500, 715));
        points.add(new Point(550, 715));
        points.add(new Point(585, 715));
        points.add(new Point(630, 715));

        for(Point point : points) {
            Rectangle rectangle = new Rectangle(10, 10, Color.YELLOW);

            rectangle.setTranslateX(point.getCoordX());
            rectangle.setTranslateY(point.getCoordY());

            pacmanMove.getChildren().add(rectangle);
        }
    }

    private void setKeyHandlers() {
        this.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            switch (key.getCode()) {
                case UP:
                    controllerDirection = TextInputControlSkin.Direction.UP;
                    break;
                case DOWN:
                    controllerDirection = TextInputControlSkin.Direction.DOWN;
                    break;
                case LEFT:
                    controllerDirection = TextInputControlSkin.Direction.LEFT;
                    break;
                case RIGHT:
                    controllerDirection = TextInputControlSkin.Direction.RIGHT;
                    break;
                default:
                    break;
            }
        });
    }

    private void movePacmanUp() {
        pacmanImageView.setRotate(-90.0);

        double coordX = pacmanImageView.getX();
        double coordY = pacmanImageView.getY() - MOVE_PIXELS;
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setY(coordY);
        }
    }

    private void movePacmanDown() {
        pacmanImageView.setRotate(90.0);

        double coordX = pacmanImageView.getX();
        double coordY = pacmanImageView.getY() + MOVE_PIXELS;
        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setY(coordY);
        }
    }

    private void movePacmanLeft() {
        pacmanImageView.setRotate(-180.0);

        double coordX = pacmanImageView.getX() - MOVE_PIXELS;
        double coordY = pacmanImageView.getY();

        if(coordX < 0) {
            coordX = (Pacman.SCENE_WEIGHT - 1) - PACMAN_WIDTH;
        }

        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setX(coordX);
        }
    }

    private void movePacmanRight() {
        pacmanImageView.setRotate(0.0);

        double coordX = pacmanImageView.getX() + MOVE_PIXELS;
        double coordY = pacmanImageView.getY();

        if(coordX > (Pacman.SCENE_WEIGHT - 1) - PACMAN_WIDTH) {
            coordX = 0;
        }

        if(checkMove(coordY, coordX) == 0) {
            pacmanImageView.setX(coordX);
        }
    }

    private int checkMove(double coordY, double coordX) {
        if(checkCollision()) {
            return -1;
        }

        int minCoordY = (int) coordY;
        int minCoordX = (int) coordX;

        int maxCoordY = minCoordY + PACMAN_HEIGHT;
        int maxCoordX =  minCoordX + PACMAN_WIDTH;



        Point checkPointExists = null;
        for(int Y = minCoordY; Y <= maxCoordY; Y++) {
            for(int X = minCoordX; X <= maxCoordX; X++) {
                if(pixels[Y][X] == 1) {
                    return -1;
                }

                for(Point point : points) {
                    if(point.getCoordX() == X && point.getCoordY() == Y) {
                        checkPointExists = point;
                    }
                }
            }
        }

        if(checkPointExists != null) {
            Iterator<Node> iterator = pacmanMove.getChildren().iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node instanceof Rectangle) {
                    Rectangle rectangle = (Rectangle) node;
                    if (rectangle.getTranslateX() == checkPointExists.getCoordX() &&
                            rectangle.getTranslateY() == checkPointExists.getCoordY()) {
                        iterator.remove();
                        Pacman.player.setScore(Pacman.player.getScore() + 10);
                        break;
                    }
                }
            }
        }

        if(Pacman.player.getScore() == 1520) {
            controllerThread.stop();
            Pacman.endScene = new EndScene("win");
            Pacman.endGame();
        }

        return 0;
    }

    private boolean checkCollision() {
        Rectangle pacmanHitBox = new Rectangle(pacmanImageView.getBoundsInParent().getMinX(),
                pacmanImageView.getBoundsInParent().getMinY(),
                PACMAN_WIDTH, PACMAN_HEIGHT);

        for (ImageView phantomImageView : phantomsImageView) {
            Rectangle phantomHitBox = new Rectangle(phantomImageView.getBoundsInParent().getMinX(),
                    phantomImageView.getBoundsInParent().getMinY(),
                    phantomImageView.getBoundsInParent().getWidth(),
                    phantomImageView.getBoundsInParent().getHeight());

            if (pacmanHitBox.getBoundsInParent().intersects(phantomHitBox.getBoundsInParent())) {
                Pacman.player.minusHealth();

                pacmanImageView.setX(320);
                pacmanImageView.setY(410);

                Pacman.endScene = new EndScene("lose");
                if(Pacman.player.getHealth() == 0) {
                    Pacman.endGame();
                }

                return true;
            }
        }
        return false;
    }

    public void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastFrameTime >= targetFrameTime) {
                    lastFrameTime = now;
                    updateGame();
                }
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        switch (controllerDirection) {
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
            default:
                break;
        }
    }
}