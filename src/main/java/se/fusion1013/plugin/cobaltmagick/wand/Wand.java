package se.fusion1013.plugin.cobaltmagick.wand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.bar.actionbar.ActionBarManager;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltcore.util.ActionBarUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.manager.MagickConfigManager;
import se.fusion1013.plugin.cobaltmagick.manager.MagickSettingsManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wand extends AbstractWand {

    public Wand(boolean shuffle, int spellsPerCast, double castDelay, double rechargeTime, int manaMax, int manaChargeSpeed, int capacity, double spread, List<SpellContainer> alwaysCast, int wandTier){
        super(shuffle, spellsPerCast, castDelay, rechargeTime, manaMax, manaChargeSpeed, capacity, spread, alwaysCast, wandTier);
    }

    public Wand(int cost, int level, boolean forceUnshuffle){
        super(cost, level, forceUnshuffle);
    }

    public Wand(Wand target) {
        super(target);
    }

    public void castSpells(Player p){
        CastResult result = performSpellCast(p);

        switch (result){
            case CAST_DELAY:
            case SUCCESS:
                break;
            case RECHARGE_TIME:
                p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                break;
            case NO_MANA:
                p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 1);
                break;
        }
    }

    public void castSpells(LivingEntity e, Vector direction, Location location) {
        CastResult result = performSpellCast(e, direction, location);
    }

    private CastResult performSpellCast(LivingEntity e) {
        return performSpellCast(e, null, null);
    }

    /**
     * Cast the next spells in the wand
     * @return The result of the casting
     */
    private CastResult performSpellCast(LivingEntity caster, Vector direction, Location location){

        // Check if the wand is on cooldown
        if (castCooldown > 0) return CastResult.CAST_DELAY;
        else if (rechargeCooldown > 0) return CastResult.RECHARGE_TIME;

        // Cast all the always cast spells. These spells get cast for free
        for (SpellContainer s : alwaysCast){
            ISpell spell = SpellManager.getSpell(s.spellId);
            if (spell == null) continue;

            if (direction == null) spell.castSpell(this, caster);
            else spell.castSpell(this, caster, direction, location);
        }

        if (spells.size() == 0) return CastResult.SUCCESS;

        int manaUsed = 0;
        double castDelayInduced = castDelay;
        int startPos = 0;
        if (shuffle) startPos = getRandomStartPos();

        CastParser parser = new CastParser(caster, id, getSpells(), spellsPerCast, startPos);
        List<ISpell> spellsToCast = parser.prepareCast();

        for (ISpell s : spellsToCast){
            // Check mana
            manaUsed += s.getTrueManaDrain();

            if (manaUsed > currentMana) {
                currentMana = 0;
                if (allSpellsCast()) recharge(); // Recharge if all spells have been cast
                return CastResult.NO_MANA;
            }

            s.setCaster(caster);
            SpellCastEvent event = new SpellCastEvent(s);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (direction == null) s.castSpell(this, caster);
                else s.castSpell(this, caster, direction, location);
                castDelayInduced += s.getTrueCastDelay();
            }
        }

        currentMana = Math.max(0, currentMana - manaUsed);
        currentMana = Math.min(currentMana, manaMax);

        // Check if all spells in the wand has been cast. If they have, start recharge cooldown
        if (allSpellsCast()) recharge();
        else castCooldown = Math.max(0, castDelayInduced); // Do not induce cast delay if all spells have been cast

        if (caster instanceof Player p) {
            p.setCooldown(getWandItem().getType(), (int)Math.ceil(Math.max(rechargeCooldown, castCooldown) * 20));
            lastRechargeMax = Math.max(rechargeCooldown, castCooldown);
        }

        return CastResult.SUCCESS;
    }

    private int getRandomStartPos(){
        List<Integer> randomPoses = new ArrayList<>();

        for (int i = 0; i < spells.size(); i++){
            ISpell spell = spells.get(i).getSpell();
            if (spell == null) continue;

            if (!spell.getHasCast()) randomPoses.add(i);
        }

        Random r = new Random();
        return randomPoses.get(r.nextInt(0, randomPoses.size()));
    }

    public void forceRecharge() {
        recharge();
    }

    /**
     * Sets the wand into its recharge state
     */
    private void recharge(){
        for (SpellContainer spellContainer : spells){
            ISpell spell = SpellManager.getSpell(spellContainer.spellId);
            if (spell == null) return;

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
        for (SpellContainer s : spells) {
            ISpell spell = s.getSpell();
            if (spell == null) continue;

            if (!spell.getHasCast()) return false;
        }
        return true;
    }

    private boolean manaRechargeAllowed() {
        if ((Boolean) ConfigManager.getInstance().getFromConfig(CobaltMagick.getInstance(), "magick.yml", "disable-wand-recharging")) return false;
        if (!regionAllowsManaRecharge) return false;

        return true;
    }

    public void tick() {
        // Increase mana
        if (currentMana < manaMax && manaRechargeAllowed()) {
            currentMana = Math.min(currentMana + ((double)manaChargeSpeed / 20.0), manaMax);
        }

        // Decrease Cast Delay
        if (castCooldown > 0) castCooldown = Math.max(0, castCooldown - 0.05);

        // Decrease Recharge Time
        if (rechargeCooldown > 0) rechargeCooldown = Math.max(0, rechargeCooldown - 0.05);

        // Passive spells
    }

    public void executePassiveSpells() {

    }

    /**
     * Displays wand data (Recharge & Mana) to the player
     *
     * @param player player to display the data to
     */
    public void displayData(Player player) {

        double recharge = Math.max(rechargeCooldown, castCooldown);

        // Add mana bar
        double manaPercent = (currentMana / manaMax) * 10;
        ActionBarUtil.ActionBarBuilder builder = new ActionBarUtil.ActionBarBuilder();
        addProgressBar(builder, "\uE001", "\uE003", "\uE002", "\uE004", "\uE006", "\uE005", -41, manaPercent);

        // Add recharge time bar
        double rechargePercent = (1 - (recharge / lastRechargeMax)) * 10;
        addProgressBar(builder, "\uE001", "\uE003", "\uE002", "\uE007", "\uE009", "\uE008", 59, rechargePercent);

        // Construct text bar
        String message = ChatColor.RED + "Recharge" + ChatColor.GRAY + ": " + ChatColor.RED + Math.round(recharge*10)/10.0 + ChatColor.GRAY + "s" + "          " + ChatColor.AQUA + "Mana" + ChatColor.GRAY + ": " + ChatColor.AQUA + Math.round(currentMana);

        switch (SettingsManager.getPlayerSetting(player, MagickSettingsManager.WAND_HUD_APPEARANCE)) {
            case "text" -> player.sendActionBar(Component.text(message));
            case "bars_only" -> ActionBarManager.subscribe(player, "magick:wand", builder.getComponents());
        }

        // Send action bar
        // TODO: player.sendActionBar(builder.getComponent());

        /*
        // Add recharge time bar
        double rechargePercent = 1 - (recharge / lastRechargeMax);
        String rechargeChar;
        if (rechargePercent >= 1) rechargeChar = "\uE91A";
        else if (rechargePercent >= .9) rechargeChar = "\uE919";
        else if (rechargePercent >= .8) rechargeChar = "\uE918";
        else if (rechargePercent >= .7) rechargeChar = "\uE917";
        else if (rechargePercent >= .6) rechargeChar = "\uE916";
        else if (rechargePercent >= .5) rechargeChar = "\uE915";
        else if (rechargePercent >= .4) rechargeChar = "\uE914";
        else if (rechargePercent >= .3) rechargeChar = "\uE913";
        else if (rechargePercent >= .2) rechargeChar = "\uE912";
        else if (rechargePercent >= .1) rechargeChar = "\uE911";
        else rechargeChar = "\uE910";

        /*
        player.sendActionBar(
                Component.text(Math.round(currentMana)).color(NamedTextColor.BLUE)
                        .append(Component.text(manaChar).color(NamedTextColor.WHITE))
                        .append(Component.text("    "))
                        .append(Component.text(Math.round(recharge*10)/10.0).color(NamedTextColor.RED))
                        .append(Component.text("s").color(NamedTextColor.GRAY))
                        .append(Component.text(rechargeChar).color(NamedTextColor.WHITE))
        );
         */

        // player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));

        // player.sendActionBar(Component.text(message));
    }

    private static void addProgressBar(ActionBarUtil.ActionBarBuilder builder, String emptyChar, String emptyLeftChar, String emptyRightChar, String fullChar, String fullLeftChar, String fullRightChar, int offset, double progress) {
        if (progress > 0) builder.addComponent(new ActionBarUtil.ActionBarComponent(fullLeftChar, 9, offset));
        else builder.addComponent(new ActionBarUtil.ActionBarComponent(emptyLeftChar, 9, offset));

        for (int i = 1; i < 9; i++) {
            if (progress >= i) builder.addComponent(new ActionBarUtil.ActionBarComponent(fullChar, 9, (8*i) + offset));
            else builder.addComponent(new ActionBarUtil.ActionBarComponent(emptyChar, 9, (8*i) + offset));
        }

        if (progress >= 10) builder.addComponent(new ActionBarUtil.ActionBarComponent(fullRightChar, 9, (8*9) + offset));
        else builder.addComponent(new ActionBarUtil.ActionBarComponent(emptyRightChar, 9, (8*9) + offset));
    }
}
