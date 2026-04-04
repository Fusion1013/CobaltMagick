package se.fusion1013.cobaltmagick.wand.cast;

import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.spell.ISpellInstance;
import se.fusion1013.cobaltmagick.wand.WandState;

import java.util.*;

public class CastState {

    private final Queue<ISpellInstance> deck = new PriorityQueue<>((o1, o2) -> {
        if (o1.slot() == o2.slot()) return 0;
        return o1.slot() < o2.slot() ? -1 : 1;
    });
    private final Queue<ISpellInstance> hand = new LinkedList<>();
    private final Queue<ISpellInstance> discard = new LinkedList<>();

    public final CastResult castResult;
    public final ShotState shotState;

    private int manaUsed = 0;
    private boolean recharge = false;

    public CastState(WandState wandState, RuleLogger ruleLogger) {
        List<ISpellInstance> spells = wandState.getSpells();
        for (ISpellInstance spellInstance : spells) {
            if (spellInstance == null) continue;

            if (spellInstance.state().equalsIgnoreCase("deck")) deck.add(spellInstance);
            if (spellInstance.state().equalsIgnoreCase("hand")) hand.add(spellInstance);
            if (spellInstance.state().equalsIgnoreCase("discard")) discard.add(spellInstance);
        }

        logState("Created new Cast State", ruleLogger);

        ShotState shotState = new ShotState();
        castResult = ruleLogger.evaluate("CastState::evaluate", () -> evaluate(shotState, wandState, ruleLogger));

        logState("Finished drawing cards for Cast State", ruleLogger);
        shotState.log(ruleLogger);
        this.shotState = shotState;
    }

    private CastResult evaluate(ShotState shotState, WandState wandState, RuleLogger ruleLogger) {
        for (int i = 0; i < wandState.getStats().getSpellsPerCast(); i++) {
            draw(shotState, ruleLogger);
        }

        ruleLogger.logMessage("Spells in hand at end of evaluation:");
        for (ISpellInstance spell : hand) {
            ruleLogger.logMessage(" - " + spell.getInternalName());
            manaUsed += spell.manaCost();
        }

        discard();

        if (deck.isEmpty() || recharge) return CastResult.Recharge;
        else return CastResult.CastDelay;
    }

    private void draw(ShotState shotState, RuleLogger ruleLogger) {
        // If the deck is empty, move the discard pile back into the deck
        if (deck.isEmpty()) wrap();

        // Moves a spell from the deck to the hand
        ISpellInstance spell = deck.poll();
        if (spell == null) return;

        spell.setState("hand");
        hand.add(spell);

        ruleLogger.evaluate("Apply Modifiers (" + spell.getInternalName() + ")", () -> spell.modify(shotState, ruleLogger));

        int draws = spell.getDraws();
        for (int i = 0; i < draws; i++) {
            draw(shotState, ruleLogger);
        }
    }

    private void wrap() {
        while (!discard.isEmpty()) {
            ISpellInstance spell = discard.poll();
            spell.setState("deck");
            deck.add(spell);
        }
        recharge = true;
    }

    private void discard() {
        // Moves all spells from the hand to the discard
        while (!hand.isEmpty()) {
            ISpellInstance spell = hand.poll();
            spell.setState("discard");
            discard.add(spell);
        }
    }

    private void logState(String title, RuleLogger ruleLogger) {
        ruleLogger.logMessage(title);
        ruleLogger.logMessage("DECK:" + deck.size() + ", HAND:" + hand.size() + ", DISCARD:" + discard.size());
    }

    public List<ISpellInstance> getSpells() {
        List<ISpellInstance> spells = new ArrayList<>();
        spells.addAll(discard.stream().toList());
        spells.addAll(hand.stream().toList()); // Hand should always be empty at this point
        spells.addAll(deck.stream().toList());
        return spells;
    }

    public int getManaUsed() {
        return manaUsed;
    }
}
