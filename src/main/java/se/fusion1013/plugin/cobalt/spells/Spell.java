package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Spell implements ISpell {

    public static final Map<Integer, Spell> INBUILT_SPELLS = new HashMap<>();

    // TODO: Change material to a custom texture
    public static final Spell sparkBolt = register(new SparkBoltSpell(0, "spark_bolt"));
    public static final Spell healSpell = register(new HealSpell(1, "heal"));

    // TODO: Implement uses (spells disappear when their uses run out)
    // Hidden Attributes
    int id;
    String spellName;
    boolean hasCast;
    int customModelData;

    double lifetime;
    double speedModifier;
    double lifetimeModifier;
    double criticalChance;

    static NamespacedKey spellKey = new NamespacedKey(Cobalt.getInstance(), "spell");

    // Shown Attributes
    int uses;
    int manaDrain;
    double damage;
    double radius;
    double spread;
    double speed;
    double castDelay;
    double rechargeTime;
    double spreadModifier;
    int addCasts;

    public Spell(int id, String spellName) {
        this.id = id;
        this.spellName = spellName;
        this.customModelData = id+1;
    }

    public static ItemStack getSpellItem(String spellName) {
        ISpell spell = getSpell(spellName);

        ItemStack stack = new ItemStack(Material.NETHER_STAR, 1); // TODO: Get material from config (??)
        ItemMeta meta = stack.getItemMeta();

        meta.getPersistentDataContainer().set(spellKey, PersistentDataType.INTEGER, spell.getId());

        // TODO: Change color depending on rarity (type?) of spell
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.BLUE + spell.getFormattedName());
        meta.setLore(getLore(spell));

        meta.setCustomModelData(spell.getCustomModelData());

        stack.setItemMeta(meta);
        return stack;
    }

    public static List<String> getLore(ISpell spell) {
        List<String> lore = new ArrayList<>();

        if (spell.getManaDrain() != 0) lore.add(ChatColor.WHITE + "Mana Drain: " + ChatColor.BLUE +  spell.getManaDrain());
        if (spell.getDamage() != 0) lore.add(ChatColor.WHITE + "Damage: " + ChatColor.BLUE +  spell.getDamage());
        if (spell.getRadius() != 0) lore.add(ChatColor.WHITE + "Radius: " + ChatColor.BLUE +  spell.getRadius());
        if (spell.getSpread() != 0) lore.add(ChatColor.WHITE + "Spread: " + ChatColor.BLUE +  spell.getSpread() + ChatColor.WHITE + " DEG");
        if (spell.getSpeed() != 0) lore.add(ChatColor.WHITE + "Speed: " + ChatColor.BLUE +  spell.getSpeed());

        if (spell.getCastDelay() > 0) lore.add(ChatColor.WHITE + "Cast Delay: +" + ChatColor.BLUE +  spell.getCastDelay() + ChatColor.WHITE + "s");
        else if (spell.getCastDelay() < 0) lore.add(ChatColor.WHITE + "Cast Delay: -" + ChatColor.BLUE +  spell.getCastDelay() + ChatColor.WHITE + "s");

        if (spell.getRechargeTime() != 0 && spell.getRechargeTime() > 0) lore.add(ChatColor.WHITE + "Recharge Time: +" + ChatColor.BLUE +  spell.getRechargeTime() + ChatColor.WHITE + "s");
        else if (spell.getRechargeTime() < 0) lore.add(ChatColor.WHITE + "Recharge Time: -" + ChatColor.BLUE +  spell.getRechargeTime() + ChatColor.WHITE + "s");

        if (spell.getSpreadModifier() != 0) lore.add(ChatColor.WHITE + "Spread: " + ChatColor.BLUE +  spell.getSpreadModifier() + ChatColor.WHITE + " DEG");
        if (spell.getAddCasts() != 0) lore.add(ChatColor.WHITE + "Add Casts: " + ChatColor.BLUE +  spell.getAddCasts());

        return lore;
    }

    public static ISpell getSpell(String name){
        List<ISpell> sps = new ArrayList<>(INBUILT_SPELLS.values());

        for (ISpell s : sps) {
            if (s.getSpellName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public static ISpell getSpell(int id){
        return INBUILT_SPELLS.get(id);
    }

    private static <T extends Spell> T register(final T spell) {
        INBUILT_SPELLS.put(spell.getId(), spell);
        return spell;
    }

    public static ISpell getSpell(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        if (meta.getPersistentDataContainer().has(spellKey, PersistentDataType.INTEGER)){
            int spellId = meta.getPersistentDataContainer().get(spellKey, PersistentDataType.INTEGER);
            return getSpell(spellId);
        } else {
            return null;
        }
    }

    @Override
    public String getSpellName() {
        return spellName;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public double getLifetime() {
        return lifetime;
    }

    @Override
    public double getSpeedModifier() {
        return speedModifier;
    }

    @Override
    public double getLifetimeModifier() {
        return lifetimeModifier;
    }

    @Override
    public double getCriticalChance() {
        return criticalChance;
    }

    @Override
    public int getManaDrain() {
        return manaDrain;
    }

    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double getSpread() {
        return spread;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getCastDelay() {
        return castDelay;
    }

    @Override
    public double getRechargeTime() {
        return rechargeTime;
    }

    @Override
    public double getSpreadModifier() {
        return spreadModifier;
    }

    @Override
    public boolean getHasCast() { return hasCast; }

    @Override
    public int getAddCasts() {
        return addCasts;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCustomModelData() { return customModelData; }
}
