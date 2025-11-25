package com.fractured;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fractured.core.ChoiceScene;
import com.fractured.core.DialogueScene;
import com.fractured.core.GameEngine;
import com.fractured.core.GameState;
import com.fractured.db.DBUtil;
import com.fractured.db.SaveDAO;
import com.fractured.model.Player;

public class Main {
    public static void main(String[] args) {
        // init DB
        DBUtil.initializeSchema();
       
        SaveDAO dao = new SaveDAO(new DBUtil());

        // create player & state
        Player p = new Player("Elias");
        p.getInventory().add("torch");
        GameState state = new GameState(p);
        state.setCurrentScene("intro");

        // create engine and scenes
        GameEngine engine = new GameEngine(state, dao);

        engine.register(new DialogueScene("intro", "Black Room", new String[]{
                "You wake up in a small dark room.",
                "A voice whispers something you can't understand."
        }, "choice1"));

        Map<String, String> choices = new LinkedHashMap<>();
        choices.put("Search the room", "search");
        choices.put("Call out for help", "call");
        engine.register(new ChoiceScene("choice1", "First Choice", "What do you do?", choices));

        engine.register(new DialogueScene("search", "Searching", new String[]{
                "You find an old notebook on the floor."
        }, "act1_foundNotebook")); // end

        engine.register(new DialogueScene("call", "Calling", new String[]{
                "Your voice echoes. Nothing answers."
        },"act1_end"));

        com.fractured.acts.Act1Register.registerAct1(engine);
       
        com.fractured.acts.Act2Register.registerAct2(engine);

        com.fractured.acts.Act3Register.registerAct3(engine);

        com.fractured.acts.Act4Register.registerAct4(engine);

        com.fractured.acts.Act5Register.registerAct5(engine);

        com.fractured.acts.Act6Register.registerAct6(engine);




        // start game
        engine.start();
        System.out.println("Game ended.");
    }
}
