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
 * Act4Register
 *
 * Drop into: src/main/java/com/fractured/acts/Act4Register.java
 * Then call: Act4Register.registerAct4(engine) from Main.
 *
 * Act 4 continues the story, wires inventory + sanity changes,
 * and routes to act5_intro at the end.
 */
public final class Act4Register {

    private Act4Register() { }

    public static void registerAct4(GameEngine engine) {

        // ---- Intro ----
        engine.register(new DialogueScene(
            "act4_intro",
            "The Hall of Echoes",
            new String[] {
                "You step into a long hall lined with mirrors and rusted vents.",
                "Every footstep multiplies; every whisper seems answered twice.",
                "The notebook trembles in your hand like a heart in a jar."
            },
            "act4_first_choice"
        ));

        // ---- First choice: where to investigate ----
        Map<String,String> first = new LinkedHashMap<>();
        first.put("Follow the mirrored reflections", "act4_mirrors");
        first.put("Explore the vent shafts", "act4_vents");
        first.put("Open the notebook and search notes", "act4_notebook");
        engine.register(new ChoiceScene(
            "act4_first_choice",
            "Hallways",
            "Which way do you go?",
            first
        ));

        // ---- Mirrors branch ----
        engine.register(new DialogueScene(
            "act4_mirrors",
            "Mirrored Hall",
            new String[] {
                "Mirrors stretch on either side; some show spaces that don't exist.",
                "One mirror shows a child standing behind you — Clara — then blinks away."
            },
            "act4_mirror_choice"
        ));

        Map<String,String> mirrorChoices = new LinkedHashMap<>();
        mirrorChoices.put("Touch the glass", "act4_touch_glass");
        mirrorChoices.put("Break the mirror", "act4_break_mirror");
        mirrorChoices.put("Step past the reflection", "act4_past_reflection");
        engine.register(new ChoiceScene(
            "act4_mirror_choice",
            "Reflections",
            "Do you interact with the mirror?",
            mirrorChoices
        ));

        // touch => small sanity hit but maybe reveals clue
        engine.register(new Scene("act4_touch_glass", "Touch") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 3);
                    // reveal a faint etching — add 'mirror_etchtag' if not present
                    if (!p.getInventory().contains("mirror_etchtag")) {
                        p.getInventory().add("mirror_etchtag");
                        System.out.println("(Sanity -3). Current sanity: " + p.getSanity());
                        System.out.println("(Inventory + mirror_etchtag)");
                    } else {
                        System.out.println("(Sanity -3). Current sanity: " + p.getSanity());
                    }
                }
                return "act4_after_reflection";
            }
        });

        // break => heavy cost but forces a new path
        engine.register(new Scene("act4_break_mirror", "Smash") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 10);
                    System.out.println("The glass shatters and a thousand echoes scream.");
                    System.out.println("(Sanity -10). Current sanity: " + p.getSanity());
                }
                return "act4_after_reflection";
            }
        });

        engine.register(new DialogueScene(
            "act4_past_reflection",
            "Past the Glass",
            new String[] {
                "You ignore the illusions and walk on. The mirrors watch you silently."
            },
            "act4_after_reflection"
        ));

        engine.register(new DialogueScene(
            "act4_after_reflection",
            "After Reflection",
            new String[] {
                "A narrow door appears where the mirror's frame used to be.",
                "A small keyhole gleams in the lock — oddly familiar."
            },
            "act4_keyhole_choice"
        ));

        Map<String,String> keyholeChoices = new LinkedHashMap<>();
        keyholeChoices.put("Try the small key (if you have it)", "act4_use_small_key");
        keyholeChoices.put("Leave it and continue down the hall", "act4_continue_hall");
        engine.register(new ChoiceScene(
            "act4_keyhole_choice",
            "Keyhole",
            "What do you do?",
            keyholeChoices
        ));

        // use small key => check inventory
        engine.register(new Scene("act4_use_small_key", "Use Small Key") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("crank")) {
                    // use crank as key (consume it, open secret)
                    p.getInventory().remove("crank");
                    System.out.println("The crank fits into the tiny lock and turns. A secret passage opens.");
                    return "act4_secret_passage";
                } else {
                    System.out.println("You don't have the correct item to turn the lock.");
                    return "act4_continue_hall";
                }
            }
        });

        engine.register(new DialogueScene(
            "act4_secret_passage",
            "Secret Passage",
            new String[] {
                "A spiral staircase descends beyond the frame — narrow, slick with age.",
                "A faded child's drawing is stuck to the wall: a house and the word 'home'."
            },
            "act4_secret_choice"
        ));

        Map<String,String> secretChoices = new LinkedHashMap<>();
        secretChoices.put("Descend the spiral", "act4_descend_spiral");
        secretChoices.put("Take the drawing", "act4_take_drawing");
        engine.register(new ChoiceScene(
            "act4_secret_choice",
            "Spiral",
            "Choose an action",
            secretChoices
        ));

        engine.register(new Scene("act4_take_drawing", "Take Drawing") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && !p.getInventory().contains("child_drawing")) {
                    p.getInventory().add("child_drawing");
                    System.out.println("(Inventory + child_drawing)");
                } else {
                    System.out.println("You already have the drawing.");
                }
                return "act4_descend_spiral";
            }
        });

        engine.register(new DialogueScene(
            "act4_descend_spiral",
            "Down the Spiral",
            new String[] {
                "The spiral spits you out into a low vaulted room. Something hums here — a low mechanical lullaby."
            },
            "act4_vault_choice"
        ));

        // ---- Vents branch ----
        engine.register(new DialogueScene(
            "act4_vents",
            "Vent Shafts",
            new String[] {
                "You pry open a maintenance vent and crawl inside the metallic throat.",
                "Pipes clatter; a shadow moves far ahead."
            },
            "act4_vent_choice"
        ));

        Map<String,String> vents = new LinkedHashMap<>();
        vents.put("Crawl to the source of the clatter", "act4_vent_source");
        vents.put("Plant a noise-maker and retreat", "act4_plant_noise");
        engine.register(new ChoiceScene(
            "act4_vent_choice",
            "Vent Crawl",
            "How do you proceed?",
            vents
        ));

        engine.register(new Scene("act4_vent_source", "Source") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    // small sanity tweak depending on whether lamp present
                    if (!p.getInventory().contains("lamp")) {
                        p.modifySanity(- 5);
                        System.out.println("(You fumble in the dark. Sanity -5). Current sanity: " + p.getSanity());
                    } else {
                        System.out.println("(Your lamp helps you see a small crate ahead.)");
                    }
                }
                return "act4_crate_choice";
            }
        });

        engine.register(new DialogueScene(
            "act4_plant_noise",
            "Plant",
            new String[] {
                "You leave a small mechanical noise-maker. It will draw attention away — if it works."
            },
            "act4_continue_hall"
        ));

        // crate choices
        Map<String,String> crateChoices = new LinkedHashMap<>();
        crateChoices.put("Open the crate", "act4_open_crate");
        crateChoices.put("Leave it", "act4_continue_hall");
        engine.register(new ChoiceScene(
            "act4_crate_choice",
            "Crate",
            "Do you open it?",
            crateChoices
        ));

        engine.register(new Scene("act4_open_crate", "Open Crate") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    if (!p.getInventory().contains("oil_can")) {
                        p.getInventory().add("oil_can");
                        System.out.println("Inside the crate: an oil can and a tarnished medallion.");
                        System.out.println("(Inventory + oil_can)");
                    } else {
                        System.out.println("The crate is empty now.");
                    }
                }
                return "act4_continue_hall";
            }
        });

        // ---- Notebook branch ----
        engine.register(new Scene("act4_notebook", "Open Notebook") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    System.out.println("You flip the notebook open. A margin note shivers into clarity: 'three steps'.");
                    // small sanity increase because understanding briefly helps
                    p.modifySanity(+ 1);
                    System.out.println("(Sanity +1). Current sanity: " + p.getSanity());
                }
                return "act4_continue_hall";
            }
        });

        // ---- Continue (common) ----
        engine.register(new DialogueScene(
            "act4_continue_hall",
            "Hall Continues",
            new String[] {
                "You move deeper into the hall. The echoes thin, and a heavy door looms ahead."
            },
            "act4_heavydoor_choice"
        ));

        // heavy door choice: use oil to loosen hinges, or force
        Map<String,String> heavyChoices = new LinkedHashMap<>();
        heavyChoices.put("Use oil can on hinges (if you have it)", "act4_use_oil_check");
        heavyChoices.put("Force the door", "act4_force_door");
        heavyChoices.put("Look for another route", "act4_look_around");
        engine.register(new ChoiceScene(
            "act4_heavydoor_choice",
            "Heavy Door",
            "How will you deal with the door?",
            heavyChoices
        ));

        // check oil
        engine.register(new Scene("act4_use_oil_check", "Use Oil Check") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("oil_can")) {
                    p.getInventory().remove("oil_can");
                    System.out.println("You oil the hinges; the door groans open smoothly.");
                    return "act4_inner_hall";
                } else {
                    System.out.println("You don't have anything to lubricate the hinges with.");
                    return "act4_force_door";
                }
            }
        });

        engine.register(new Scene("act4_force_door", "Force Door") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 6);
                    System.out.println("You force the door. Metal screams and your shoulder aches.");
                    System.out.println("(Sanity -6). Current sanity: " + p.getSanity());
                }
                return "act4_inner_hall";
            }
        });

        engine.register(new DialogueScene(
            "act4_look_around",
            "Look Around",
            new String[] {
                "You circle the room and find a rusted ladder leading up and a service corridor to the right."
            },
            "act4_inner_hall"
        ));

        // inner hall: lead to a memory room or a puzzle
        engine.register(new DialogueScene(
            "act4_inner_hall",
            "Inner Hall",
            new String[] {
                "Inside: tiles of faded color and a central dais with an engraved pattern: three steps and a circle."
            },
            "act4_dais_choice"
        ));

        Map<String,String> daisChoices = new LinkedHashMap<>();
        daisChoices.put("Step on the three tiles (in order: left, center, right)", "act4_step_tiles");
        daisChoices.put("Inspect the dais for a mechanism", "act4_inspect_dais");
        daisChoices.put("Leave the dais and search the room", "act4_search_room");
        engine.register(new ChoiceScene(
            "act4_dais_choice",
            "The Dais",
            "How do you approach the dais?",
            daisChoices
        ));

        // ---- ADDED: inspect & search scenes to prevent missing-IDs ----
        engine.register(new Scene("act4_inspect_dais", "Inspect Dais") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    System.out.println("You crouch and inspect the dais closely. There is a faint seam and a tiny gear slot.");
                    // small sanity tweak for close inspection
                    p.modifySanity(-2);
                    // reveal crank if player doesn't have it
                    if (!p.getInventory().contains("crank")) {
                        p.getInventory().add("crank");
                        System.out.println("You pry out a small crank from a hidden recess. (Inventory + crank)");
                    } else {
                        System.out.println("You find nothing new beyond the seam.");
                    }
                    System.out.println("(Sanity -2). Current sanity: " + p.getSanity());
                }
                // return to dais choice so player can act on new info
                return "act4_dais_choice";
            }
        });

        engine.register(new Scene("act4_search_room", "Search Room") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    System.out.println("You search the nearby alcoves and tiles for anything useful.");
                    // small chance to find a note or item — deterministic for now: give clara_note if missing
                    if (!p.getInventory().contains("clara_note")) {
                        p.getInventory().add("clara_note");
                        System.out.println("You find a crumpled note with Clara's name. (Inventory + clara_note)");
                    } else if (!p.getInventory().contains("crank")) {
                        p.getInventory().add("crank");
                        System.out.println("Under a loose tile you pull out a small crank. (Inventory + crank)");
                    } else {
                        System.out.println("You find nothing else of use.");
                    }
                }
                return "act4_dais_choice";
            }
        });

        // stepping tiles: success if player has clue from notebook or drawing
        engine.register(new Scene("act4_step_tiles", "Three Steps") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                boolean success = false;
                if (p != null) {
                    if (p.getInventory().contains("child_drawing") || p.getInventory().contains("mirror_etchtag")) {
                        success = true;
                    }
                    if (success) {
                        System.out.println("The three tiles click into place and the dais hums. A hidden compartment opens.");
                        p.getInventory().add("silver_locket");
                        System.out.println("(Inventory + silver_locket)");
                        return "act4_dais_success";
                    } else {
                        p.modifySanity(- 8);
                        System.out.println("The tiles misalign. A painful shock ripples through you.");
                        System.out.println("(Sanity -8). Current sanity: " + p.getSanity());
                        return "act4_dais_fail";
                    }
                }
                return "act4_dais_fail";
            }
        });

        engine.register(new DialogueScene(
            "act4_dais_success",
            "Dais — Success",
            new String[] {
                "Inside the compartment: a small silver locket with a faded photo of Clara as a child.",
                "You clutch it, breath shallow."
            },
            "act4_after_dais"
        ));

        engine.register(new DialogueScene(
            "act4_dais_fail",
            "Dais — Fail",
            new String[] {
                "You stagger as the dais resets. The hall feels colder. You catch your breath and move on."
            },
            "act4_after_dais"
        ));

        engine.register(new DialogueScene(
            "act4_after_dais",
            "After Dais",
            new String[] {
                "Pieces are forming a pattern. The locket pulses faintly in your hand."
            },
            "act4_prepare_confront"
        ));

        // prepare for confrontation: brief rest choices
        Map<String,String> prepare = new LinkedHashMap<>();
        prepare.put("Rest a moment and collect yourself", "act4_rest");
        prepare.put("Move forward immediately", "act4_confront");
        engine.register(new ChoiceScene(
            "act4_prepare_confront",
            "Prepare",
            "How do you proceed?",
            prepare
        ));

        engine.register(new Scene("act4_rest", "Rest") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    // small sanity restore if holding locket
                    if (p.getInventory().contains("silver_locket")) {
                        p.modifySanity(+ 3);
                        System.out.println("(Sanity +3). Current sanity: " + p.getSanity());
                    } else {
                        System.out.println("You rest but the unease persists.");
                    }
                }
                return "act4_confront";
            }
        });

        // confrontation scene uses inventory to vary outcomes
        engine.register(new DialogueScene(
            "act4_confront",
            "Confrontation",
            new String[] {
                "A vast chamber opens. Machinery and puppets hang from the ceiling.",
                "At the center sits a figure and a panel with three sockets — one matches your silver locket."
            },
            "act4_confront_choice"
        ));

        Map<String,String> confront = new LinkedHashMap<>();
        confront.put("Insert the silver locket", "act4_insert_locket");
        confront.put("Try to pull the figure down", "act4_pull_figure");
        confront.put("Speak softly, ask for Clara", "act4_speak_clara");
        engine.register(new ChoiceScene(
            "act4_confront_choice",
            "Face the Figure",
            "What do you do?",
            confront
        ));

        engine.register(new Scene("act4_insert_locket", "Insert Locket") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null && p.getInventory().contains("silver_locket")) {
                    p.getInventory().remove("silver_locket");
                    System.out.println("The locket slides into the socket. Gears turn. A hollow voice sings Clara's lullaby.");
                    return "act4_reveal";
                } else {
                    System.out.println("You don't have the locket. The panel remains dark.");
                    p.modifySanity(-5);
                    System.out.println("(Sanity -5). Current sanity: " + p.getSanity());
                    return "act4_confront";
                }
            }
        });

        engine.register(new Scene("act4_pull_figure", "Pull") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    p.modifySanity(- 12);
                    System.out.println("You lunge to pull the figure. It resists and you are thrown back, rattled.");
                    System.out.println("(Sanity -12). Current sanity: " + p.getSanity());
                }
                return "act4_end_branch";
            }
        });

        engine.register(new Scene("act4_speak_clara", "Speak") {
            @Override
            public String play(GameState state, Scanner in) {
                Player p = state.getPlayer();
                if (p != null) {
                    // speaking gives a small clarity if you have clara_note
                    if (p.getInventory().contains("clara_note")) {
                        p.modifySanity(+ 2);
                        System.out.println("A small warmth. A whisper: 'You were close.'");
                        System.out.println("(Sanity +2). Current sanity: " + p.getSanity());
                    } else {
                        System.out.println("Only static answers your question.");
                    }
                }
                return "act4_end_branch";
            }
        });

        engine.register(new DialogueScene(
            "act4_reveal",
            "Reveal",
            new String[] {
                "The machinery peels back to reveal a small sleeping alcove. A child-shaped blanket stirs.",
                "A voice: 'You found me, Elias.' The name lands like a stone and everything tilts."
            },
            "act4_end_branch"
        ));

        // wrap-up for Act 4
        engine.register(new DialogueScene(
            "act4_end_branch",
            "Aftermath",
            new String[] {
                "Something has changed. The Hall of Echoes is quieter now; pieces have moved into place.",
                "You step toward the next threshold, the last things heavy in your pockets."
            },"act5_intro" 
        ));

        // safety node
        engine.register(new DialogueScene(
            "act4_no_scene",
            "No Scene",
            new String[] { "Silence — nothing continues here." },
            null
        ));
    }
}
