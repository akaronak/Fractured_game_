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
 * Act5Register
 *
 * Registers Act V of the game. All scene IDs are prefixed with "act5_".
 * Sanity changes and inventory mutations are applied using small effect Scenes
 * which access the GameState and Player (getSanity/modifySanity and getInventory()).
 *
 * Drop into: src/main/java/com/fractured/acts/Act5Register.java
 * Then in Main.java call:
 *   com.fractured.acts.Act5Register.registerAct5(engine);
 */
public final class Act5Register {

    private Act5Register() { }

    public static void registerAct5(GameEngine engine) {

        // --- Intro ---
        engine.register(new DialogueScene(
            "act5_intro",
            "The Hollow Atrium",
            new String[] {
                "You emerge into a cavernous atrium where old banners hang like tired ghosts.",
                "A single skylight pours a narrow beam onto a circular pattern on the floor."
            },
            "act5_entry_choice"
        ));

        // Entry choices
        Map<String,String> entry = new LinkedHashMap<>();
        entry.put("Walk into the beam", "act5_beam");
        entry.put("Examine the banners", "act5_banners");
        entry.put("Check your inventory", "act5_check_inventory");
        engine.register(new ChoiceScene("act5_entry_choice", "Entrance", "What do you do?", entry));

        // Check inventory scene: prints inventory and loops back
        engine.register(new Scene("act5_check_inventory", "Inventory") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                System.out.println("--- Inventory ---");
                if (p != null && p.getInventory().size() > 0) {
                    System.out.println(p.getInventory());
                } else {
                    System.out.println("(empty)");
                }
                System.out.println("-----------------");
                return "act5_entry_choice";
            }
        });

        // Beam: reveals a hidden inscription -> small sanity reward for clarity
        engine.register(new DialogueScene("act5_beam", "The Beam",
            new String[] {
                "The light strikes the floor and the circle's hidden runes glow faintly.",
                "You can make out a word etched into the stone: 'Remember'."
            }, "act5_beam_effect"));

        engine.register(new Scene("act5_beam_effect", "Beam Effect") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(+ 2);
                    System.out.println("(Sanity +2). Current sanity: " + p.getSanity());
                }
                return "act5_main_hub";
            }
        });

        // Banners: choose to read one or cut a strip (inventory)
        engine.register(new DialogueScene("act5_banners", "Banners",
            new String[] { "The banners show fragmented family crests and dates. One banner looks recent." }, "act5_banner_choice"));

        Map<String,String> bannerChoices = new LinkedHashMap<>();
        bannerChoices.put("Read the recent banner", "act5_read_banner");
        bannerChoices.put("Cut a strip of fabric for later", "act5_cut_strip");
        engine.register(new ChoiceScene("act5_banner_choice", "Banner", "What do you do?", bannerChoices));

        engine.register(new Scene("act5_read_banner", "Read Banner") {
            @Override
            public String play(GameState state, Scanner in) {
                System.out.println("The banner reads: 'For Clara. For mercy.' A date is sewn into the hem.");
                return "act5_main_hub";
            }
        });

        engine.register(new Scene("act5_cut_strip", "Cut Strip") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && !p.getInventory().contains("cloth_strip")) {
                    p.getInventory().add("cloth_strip");
                    System.out.println("You cut a strip of cloth and tuck it away. (Inventory + cloth_strip)");
                } else {
                    System.out.println("You already have a strip.");
                }
                return "act5_main_hub";
            }
        });

        // Main hub after initial exploration
        engine.register(new DialogueScene("act5_main_hub", "Atrium Hub",
            new String[] {
                "From the beam three doors radiate: North (silence), East (machinery), West (a locked glass chamber).",
                "You feel the weight of choice — and the Lapati hums in the walls." }, "act5_main_choice"));

        Map<String,String> main = new LinkedHashMap<>();
        main.put("North: the Silent Corridor", "act5_north");
        main.put("East: the Machinery Annex", "act5_east");
        main.put("West: the Glass Chamber", "act5_west_gate");
        main.put("Sit and try to remember Clara", "act5_memory_explore");
        main.put("Approach and prepare the central device", "act5_prepare_device");
        engine.register(new ChoiceScene("act5_main_choice", "Choices", "Which path?", main));

       // Debug Device Check Scene -------------------------
