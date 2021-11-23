package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.wand.Wand;

public abstract class ProjectileSpell extends Spell {

    boolean affectedByGravity = false;
    double weight;

    public ProjectileSpell(int id, String spellName) {
        super(id, spellName);
    }

    @Override
    public void castSpell(Wand wand, Player player) {
    }

    @Override
    public void tick() {

    }
}
