package com.fractured.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fractured.ui.SanityDisplayController;

import com.fractured.db.SaveDAO;
import com.fractured.threads.Lapati;

public class GameEngine {
    private final Map<String, Scene> scenes = new HashMap<>();
    private final GameState state;
    private final SaveDAO saveDAO;
    private final Lapati lapati;
    private final SanityDisplayController sanityUI;

    private final ExecutorService es = Executors.newSingleThreadExecutor();

    public GameEngine(GameState state, SaveDAO saveDAO) {
        this.state = state;
        this.saveDAO = saveDAO;
        this.lapati = new Lapati(state);
        this.sanityUI = new SanityDisplayController(state.getPlayer());

    }

    public void register(Scene s){ scenes.put(s.getId(), s); }

    /**
     * Quick diagnostic helper used by Main to verify registration of scenes.
     */
    public boolean hasScene(String id) { return scenes.containsKey(id); }

    public void start() {
        // start lapati thread
        es.submit(lapati);

        try (Scanner in = new Scanner(System.in)) {
            String current = state.getCurrentScene();
            while (current != null) {
                Scene s = scenes.get(current);
                if (s == null) {
                    System.out.println("No scene: " + current);
                    break;
                }
                try {
    
                   
                    String next = s.play(state, in);
                    // debug: report next scene id returned by the scene
                    System.out.println("[DEBUG] scene '" + s.getId() + "' returned next='" + next + "'");

    // small autosave after every scene — handle checked exceptions here
    try {
        saveDAO.saveGame("autosave", state);
    } catch (Exception e) {
        // log but do not stop the game
        System.err.println("Warning: failed to autosave: " + e.getMessage());
        // optional: e.printStackTrace();
    }

    current = next;
    synchronized (state) {
        state.setCurrentScene(current);
    }

    // termination conditions
    if (state.getPlayer().getSanity() <= 0) {
        sanityUI.tick();
        System.out.println("You slipped into the darkness. Game over.");
        break;
    }

} catch (GameException ge) {
    System.out.println("Error: " + ge.getMessage());
}

            }
        } finally {
            // stop lapati and shutdown
            lapati.stop();
            es.shutdownNow();
        }
    }
}
