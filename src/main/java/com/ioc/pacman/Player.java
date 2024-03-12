package com.ioc.pacman;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Player {
    private String name;
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private int health;

    public Player(String name, int score) {
        this.name = name;
        this.score.set(score);
        this.health = 3;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void minusHealth() {
        if(this.health > 0) {
            this.health--;
        }
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}
