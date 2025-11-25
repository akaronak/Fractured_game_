package com.fractured.core;

import java.util.Scanner;

public class DialogueScene extends Scene {
    private final String[] lines;
    private final String nextScene;

    public DialogueScene(String id, String title, String[] lines, String nextScene) {
        super(id, title);
        this.lines = lines;
        this.nextScene = nextScene;
    }

    @Override
    public String play(GameState state, Scanner in) throws GameException {
        System.out.println("[" + getTitle() + "]");
        for (String l : lines) {
            System.out.println(l);
            // small pause simulation
            try { Thread.sleep(350); } catch (InterruptedException ignored) {}
        }
        System.out.println();
        return nextScene;
    }
}
