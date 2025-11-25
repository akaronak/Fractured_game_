package com.fractured.acts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fractured.core.ChoiceScene;
import com.fractured.core.DialogueScene;
import com.fractured.core.GameEngine;

/**
 * Act1Register
 *
 * Registers a complete Act 1 (Black Room) — all dialogue and branching.
 */
public final class Act1Register {

    private Act1Register() { }

    public static void registerAct1(GameEngine engine) {
        // NOTE: IDs should be unique across the project; these start with "act1_"

        // --- Intro (keeps the original two lines) ---
        engine.register(new DialogueScene(
            "act1_intro",
            "Black Room",
            new String[] {
                "You wake up in a small dark room.",
                "A voice whispers something you can't understand."
            },
            "act1_choice1"
        ));

        // --- First choice (search vs call) ---
        Map<String,String> firstChoices = new LinkedHashMap<>();
        firstChoices.put("Search the room", "act1_search");
        firstChoices.put("Call out for help", "act1_call");
        engine.register(new ChoiceScene(
            "act1_choice1",
            "First Choice",
            "What do you do?",
            firstChoices
        ));

        // --- Search branch ---
        engine.register(new DialogueScene(
            "act1_search",
            "Searching",
            new String[] {
                "You find an old notebook on the floor."
            },
            "act1_foundNotebook"
        ));

        // Found notebook: read vs keep
        Map<String,String> notebookChoices = new LinkedHashMap<>();
        notebookChoices.put("Open the notebook", "act1_readNotebook");
        notebookChoices.put("Pocket it for later", "act1_keepNotebook");
        engine.register(new ChoiceScene(
            "act1_foundNotebook",
            "Notebook",
            "What do you do with the notebook?",
            notebookChoices
        ));

        engine.register(new DialogueScene(
            "act1_readNotebook",
            "Notebook — Read",
            new String[] {
                "You open the notebook. A few words are scribbled inside:",
                "'Remember the Lapati. Remember Clara.'",
                "The handwriting is shaky, as if written in a hurry."
            },
            "act1_afterNotebookRead"
        ));

        engine.register(new DialogueScene(
            "act1_keepNotebook",
            "Notebook — Pocketed",
            new String[] {
                "You tuck the notebook into your pocket.",
                "It feels important, but you can't read it in the dark."
            },
            "act1_afterNotebookRead"
        ));

        // After the notebook moment — player decides how to proceed
        Map<String,String> afterNotebookChoices = new LinkedHashMap<>();
        afterNotebookChoices.put("Search further for an exit", "act1_searchExit");
        afterNotebookChoices.put("Shout for help", "act1_call_from_search");
        engine.register(new ChoiceScene(
            "act1_afterNotebookRead",
            "After Notebook",
            "What next?",
            afterNotebookChoices
        ));

        engine.register(new DialogueScene(
            "act1_searchExit",
            "Corridor",
            new String[] {
                "You feel along the wall and find a seam — a door.",
                "You squeeze the door open and step into the corridor.",
                "Dust motes float in a thin beam of light. The world outside the room is uncertain, but it feels like progress."
            },
            "act1_end" // moves to act end (act 2 will continue later)
        ));

        engine.register(new DialogueScene(
            "act1_call_from_search",
            "Calling",
            new String[] {
                "You call out for someone. The voice echoes back to you, but nothing answers.",
                "In the distance, a faint mechanical sound ticks in rhythm with your heartbeat."
            },
            "act1_end"
        ));

        // --- Call branch (from initial choice) ---
        engine.register(new DialogueScene(
            "act1_call",
            "Calling",
            new String[] {
                "Your voice echoes. Nothing answers."
            },
            "act1_call_noanswer"
        ));

        // If they keep calling: slight sanity drain then choice to keep or search
        Map<String,String> callChoices = new LinkedHashMap<>();
        callChoices.put("Keep calling", "act1_call_loop");
        callChoices.put("Search the room", "act1_search"); // jump into search branch
        engine.register(new ChoiceScene(
            "act1_call_noanswer",
            "Calling",
            "Your voice fades. What do you do?",
            callChoices
        ));

        engine.register(new DialogueScene(
            "act1_call_loop",
            "Calling — Alone",
            new String[] {
                "You keep calling and the emptiness grows heavy.",
                "A whisper in the dark says something — you can't quite make the words out.",
                "It makes you feel colder inside."
            },
            "act1_after_call_loop"
        ));

        // after calling loop: options
        Map<String,String> afterCallChoices = new LinkedHashMap<>();
        afterCallChoices.put("Stand and keep calling", "act1_call_loop_again");
        afterCallChoices.put("Search the room", "act1_search");
        engine.register(new ChoiceScene(
            "act1_after_call_loop",
            "After Calling",
            "You shiver. What do you try?",
            afterCallChoices
        ));

        engine.register(new DialogueScene(
            "act1_call_loop_again",
            "Calling — More",
            new String[] {
                "You feel your mind tugging at the edges. Whispering grows louder.",
                "This path leads only inward for now, and you steady yourself."
            },
            "act1_end"
        ));

        // --- small "Act end" tidy wrap-up for Act 1 ---
        engine.register(new DialogueScene(
            "act1_end",
            "Act 1 — End",
            new String[] {
                "You have escaped the immediate prison of the black room.",
                "Further questions remain. The notebook's words haunt you: 'Remember the Lapati. Remember Clara.'",
                "This is not the end — Act II waits."
            },
            "act2_intro" // nextScene == null ends the act nicely
        ));

        // ---- optional: safety scene if IDs collide or missing paths ----
        // (no registration needed here; all branches above reach act1_end or a continuing scene)
    }
}
