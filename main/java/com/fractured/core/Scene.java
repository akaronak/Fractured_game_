package com.fractured.core;

import java.util.Scanner;

public abstract class Scene {
    private final String id;
    private final String title;

    protected Scene(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId(){ return id; }
    public String getTitle(){ return title; }

    /**
     * Play this scene. Must return the id of the next scene (or null to end).
     * Implementations may modify game state (player, inventory, sanity).
     */
    public abstract String play(GameState state, Scanner in) throws GameException;
}
