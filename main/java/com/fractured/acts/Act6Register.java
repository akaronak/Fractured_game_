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
 * Act6Register
 *
 * Registers Act VI of the game. Scene IDs use the "act6_" prefix.
 * Mutates Player sanity via get/set and inventory via getInventory().
 *
 * Drop into: src/main/java/com/fractured/acts/Act6Register.java
 * Then in Main.java call:
 *   com.fractured.acts.Act6Register.registerAct6(engine);
 */
public final class Act6Register {

    private Act6Register() { }

    public static void registerAct6(GameEngine engine) {

        // --- Intro ---
        engine.register(new DialogueScene(
            "act6_intro",
            "The Core",
            new String[] {
                "At last you stand before the Core — a spherical machine of glass, brass and old hope.",
                "The Lapati is louder here; it feels like fingers on the back of your neck."
            },
            "act6_choice_entry"
        ));

        // Entry choice: examine core, check inventory, or call out
        Map<String,String> entry = new LinkedHashMap<>();
        entry.put("Examine the Core closely", "act6_examine_core");
        entry.put("Check inventory for tools", "act6_check_inventory");
        entry.put("Call out to Clara", "act6_call_clara");
        engine.register(new ChoiceScene("act6_choice_entry", "Approach", "What do you do?", entry));

        engine.register(new Scene("act6_check_inventory","Inventory Check"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                System.out.println("-- Inventory --");
                if(p!=null && p.getInventory().size()>0) System.out.println(p.getInventory());
                else System.out.println("(empty)");
                System.out.println("---------------");
                return "act6_choice_entry";
            }
        });

        // Calling Clara: costs sanity but may trigger a response
        engine.register(new Scene("act6_call_clara","Call"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                if(p!=null){
                    p.modifySanity(-5);
                    System.out.println("You call: 'Clara!'. The Core answers with a thousand tiny echoes. (Sanity -5). Current sanity: " + p.getSanity());
                }
                return "act6_reaction";
            }
        });

        engine.register(new DialogueScene("act6_reaction","Reaction",
            new String[] { "The Core pulses and a low voice replies: 'You were always coming.'" }, "act6_choice_reaction"));

        Map<String,String> react = new LinkedHashMap<>();
        react.put("Ask what happened", "act6_ask_happened");
        react.put("Stay silent and listen", "act6_listen");
        engine.register(new ChoiceScene("act6_choice_reaction","Reply","How do you respond?", react));

        engine.register(new DialogueScene(
    "act6_ask_happened",
    "Ask",
    new String[] { "The voice recounts fragments: a lab, a promise, a mistake." },
    "act6_core_access"
));

