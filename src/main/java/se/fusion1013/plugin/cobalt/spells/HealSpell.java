package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.wand.Wand;

// TODO: Change to a projectile type spell
public class HealSpell extends Spell {

    double healAmount = 1;

    public HealSpell(int id, String spellName) {
        super(id, spellName);

        uses = 20;
        manaDrain = 15;
        radius = 2;
        spread = 0.6;
        speed = 625;
        lifetime = 60;
        castDelay = 0.07;
        spreadModifier = 2;
    }

    @Override
    public void castSpell(Wand wand, Player player) {
        Cobalt.getInstance().getLogger().info("Casting heal spell");
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double currentHealth = player.getHealth();
        double healTo = Math.min(maxHealth, currentHealth + healAmount);

        player.setHealth(healTo);

        showEffects(player);
    }

    private void showEffects(Player p) {
        p.spawnParticle(Particle.HEART, p.getLocation(), 10, .5, 1, .5);
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
    }

    @Override
    public void tick() {

    }

    @Override
    public String getFormattedName() {
        return "Heal";
    }
}
