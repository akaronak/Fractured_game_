package com.fractured.acts;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fractured.core.ChoiceScene;
import com.fractured.core.DialogueScene;
import com.fractured.core.GameEngine;

/**
 * Act2Register
 *
 * Registers Act II — the corridor and first outside encounters.
 * IDs begin with "act2_".
 */
public final class Act2Register {

    private Act2Register() { }

    public static void registerAct2(GameEngine engine) {
        // Intro to Act 2 - corridor - this should match whatever Act1 uses as nextScene
        engine.register(new DialogueScene(
            "act2_intro",
            "Corridor",
            new String[] {
                "You step into the corridor. The air is colder here, and the light is thin.",
                "A low mechanical hum carries from somewhere deeper in the structure."
            },
            "act2_choice1"
        ));

        // First Act2 choice: follow the hum, explore a side door, or search the floor
        Map<String,String> choices1 = new LinkedHashMap<>();
        choices1.put("Follow the hum down the corridor", "act2_followHum");
        choices1.put("Try the side door", "act2_sideDoor");
        choices1.put("Check the floor / retrace pockets", "act2_checkFloor");
        engine.register(new ChoiceScene(
            "act2_choice1",
            "Corridor — Decision",
            "Which way?",
            choices1
        ));

        // Follow the hum -> encounter a broken mechanism and a faint recording
        engine.register(new DialogueScene(
            "act2_followHum",
            "The Hum",
            new String[] {
                "You move towards the hum and find an old machine, half buried in dust.",
                "A portion of its casing is cracked open. There is a small speaker; when you brush dust away, a fragment of a voice plays: '—Lapati… not done…'",
                "The voice chills you. Your head aches; sanity trembles."
            },
            "act2_afterHum"
        ));

        Map<String,String> afterHumChoices = new LinkedHashMap<>();
        afterHumChoices.put("Smash the machine", "act2_smashMachine");
        afterHumChoices.put("Leave it and press on", "act2_pressOn");
        engine.register(new ChoiceScene(
            "act2_afterHum",
            "Machine",
            "What now?",
            afterHumChoices
        ));

        engine.register(new DialogueScene(
            "act2_smashMachine",
            "Broken",
            new String[] {
                "You strike the machine. It cracks and stutters; a single final whisper cycles: '—remember—' and then silence.",
                "The act of smashing steadies your hands — a small reprieve."
            },
            "act2_pressOn" // continue corridor
        ));

        engine.register(new DialogueScene(
            "act2_pressOn",
            "Forward",
            new String[] {
                "You move on. The corridor turns and opens into a chamber with faint graffiti on the wall.",
                "A name is written there in hurried letters: Clara."
            },
            "act2_choice2"
        ));

        // side door -> small room with a locked chest (choice to open)
        engine.register(new DialogueScene(
            "act2_sideDoor",
            "Side Room",
            new String[] {
                "The side door squeals open. Inside, a small room — a locked chest in the corner, its wood swollen with age.",
                "A scrap of paper is taped to the lid; it says, 'For Elias'."
            },
            "act2_chestChoice"
        ));

        Map<String,String> chestChoices = new LinkedHashMap<>();
        chestChoices.put("Try to open the chest", "act2_openChest");
        chestChoices.put("Leave it — it's probably a trap", "act2_leaveChest");
        engine.register(new ChoiceScene(
            "act2_chestChoice",
            "Chest",
            "What do you do?",
            chestChoices
        ));

        engine.register(new DialogueScene(
            "act2_openChest",
            "Chest Opened",
            new String[] {
                "The chest yields. Inside: a faded photograph of two people — one unmistakably Clara — and a small key.",
                "Something in you tightens; memory stabs like a pin. You pocket the key."
            },
            "act2_choice2" // continue to main chamber
        ));

        engine.register(new DialogueScene(
            "act2_leaveChest",
            "Chest Left",
            new String[] {
                "You step back. The urge to open it nags at you, but you keep moving.",
                "Whatever is inside will wait."
            },
            "act2_choice2"
        ));

        // check floor -> find a bent nail and a scrap of paper referencing Lapati
        engine.register(new DialogueScene(
            "act2_checkFloor",
            "On the Floor",
            new String[] {
                "On the floor you find a bent nail and a scrap reading: 'Do not trust the voice — Lapati will pull.'",
                "You tuck the nail in your shoe. It is small, but it feels like a tool."
            },
            "act2_choice2"
        ));

        // Main chamber choice: follow graffiti trail or search the side tunnel
        Map<String,String> choices2 = new LinkedHashMap<>();
        choices2.put("Inspect the graffiti and follow the trail", "act2_followGraffiti");
        choices2.put("Enter a narrow side tunnel", "act2_sideTunnel");
        engine.register(new ChoiceScene(
            "act2_choice2",
            "Chamber",
            "How do you proceed?",
            choices2
        ));

        // follow graffiti -> series of memory-flash dialogues, choice to confront the voice or run
        engine.register(new DialogueScene(
            "act2_followGraffiti",
            "Graffiti Trail",
            new String[] {
                "You follow a line of scrawled marks; each has been written in the same hurried hand as the notebook.",
                "Images press at the back of your mind — a child's laugh, Clara's face, something broken."
            },
            "act2_finalChoice"
        ));

        // side tunnel -> encounter an automated gate and a puzzle (simple branch outcome)
        engine.register(new DialogueScene(
            "act2_sideTunnel",
            "Narrow Tunnel",
            new String[] {
                "The narrow tunnel opens to a small alcove with an automated gate.",
                "A panel requires a key or a small tool to pry it open."
            },
            "act2_sideTunnelChoice"
        ));

        Map<String,String> tunnelChoices = new LinkedHashMap<>();
        tunnelChoices.put("Use the small key (if you have one)", "act2_useKey");
        tunnelChoices.put("Use the bent nail (if you have one) to pry", "act2_useNail");
        tunnelChoices.put("Retreat and take the graffiti path instead", "act2_followGraffiti");
        engine.register(new ChoiceScene(
            "act2_sideTunnelChoice",
            "Gate",
            "How do you open the gate?",
            tunnelChoices
        ));

        engine.register(new DialogueScene(
            "act2_useKey",
            "Gate — Key",
            new String[] {
                "You find a key in your pocket (or imagine you do) and it turns. The gate clicks open.",
                "Beyond is a dim stair leading downward. A cold wind rises, carrying a whisper that sounds like your name."
            },
            "act2_finalChoice"
        ));

        engine.register(new DialogueScene(
            "act2_useNail",
            "Gate — Pry",
            new String[] {
                "You wedge the nail and pry. The gate groans and yields.",
                "The stair beyond smells of old rain and metal. A faint child's humming floats from below."
            },
            "act2_finalChoice"
        ));

        // final choice of act 2: confront voice (risk sanity) or secure an exit (preserve sanity)
        Map<String,String> finalChoices = new LinkedHashMap<>();
        finalChoices.put("Confront the voice (risk sanity)", "act2_confront");
        finalChoices.put("Find a secure exit for now", "act2_secureExit");
        engine.register(new ChoiceScene(
            "act2_finalChoice",
            "Decision — Confront or Exit",
            "Do you confront what calls to you, or secure a way out?",
            finalChoices
        ));

        engine.register(new DialogueScene(
            "act2_confront",
            "Confrontation",
            new String[] {
                "You call into the dark: 'Lapati — who are you? What do you want?'",
                "Silence answers, then a pattern of voices like teeth on glass: 'Remember... Clara...' You feel something clawing at your mind; it costs you deeply."
            },
            "act2_confrontEnd"
        ));

        engine.register(new DialogueScene(
            "act2_confrontEnd",
            "After Confrontation",
            new String[] {
                "Your head swims. But in the fog, an image: a photograph half-buried, a child's face — Clara.",
                "The cost of the confrontation is a touch of clarity, and a heavy debt on your sanity."
            },
            "act2_end"
        ));

        engine.register(new DialogueScene(
            "act2_secureExit",
            "Secure Exit",
            new String[] {
                "You decide to be practical and secure a route out. You mark the path, steady your breathing, and step forward.",
                "Some questions wait; for now you preserved a measure of yourself."
            },
            "act2_end"
        ));

        // Act 2 end: wrap, hand-off to Act 3
        engine.register(new DialogueScene(
            "act2_end",
            "Act 2 — End",
            new String[] {
                "The corridor narrows behind you. The notebook's words echo: 'Remember the Lapati. Remember Clara.'",
                "You have answers but also heavier questions. Act III awaits, and with it the deeper truth."
            },
            "act3_intro" // set to "act3_intro" if you want auto-advance
        ));
    }
}