engine.register(new DialogueScene(
    "act6_listen",
    "Listen",
    new String[] { "You listen and pieces of lullaby reform into a face." },
    "act6_core_access"
));

        // Examine core: different behavior if player has clarity_token or crank
        engine.register(new Scene("act6_examine_core","Examine"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                if(p!=null){
                    boolean hasClarity = p.getInventory().contains("clarity_token");
                    boolean hasCrank = p.getInventory().contains("crank");
                    if(hasClarity){
                        System.out.println("The core recognizes the clarity token and shudders in welcome.");
                        // small sanity reward
                        p.modifySanity(+4);
                        System.out.println("(Sanity +4). Current sanity: " + p.getSanity());
                        return "act6_core_access";
                    } else if(hasCrank){
                        System.out.println("The crank fits a side socket. It turns with a dull thump.");
                        p.getInventory().remove("crank");
                        // maybe reveal a small compartment
                        if(!p.getInventory().contains("core_fragment")){
                            p.getInventory().add("core_fragment");
                            System.out.println("(Inventory + core_fragment)");
                        }
                        return "act6_core_access";
                    } else {
                        System.out.println("You probe the casing; it resists. The Lapati laughs softly.");
                        p.modifySanity(-3);
                        System.out.println("(Sanity -3). Current sanity: " + p.getSanity());
                        return "act6_choice_entry";
                    }
                }
                return "act6_choice_entry";
            }
        });

        // After we have entry info, lead to core access choices
        engine.register(new DialogueScene("act6_core_access","Access",
            new String[] { "The Core offers a panel with three options: Restore, Remember, or Release." }, "act6_core_choice"));

        Map<String,String> coreOptions = new LinkedHashMap<>();
        coreOptions.put("Restore (attempt to repair the Core)", "act6_restore");
        coreOptions.put("Remember (play memories into the Core)", "act6_remember");
        coreOptions.put("Release (destroy or free whatever's inside)", "act6_release");
        engine.register(new ChoiceScene("act6_core_choice","Core Panel","Choose an action:", coreOptions));

        // Restore: requires circuit_part or oil_can; success gives small sanity and core_fragment->core_fixed
        engine.register(new Scene("act6_restore","Restore"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                boolean hasCircuit = p!=null && p.getInventory().contains("circuit_part");
                boolean hasOil = p!=null && p.getInventory().contains("oil_can");
                if(p!=null && (hasCircuit||hasOil)){
                    if(hasCircuit) p.getInventory().remove("circuit_part");
                    if(hasOil) p.getInventory().remove("oil_can");
                    System.out.println("You fit the parts. The Core sighs and a warm light runs through its veins.");
                    p.modifySanity(+5);
                    System.out.println("(Sanity +5). Current sanity: " + p.getSanity());
                    if(!p.getInventory().contains("core_fixed")) p.getInventory().add("core_fixed");
                    return "act6_after_restore";
                } else {
                    System.out.println("You lack the parts to repair the Core. The Core's patience thins.");
                    if(p!=null){ p.modifySanity(-4); System.out.println("(Sanity -4). Current sanity: " + p.getSanity()); }
                    return "act6_core_choice";
                }
            }
        });

        engine.register(new DialogueScene("act6_after_restore","Restored",
            new String[] { "the Core's tone changes: for a moment, it hums like a lullaby and shows a clear image of Clara." }, "act6_end_branch"));

        // Remember: feed memories (requires memory_token/clarity_token/clara_note)
        engine.register(new Scene("act6_remember","Remember"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                boolean hasMemory = p!=null && (p.getInventory().contains("memory_token") || p.getInventory().contains("clarity_token") || p.getInventory().contains("clara_note"));
                if(p!=null && hasMemory){
                    // consume a memory token if present for a stronger effect
                    if(p.getInventory().contains("memory_token")) p.getInventory().remove("memory_token");
                    p.modifySanity(+3);
                    System.out.println("You feed the Core a memory. It responds with a face: Clara, small and smiling. (Sanity +3). Current sanity: " + p.getSanity());
                    return "act6_core_reaction";
                } else {
                    System.out.println("You have nothing solid enough to share. The Core stares back, blank.");
                    if(p!=null){ p.modifySanity(-5); System.out.println("(Sanity -5). Current sanity: " + p.getSanity()); }
                    return "act6_core_choice";
                }
            }
        });

        engine.register(new DialogueScene("act6_core_reaction","Core Reacts",
            new String[] { "A cascade of images floods you — fragments of a childhood, a lab, a face that isn't yours." }, "act6_end_branch"));

        // Release: destructive option, heavy sanity cost, possible truth reveal
        engine.register(new Scene("act6_release","Release"){
            @Override
            public String play(GameState state, Scanner in){
                Player p = state.getPlayer();
                if(p!=null){
                    p.modifySanity(-20);
                    System.out.println("You strike the Core. The machine shudders and shrapnel sings.");
                    System.out.println("(Sanity -20). Current sanity: " + p.getSanity());
                    // reveal a brutal truth token
                    if(!p.getInventory().contains("brutal_truth")){
                        p.getInventory().add("brutal_truth");
                        System.out.println("(Inventory + brutal_truth)");
                    }
                }
                return "act6_end_branch";
            }
        });

        // After core actions converge here
        engine.register(new DialogueScene("act6_after_core_action","After Core",
            new String[] { "The Core settles. The world seems thinner, truer, or crueler depending on what you did." }, "act6_end_branch"));

        // End branch: wrap and handoff to final act (or epilogue)
        engine.register(new DialogueScene("act6_end_branch","Aftermath",
            new String[] { "You step back from the Core. Pieces of your past flicker in your hands." }, "act7_intro"));

        // safety
        engine.register(new DialogueScene("act6_no_scene","No Scene",
            new String[] { "Nothing continues here." }, null));
    }
}
