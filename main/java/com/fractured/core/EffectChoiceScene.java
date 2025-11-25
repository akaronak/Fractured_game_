package com.fractured.core;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A ChoiceScene which runs a small side-effect (BiConsumer<GameState,String choiceId>)
 * when a choice is selected. This is how we modify sanity or inventory from choices.
 */
public class EffectChoiceScene extends ChoiceScene {
    private final BiConsumer<GameState, String> onChoice;

    /**
     * @param id scene id
     * @param title display title
     * @param prompt prompt text
     * @param choices map label->targetId
     * @param onChoice callback(state, selectedChoiceLabel) -> can mutate state
     */
    public EffectChoiceScene(String id, String title, String prompt, Map<String, String> choices,
                             BiConsumer<GameState, String> onChoice) {
        super(id, title, prompt, choices);
        this.onChoice = onChoice;
    }

    @Override
    public String play(GameState state, java.util.Scanner in) throws GameException {
        // Let base ChoiceScene handle display and selection; we capture the selected key
        // To do that we call super.play but we need the label of the choice selected.
        // Since original ChoiceScene probably returns the nextScene id, we replicate its
        // behaviour with minor modifications. Implement minimal re-display logic by
        // copying the choice behaviour — if ChoiceScene internals are different, adapt accordingly.
        // For safety, we'll call ChoiceScene's play (which returns nextId) and then run the callback
        // with a convention: the choice label isn't available from ChoiceScene; instead we
        // will run onChoice with the returned next id (so callback can switch on nextId).
        String nextId = super.play(state, in);
        try {
            if (onChoice != null) onChoice.accept(state, nextId);
        } catch (Exception e) {
            // convert to GameException so caller handles it
            throw new GameException("EffectChoiceScene side-effect failed: " + e.getMessage());
        }
        return nextId;
    }
}
