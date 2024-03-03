package com.ioc.pacman;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {
    private String name;
    private IntegerProperty score = new SimpleIntegerProperty(0);

    public Player(String name, int score) {
        this.name = name;
        this.score.set(score);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}
