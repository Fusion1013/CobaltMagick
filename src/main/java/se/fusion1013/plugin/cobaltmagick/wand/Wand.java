package se.fusion1013.plugin.cobaltmagick.wand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.database.Database;
import se.fusion1013.plugin.cobaltmagick.manager.LocaleManager;
import se.fusion1013.plugin.cobaltmagick.spells.CastParser;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wand extends AbstractWand implements Runnable { // TODO: Move things to abstract class and extend it

    public Wand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<ISpell> alwaysCast, int wandTier){
        super(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);
    }

    public Wand(int cost, int level, boolean forceUnshuffle){
        super(cost, level, forceUnshuffle);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);
    }

    public void castSpells(Player p){
        CastResult result = performSpellCast(p);

        LocaleManager localeManager = LocaleManager.getInstance();
        switch (result){
            case CAST_DELAY:
                break;
            case RECHARGE_TIME:
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case NO_MANA:
                p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);
                break;
            case SUCCESS:
                break;
        }
    }

    /**
     * Cast the next spells in the wand
     * @return The result of the casting
     */
    private CastResult performSpellCast(Player p){
        int spellsCast = 0;

        // Check if the wand is on cooldown
        if (castCooldown > 0) return CastResult.CAST_DELAY;
        else if (rechargeCooldown > 0) return CastResult.RECHARGE_TIME;

        // Cast all the always cast spells. These spells get cast for free
        for (ISpell s : alwaysCast){
            s.castSpell(this, p);
        }

        int manaUsed = 0;
        double castDelayInduced = castDelay;
        int startPos = 0;
        // if (shuffle)  startPos = new Random().nextInt(0, spells.size()); // TODO: Low chance of early spells in wand to be cast

        CastParser parser = new CastParser(spells, spellsPerCast, startPos);
        List<ISpell> spellsToCast = parser.prepareCast();

        for (ISpell s : spellsToCast){
            // Check mana
            manaUsed += s.getTrueManaDrain();

            if (manaUsed > currentMana) {
                currentMana = 0;
                if (allSpellsCast()) recharge(); // Recharge if all spells have been cast
                return CastResult.NO_MANA;
            }

            s.castSpell(this, p);
            castDelayInduced += s.getTrueCastDelay();
        }

        currentMana = Math.max(0, currentMana - manaUsed);
        currentMana = Math.min(currentMana, manaMax);
        castCooldown = Math.max(0, castDelayInduced);

        // Check if all spells in the wand has been cast. If they have, start recharge cooldown
        if (allSpellsCast()) recharge();

        // Set visual cooldown on item
        p.setCooldown(getWandItem().getType(), (int)Math.ceil(Math.max(rechargeCooldown, castCooldown) * 20));

        return CastResult.SUCCESS;
    }

    /**
     * Sets the wand into its recharge state
     */
    private void recharge(){
        for (ISpell spell : spells){
            spell.setHasCast(false);
            rechargeCooldown += spell.getRechargeTime();
        }
        rechargeCooldown += rechargeTime;
    }

    /**
     * Checks if all spells in the wand have been cast
     *
     * @return If all spells have been cast
     */
    private boolean allSpellsCast(){
        for (ISpell s : spells){
            if (!s.getHasCast()) return false; // TODO: Check if the spell is a modifier
        }
        return true;
    }

    @Override
    public void run() {
        // Increase mana
        if (currentMana < manaMax) currentMana = Math.min(currentMana + ((double)manaChargeSpeed / 20.0), manaMax);

        // Decrease Cast Delay
        if (castCooldown > 0) castCooldown = Math.max(0, castCooldown - 0.05);

        // Decrease Recharge Time
        if (rechargeCooldown > 0) rechargeCooldown = Math.max(0, rechargeCooldown - 0.05);

        // Display data to player holding item
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack is = p.getInventory().getItemInMainHand();
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                Integer wandId = meta.getPersistentDataContainer().get(wandKey, PersistentDataType.INTEGER);
                if (wandId != null){
                    if (wandId.equals(id)) {
                        displayData(p);
                    }
                }
            }
        }

        // Passive spells
    }

    /**
     * Displays wand data (Recharge & Mana) to the player
     *
     * @param p player to display the data to
     */
    private void displayData(Player p){
        double recharge = Math.max(rechargeCooldown, castCooldown);

        String message = ChatColor.RED + "Recharge" + ChatColor.GRAY + ": " + ChatColor.RED + Math.round(recharge*10)/10.0 + ChatColor.GRAY + "s" + "          " + ChatColor.AQUA + "Mana" + ChatColor.GRAY + ": " + ChatColor.AQUA + Math.round(currentMana);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}
