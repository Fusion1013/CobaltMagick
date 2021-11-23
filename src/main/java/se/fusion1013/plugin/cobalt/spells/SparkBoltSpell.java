package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.wand.Wand;

public class SparkBoltSpell extends ProjectileSpell {
    public SparkBoltSpell(int id, String spellName) {
        super(id, spellName);
    }

    @Override
    public void castSpell(Wand wand, Player player) {

    }

    @Override
    public void tick() {

    }

    @Override
    public String getFormattedName() {
        return "Spark Bolt";
    }
}
