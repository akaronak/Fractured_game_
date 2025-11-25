package com.fractured.core;

import com.fractured.model.Player;

public class GameState {
    private final Player player;
    private String currentScene;

    public GameState(Player player) { this.player = player; }

    public Player getPlayer() { return player; }
    public String getCurrentScene() { return currentScene; }
    public void setCurrentScene(String currentScene) { this.currentScene = currentScene; }
}
