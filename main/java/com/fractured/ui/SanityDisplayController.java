package com.fractured.ui;

import com.fractured.model.Player;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SanityDisplayController {

    private final Player player;
    private final AtomicInteger lastDisplayedSanity = new AtomicInteger(Integer.MIN_VALUE);
    private final AtomicLong lastChangeTime = new AtomicLong(0L);
    private volatile boolean visible = false;

    private final long displayDurationMs = 3000; // 3 seconds

    public SanityDisplayController(Player p) {
        this.player = p;

        // When sanity changes, mark time & make visible (called from whichever thread modifies sanity)
        p.setSanityChangeListener(() -> {
            lastChangeTime.set(System.currentTimeMillis());
            visible = true; // volatile write is safe for readers
        });
    }

    // Call this from your main game loop
    public void tick() {
        int curr = player.getSanity();

        if (visible) {
            if (curr != lastDisplayedSanity.get()) {
                showSanityPopup(curr);
                lastDisplayedSanity.set(curr);
                lastChangeTime.set(System.currentTimeMillis());
            }

            if (System.currentTimeMillis() - lastChangeTime.get() > displayDurationMs) {
                hideSanityPopup();
                visible = false;
                lastDisplayedSanity.set(Integer.MIN_VALUE);
            }
        }
    }

    private void showSanityPopup(int s) {
        // replace with your UI rendering code later
        System.out.println("[Sanity] " + s);
    }

    private void hideSanityPopup() {
        System.out.println("[Sanity popup hidden]");
    }
}
