package com.fractured.core;

import java.util.Map;
import java.util.Scanner;

public class ChoiceScene extends Scene {
    private final String prompt;
    private final Map<String, String> choices; // option -> nextScene

    public ChoiceScene(String id, String title, String prompt, Map<String, String> choices) {
        super(id, title);
        this.prompt = prompt;
        this.choices = choices;
    }

    @Override
    public String play(GameState state, Scanner in) throws GameException {
        System.out.println("[" + getTitle() + "]");
        System.out.println(prompt);
        int i = 1;
        String[] opts = new String[choices.size()];
        for (String opt : choices.keySet()) {
            System.out.println(i + ") " + opt);
            opts[i-1] = opt;
            i++;
        }
        System.out.print("> ");
        String line = in.nextLine().trim();
        int sel;
        try {
            sel = Integer.parseInt(line);
            if (sel < 1 || sel > opts.length) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new GameException("Invalid choice");
        }
        return choices.get(opts[sel-1]);
    }
}