engine.register(new Scene("act5_prepare_device_check", "Device Check") {
    @Override
    public String play(GameState state, Scanner in) {
        Player p = state.getPlayer();
        System.out.println("=== device_check entered ===");
        if (p == null) {
            System.out.println("Player null -> back to hub");
            return "act5_main_hub";
        }

        System.out.println("Current Inventory: " + p.getInventory());
        System.out.println("Current Sanity: " + p.getSanity());

        boolean hasCircuit = p.getInventory().contains("circuit_part");
        boolean hasMemory  = p.getInventory().contains("memory_token");
        boolean hasClarity = p.getInventory().contains("clarity_token");
        boolean hasLocket  = p.getInventory().contains("silver_locket");

        System.out.println("flags: circuit=" + hasCircuit +
                           " memory=" + hasMemory +
                           " clarity=" + hasClarity +
                           " locket=" + hasLocket);

        if (hasCircuit || hasMemory || hasClarity || hasLocket) {
            System.out.println("Proceeding to act5_prepare_device");
            return "act5_prepare_device";
        } else {
            System.out.println(" Missing required items; routing back to hub");
            return "act5_main_hub";
        }
    }
});

    

        // North branch: Silent Corridor (dangerous mind tests)
        engine.register(new DialogueScene("act5_north", "Silent Corridor",
            new String[] { "The corridor is almost soundless. Echoes die quickly here." }, "act5_north_choice"));

        Map<String,String> northChoices = new LinkedHashMap<>();
        northChoices.put("Walk calmly through", "act5_north_calm");
        northChoices.put("Run and push forward", "act5_north_run");
        engine.register(new ChoiceScene("act5_north_choice", "Silent Tests", "How proceed?", northChoices));

        engine.register(new Scene("act5_north_calm", "Calm Walk") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 2);
                    System.out.println("The silence presses. (Sanity -2). Current sanity: " + p.getSanity());
                }
                return "act5_main_hub";
            }
        });

        engine.register(new Scene("act5_north_run", "Run") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 6);
                    System.out.println("Panic rips through you. (Sanity -6). Current sanity: " + p.getSanity());
                }
                return "act5_main_hub";
            }
        });

        // East branch: Machinery Annex (tools and puzzles)
        engine.register(new DialogueScene("act5_east", "Machinery Annex",
            new String[] { "Pipes and levers arc across the room. A console waits covered in grime." }, "act5_east_choice"));

        Map<String,String> eastChoices = new LinkedHashMap<>();
        eastChoices.put("Try the console", "act5_console_try");
        eastChoices.put("Search for usable parts", "act5_search_parts");
        engine.register(new ChoiceScene("act5_east_choice", "Machinery", "What to do?", eastChoices));

        engine.register(new Scene("act5_console_try", "Console") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                // console requires oil_can to restore; otherwise causes sanity loss
                if (p != null && p.getInventory().contains("oil_can")) {
                    System.out.println("You oil the gears and the console blinks to life.");
                    // unlock a subpanel item
                    if (!p.getInventory().contains("circuit_part")) {
                        p.getInventory().add("circuit_part");
                        System.out.println("(Inventory + circuit_part)");
                    }
                } else {
                    p.modifySanity(-5);
                    System.out.println("You fiddle with dead switches. A shock jolts you. (Sanity -5). Current sanity: " + p.getSanity());
                }
                return "act5_main_hub";
            }
        });

        engine.register(new Scene("act5_search_parts", "Search Parts") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && !p.getInventory().contains("circuit_part")) {
                    p.getInventory().add("circuit_part");
                    System.out.println("You pry a small circuit from a panel. (Inventory + circuit_part)");
                } else {
                    System.out.println("You find nothing else of use.");
                }
                return "act5_main_hub";
            }
        });

        // West: Glass Chamber (locked) -> requires a token: silver_locket from Act4
        engine.register(new DialogueScene("act5_west_gate", "Glass Chamber",
            new String[] { "A glass chamber stands locked. Its inscription reads: 'For her, only the heart.'" }, "act5_west_check"));

        engine.register(new Scene("act5_west_check", "Gate Check") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("silver_locket")) {
                    // use the locket (optional: consume or keep)
                    System.out.println("The locket warms in your hand and the glass slides open.");
                    return "act5_glass_inside";
                } else {
                    System.out.println("The chamber refuses you. You need something that belongs to her.");
                    // small sanity penalty for being denied
                    if (p != null) {
                        p.modifySanity(- 3);
                        System.out.println("(Sanity -3). Current sanity: " + p.getSanity());
                    }
                    return "act5_main_hub";
                }
            }
        });

        engine.register(new DialogueScene("act5_glass_inside", "Inside the Glass",
            new String[] {
                "Inside the chamber: a cradle of glass with a small carved music-box figurine.",
                "A note inside reads: 'If you want her, you must remember why you left.'"
            }, "act5_glass_choice"));

        Map<String,String> glassChoices = new LinkedHashMap<>();
        glassChoices.put("Take the figurine", "act5_take_figurine");
        glassChoices.put("Read the note again", "act5_read_note");
        engine.register(new ChoiceScene("act5_glass_choice", "Glass", "What do you do?", glassChoices));

        engine.register(new Scene("act5_take_figurine", "Take Figurine") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && !p.getInventory().contains("figurine")) {
                    p.getInventory().add("figurine");
                    System.out.println("You take the figurine. It hums faintly. (Inventory + figurine)");
                } else {
                    System.out.println("You already took it.");
                }
                return "act5_main_hub";
            }
        });

        engine.register(new Scene("act5_read_note", "Read Note") {
            @Override
            public String play(GameState state, Scanner in) {
                System.out.println("The note says: 'Memory is a key. Three steps are truth.'");
                return "act5_main_hub";
            }
        });

        // Memory exploration: player can attempt to recall Clara (risky, but important)
        engine.register(new Scene("act5_memory_explore", "Remembering") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                System.out.println("You close your eyes and comb memory for a face.");
                if (p != null) {
                    // if player has 'clara_note' or 'child_drawing', memory is kinder
                    if (p.getInventory().contains("clara_note") || p.getInventory().contains("child_drawing")) {
                        p.modifySanity(+ 4);
                        System.out.println("A bright memory surfaces. (Sanity +4). Current sanity: " + p.getSanity());
                        // reveal a clue token
                        if (!p.getInventory().contains("memory_token")) {
                            p.getInventory().add("memory_token");
                            System.out.println("(Inventory + memory_token)");
                        }
                    } else {
                        p.modifySanity(- 5);
                        System.out.println("Shadows rush the edges of your mind. (Sanity -5). Current sanity: " + p.getSanity());
                    }
                }
                return "act5_main_hub";
            }
        });

        // Final puzzle: combining circuit_part and memory_token to power the central device
        engine.register(new DialogueScene("act5_prepare_device", "Device Prep",
            new String[] { "A dais holds a broken device with two empty sockets: one electrical, one token-shaped." }, "act5_device_choice"));

        Map<String,String> deviceChoices = new LinkedHashMap<>();
        deviceChoices.put("Insert circuit part", "act5_insert_circuit");
        deviceChoices.put("Insert memory token", "act5_insert_token");
        deviceChoices.put("Do both at once (if you have both)", "act5_insert_both");
        engine.register(new ChoiceScene("act5_device_choice", "Device", "How do you operate the device?", deviceChoices));

        engine.register(new Scene("act5_insert_circuit", "Circuit") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("circuit_part")) {
                    p.getInventory().remove("circuit_part");
                    System.out.println("The device sparks but does not fully awaken.");
                    return "act5_device_partial";
                } else {
                    System.out.println("You don't have a circuit part.");
                    return "act5_main_hub";
                }
            }
        });

        engine.register(new Scene("act5_insert_token", "Token") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("memory_token")) {
                    p.getInventory().remove("memory_token");
                    System.out.println("The device vibrates and a faint image forms in the air.");
                    return "act5_device_partial";
                } else {
                    System.out.println("You don't have a memory token.");
                    return "act5_main_hub";
                }
            }
        });

        engine.register(new Scene("act5_insert_both", "Both") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                boolean ok = true;
                if (p != null) {
                    if (!p.getInventory().contains("circuit_part") || !p.getInventory().contains("memory_token")) ok = false;
                    if (ok) {
                        p.getInventory().remove("circuit_part");
                        p.getInventory().remove("memory_token");
                        System.out.println("The device roars to life. A clear image fills the air: Clara, smiling.");
                        // reward: sanity boost and new item
                        p.modifySanity(+6);
                        if (!p.getInventory().contains("clarity_token")) {
                            p.getInventory().add("clarity_token");
                        }
                        System.out.println("(Sanity +6). Current sanity: " + p.getSanity());
                        System.out.println("(Inventory + clarity_token)");
                        return "act5_device_full";
                    }
                }
                System.out.println("You don't have the required items to do both.");
                return "act5_main_hub";
            }
        });

        engine.register(new DialogueScene("act5_device_partial", "Device — Partial",
            new String[] { "The device whirs and shows a fragment of an old photograph: a child's hand in yours." }, "act5_main_hub"));

        engine.register(new DialogueScene("act5_device_full", "Device — Full",
            new String[] { "The device reveals a recorded memory: Clara laughing, a name spoken softly: 'Forgive'." }, "act5_end_branch"));

        // device end branch
        engine.register(new DialogueScene("act5_end_branch", "After Device",
            new String[] { "A corridor opens where darkness had been. You step forward, carrying the new certainty." }, "act5_end"));

        // final act scene (point to act6_intro)
        engine.register(new DialogueScene("act5_end", "Act 5 — End",
            new String[] { "You move toward the next threshold. The story tightens; the Lapati waits." 

            },"act6_intro"));

        // safety scene
        engine.register(new DialogueScene("act5_no_scene", "No Scene",
            new String[] { "Nothing else continues." }, null));
    }
}
