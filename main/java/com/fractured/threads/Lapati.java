package com.fractured.threads;

import com.fractured.core.GameState;
import com.fractured.model.Player;

public class Lapati implements Runnable {
    private final GameState state;
    private volatile boolean running = true;

    public Lapati(GameState state) { this.state = state; }

    public void stop() { running = false; }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(15000); // whisper every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            synchronized (state) {
                Player p = state.getPlayer();
                if (p == null) continue;
                // whisper effect: lower sanity by 1-5 randomly
                int drop = 1 + (int)(Math.random()*5);
                p.modifySanity(-drop);
                System.out.println("[Lapati whispers. Sanity -" + drop + ". Current sanity: " + p.getSanity() + "]");
            }
        }
    }
}
