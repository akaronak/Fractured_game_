package com.fractured.acts;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.fractured.core.ChoiceScene;
import com.fractured.core.DialogueScene;
import com.fractured.core.GameEngine;
import com.fractured.core.GameState;
import com.fractured.core.Scene;
import com.fractured.model.Player;

/**
 * Act3Register
 *
 * Complete Act III registration wired to your existing Player API (getSanity/modifySanity)
 * and inventory (getInventory().add/remove/contains).
 *
 * Drop this file into: src/main/java/com/fractured/acts/Act3Register.java
 * Then in Main.java call:
 *   com.fractured.acts.Act3Register.registerAct3(engine);
 */
public final class Act3Register {

    private Act3Register() { }

    public static void registerAct3(GameEngine engine) {

        // --- Intro ---
        engine.register(new DialogueScene(
            "act3_intro",
            "Abandoned Passage",
            new String[] {
                "A narrow passage unwinds past a rusted grate and a black stair.",
                "You carry the notebook; its edges bite into your palm like a promise."
            },
            "act3_choice1"
        ));

        // Choice 1
        Map<String,String> ch1 = new LinkedHashMap<>();
        ch1.put("Descend the stair", "act3_descend");
        ch1.put("Follow the passage", "act3_follow");
        engine.register(new ChoiceScene(
            "act3_choice1",
            "First fork",
            "Which way do you go?",
            ch1
        ));

        // Descend: describe then an effect node that reduces sanity
        engine.register(new DialogueScene(
            "act3_descend",
            "Rusty Stair",
            new String[] {
                "You pick the stairs, each step groaning like a slow animal.",
                "The air lower down tastes like iron and old clocks."
            },
            "act3_descend_effect"
        ));

        engine.register(new Scene("act3_descend_effect", "Descent Effect") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    int cur = p.getSanity();
                    p.modifySanity(cur - 2);
                    System.out.println("(Sanity -2). Current sanity: " + p.getSanity());
                }
                return "act3_stairroom";
            }
        });

        engine.register(new DialogueScene(
            "act3_stairroom",
            "Stairroom",
            new String[] {
                "An iron door is bolted in the corner; faint light leaks around its edges.",
                "The notebook feels hot against your ribs."
            },
            "act3_stair_choice"
        ));

        // Stair choice: peek or search for light
        Map<String,String> chStair = new LinkedHashMap<>();
        chStair.put("Push the heavy door and look inside", "act3_peek");
        chStair.put("Search pockets for a light", "act3_search_light");
        engine.register(new ChoiceScene(
            "act3_stair_choice",
            "At the door",
            "What do you do?",
            chStair
        ));

        // If they peek, later we will penalize sanity if no lamp present
        engine.register(new DialogueScene(
            "act3_peek",
            "The Room Inside",
            new String[] {
                "Inside: a small study. A rusted mechanism ticks in the dark.",
                "You glimpse a folded paper on the desk with Clara's handwriting."
            },
            "act3_readpaper"
        ));

        // Search pockets for a light -> add 'lamp' to inventory if not present
        engine.register(new Scene("act3_search_light", "Search for Light") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    if (!p.getInventory().contains("lamp")) {
                        p.getInventory().add("lamp");
                        System.out.println("(Inventory + lamp)");
                    } else {
                        System.out.println("You already have a light.");
                    }
                }
                return "act3_stairroom"; // return to the stairroom to choose again
            }
        });

        // Reading the paper / choices there
        engine.register(new DialogueScene("act3_readpaper", "Paper Corner",
            new String[] { "The handwriting calls your name." }, "act3_readpaper_choice"));

        Map<String,String> chRead = new LinkedHashMap<>();
        chRead.put("Steal paper and flee", "act3_takepaper");
        chRead.put("Leave it and search the desk", "act3_searchdesk");
        engine.register(new ChoiceScene("act3_readpaper_choice", "Paper", "Do you take the paper?", chRead));

        // take paper: sanity -3 and add clara_note
        engine.register(new Scene("act3_takepaper", "Taken") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 3);
                    p.getInventory().add("clara_note");
                    System.out.println("(Sanity -3). Current sanity: " + p.getSanity());
                    System.out.println("(Inventory + clara_note)");
                }
                return "act3_afterpaper";
            }
        });

        engine.register(new DialogueScene("act3_searchdesk", "Search Desk",
            new String[] { "You find a cold brass key. It clicks nicely in your palm." }, "act3_afterpaper_search"));

        engine.register(new DialogueScene("act3_afterpaper_search", "After Desk",
            new String[] { "Pocketed the key." }, "act3_afterpaper"));

        engine.register(new DialogueScene("act3_afterpaper", "Leaving",
            new String[] { "You leave the room. The passage waits." }, "act3_corridor"));

        // Follow passage branch -> trap choices
        engine.register(new DialogueScene("act3_follow", "Narrow Passage",
            new String[] { "The passage tightens. You feel watched." }, "act3_trap_choice"));

        Map<String,String> chTrap = new LinkedHashMap<>();
        chTrap.put("Press on bravely", "act3_trap");
        chTrap.put("Try to go back", "act3_corridor");
        engine.register(new ChoiceScene("act3_trap_choice", "Tightness", "Do you press on?", chTrap));

        engine.register(new Scene("act3_trap", "A trap") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 4);
                    System.out.println("The floor shudders; you barely dodge falling gears.");
                    System.out.println("(Sanity -4). Current sanity: " + p.getSanity());
                }
                return "act3_corridor";
            }
        });

        // Common corridor node
        engine.register(new DialogueScene("act3_corridor", "Corridor",
            new String[] { "You are in a long corridor. A fork ahead glitters with faint signs of life." }, "act3_main_choice"));

        // Main choice
        Map<String,String> mainChoices = new LinkedHashMap<>();
        mainChoices.put("Enter the well-lit chamber", "act3_chamber");
        mainChoices.put("Sneak into the machinery room", "act3_machinery");
        mainChoices.put("Open the gate labeled 'CLARA'", "act3_clara_gate");
        engine.register(new ChoiceScene("act3_main_choice", "Decision", "Where do you go next?", mainChoices));

        // Chamber branch
        engine.register(new DialogueScene("act3_chamber", "Chamber",
            new String[] {
                "A circular chamber. Statues with hollow eyes line a platform.",
                "At the center: a locked box. It hums like a heart."
            }, "act3_box_choice"));

        Map<String,String> boxChoices = new LinkedHashMap<>();
        boxChoices.put("Open the box (risky)", "act3_box_open");
        boxChoices.put("Leave the box alone", "act3_box_leave");
        engine.register(new ChoiceScene("act3_box_choice", "The Box", "Do you open it?", boxChoices));

        // Opening box: heavy sanity loss but item added
        engine.register(new Scene("act3_box_open", "Box Open") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 12);
                    p.getInventory().add("crank");
                    System.out.println("Inside: a small brass crank and a faded photograph. A name: Clara.");
                    System.out.println("(Sanity -12). Current sanity: " + p.getSanity());
                    System.out.println("(Inventory + crank)");
                }
                return "act3_box_after";
            }
        });

        engine.register(new DialogueScene("act3_box_after", "After Box",
            new String[] { "You place the crank into your pack." }, "act3_end_branch"));

        engine.register(new DialogueScene("act3_box_leave", "Left Alone",
            new String[] { "You back away. Some things better remain sealed." }, "act3_end_branch"));

        // Machinery room
        engine.register(new DialogueScene("act3_machinery", "Machinery Room",
            new String[] { "Huge gears rotate slowly; steam hisses in a rhythm." }, "act3_machine_choice"));

        Map<String,String> machChoices = new LinkedHashMap<>();
        machChoices.put("Climb onto the gears", "act3_gear_risk");
        machChoices.put("Search control panel", "act3_panel_search");
        engine.register(new ChoiceScene("act3_machine_choice", "Machine", "What do you attempt?", machChoices));

        engine.register(new Scene("act3_panel_search", "Control Panel") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    if (!p.getInventory().contains("brass_key")) {
                        p.getInventory().add("brass_key");
                        System.out.println("Beneath grime you find a brass key with CLARA etched on it.");
                        System.out.println("(Inventory + brass_key)");
                    } else {
                        System.out.println("You search but find nothing new.");
                    }
                }
                return "act3_end_branch";
            }
        });

        engine.register(new Scene("act3_gear_risk", "Gears") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 14);
                    System.out.println("You slip between teeth of giants — you escape, bloodless but shaken.");
                    System.out.println("(Sanity -14). Current sanity: " + p.getSanity());
                }
                return "act3_end_branch";
            }
        });

        // Clara gate
        engine.register(new DialogueScene("act3_clara_gate", "CLARA Gate",
            new String[] { "An arch with the name CLARA burnt into it. A mechanism requires a key." }, "act3_gate_check"));

        // gate check uses inventory to decide
        engine.register(new Scene("act3_gate_check", "Gatecheck") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("brass_key")) {
                    p.getInventory().remove("brass_key");
                    System.out.println("[You use the brass key. It fits.]");
                    return "act3_gate_open";
                } else {
                    System.out.println("[The gate is locked. You feel the Lapati mocking you.]");
                    // penalize a bit for trying to force the gate
                    if (p != null) {
                        p.modifySanity(- 10);
                        System.out.println("(Sanity -10). Current sanity: " + p.getSanity());
                    }
                    return "act3_gate_locked";
                }
            }
        });

        engine.register(new DialogueScene("act3_gate_open", "Gate Open",
            new String[] { "The gate opens. Beyond: a small room with a wooden cradle." }, "act3_cradle"));

        engine.register(new DialogueScene("act3_gate_locked", "Locked",
            new String[] { "You try to force it, but the mechanism snaps and you stagger." }, "act3_end_branch"));

        engine.register(new DialogueScene("act3_cradle", "Cradle",
            new String[] {
                "Inside the cradle: a music box. A single note plays; inside a photograph of Clara and you, older.",
                "A memory — or a trap."
            },
            "act3_end_branch"));

        // common end branch
        engine.register(new DialogueScene("act3_end_branch", "Aftermath",
            new String[] {
                "You stagger out: pieces clutched, empty hands, or secrets pocketed.",
                "A door far along the corridor slides open. A cold breeze carries a distant promise."
            },
            "act3_end"));

        // Act3 end -> link to Act4 intro (change if different)
        engine.register(new DialogueScene("act3_end", "Act 3 — End",
            new String[] {
                "The corridor opens onto an unfamiliar hall.",
                "The notebook trembles; you are closer to Clara and to the Lapati's source."
            },"act4_intro" // point to act4_intro when Act4 exists; otherwise set to null
        ));

        // safety
        engine.register(new DialogueScene("act3_missing", "No Scene",
            new String[] { "You stand still. Something is wrong." }, null));
    }
}
