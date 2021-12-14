package se.fusion1013.plugin.cobalt.spells;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.spells.spellmodifiers.SpellModifier;
import se.fusion1013.plugin.cobalt.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public abstract class Spell implements ISpell, Cloneable {

    // Hidden Attributes
    int id;
    String internalSpellName;
    String spellName;
    String description;
    static final int maxDescriptionLineLength = 30;
    int customModelData = 1;
    final SpellType type;
    static NamespacedKey spellKey = new NamespacedKey(Cobalt.getInstance(), "spell");

    boolean hasCast = false;

    // Shown Attributes
    int uses;
    int manaDrain;
    double castDelay;
    double rechargeTime;

    List<DelayedSpell> delayedSpells = new ArrayList<>();

    public Spell(int id, String internalSpellName, String spellName, SpellType type) {
        this.id = id;
        this.internalSpellName = internalSpellName;
        this.spellName = spellName;
        this.customModelData = id+1;
        this.type = type;
    }

    /**
     * Creates a copy of a spell
     * @param spell spell to copy
     */
    public Spell(Spell spell){
        this.id = spell.getId();
        this.internalSpellName = spell.getInternalSpellName();
        this.spellName = spell.getSpellName();
        this.customModelData = spell.getCustomModelData();
        this.type = spell.getSpellType();

        this.uses = spell.getUses();
        this.manaDrain = spell.getManaDrain();
        this.castDelay = spell.getCastDelay();
        this.rechargeTime = spell.getRechargeTime();
        this.description = spell.getDescription();
        this.hasCast = spell.getHasCast();
        this.delayedSpells = spell.getDelayedSpells();
    }

    /**
     * Performs operations that need to be done before a spell can be cast
     */
    @Override
    public void performPreCast(List<ISpell> wandSpells, int casts, int spellPos){
    }

    @Override
    public void castSpell(Wand wand, Player player) {

    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets a new <code>ItemStack</code> representing a castable spell
     * Automatically generates the lore for the spell
     *
     * @return a new <code>ItemStack</code> representing the spell
     */
    public ItemStack getSpellItem(){
        return getSpellItem(getLore());
    }

    /**
     * Gets a new <code>ItemStack</code> representing a castable spell, with the given lore
     *
     * @param lore lore to add to the item
     * @return a new <code>ItemStack</code> representing the spell
     */
    public ItemStack getSpellItem(List<String> lore) {

        lore.add("");
        lore.add(ChatColor.BLUE + type.name());

        ItemStack stack = new ItemStack(Material.CLOCK, 1);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(spellKey, PersistentDataType.INTEGER, id);

            meta.setDisplayName(type.spellColor + spellName);
            meta.setLore(lore);

            meta.setCustomModelData(customModelData);

            stack.setItemMeta(meta);
        }

        return stack;
    }

    /**
     * Returns the description of this spell formatted as a list of strings with a maximum length
     *
     * @return description formatted as a list of strings
     */
    public List<String> getFormattedDescription(){
        List<String> description = new ArrayList<>();

        StringBuilder currentLine = new StringBuilder();
        currentLine.append(ChatColor.ITALIC);
        currentLine.append(ChatColor.DARK_PURPLE);

        if (this.description == null) return description;

        for (int i = 0; i < this.description.length(); i++){
            if (this.description.charAt(i) == ' ' && currentLine.length() >= maxDescriptionLineLength){
                description.add(currentLine.toString());
                currentLine = new StringBuilder();

                currentLine.append(ChatColor.ITALIC);
                currentLine.append(ChatColor.DARK_PURPLE);
            } else {
                currentLine.append(this.description.charAt(i));
            }
        }
        description.add(currentLine.toString());
        description.add("");

        return description;
    }

    /**
     * Gets the basic lore for a spell
     *
     * @return list of Strings representing the lore of the spell
     */
    public List<String> getLore() {

        List<String> lore = new ArrayList<>(getFormattedDescription());

        if (manaDrain != 0) lore.add(colorizeValue("Mana Drain: ", manaDrain, ""));
        if (castDelay != 0) lore.add(colorizeValue("Cast Delay: ", castDelay, "s"));
        if (rechargeTime != 0) lore.add(colorizeValue("Recharge Time: ", rechargeTime, "s"));

        return lore;
    }

    /**
     * Colorizes a value with a prefix and a suffix
     * @param prefix prefix to put before the value
     * @param value value to color
     * @param suffix suffix to put after the value
     * @return string with the colorized value
     */
    public static String colorizeValue(String prefix, Object value, String suffix) {
        return ChatColor.WHITE + prefix + ChatColor.BLUE + value + ChatColor.WHITE + suffix;
    }

    public abstract void cancelTask();

    /**
     * Builds a new spell. Subclasses should extend this class when creating their own builders
     */
    protected static abstract class SpellBuilder<T extends Spell, B extends SpellBuilder> {

        T obj;

        int id;
        String internalSpellName;
        String spellName;
        String description;
        int manaDrain;

        double castDelay;
        double rechargeTime;

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id id of the spell
         * @param internalSpellName internal name of the spell
         */
        public SpellBuilder(int id, String internalSpellName){
            obj = createObj();

            this.id = id;
            this.internalSpellName = internalSpellName;
            generateSpellName();
        }

        public T build() {
            obj.id = id;
            obj.internalSpellName = internalSpellName;
            obj.spellName = spellName;
            return obj;
        }

        protected abstract T createObj();
        protected abstract B getThis();

        // TODO: Replace with regex
        private void generateSpellName(){
            String[] strings = internalSpellName.split("_");
            spellName = "";
            for (String s : strings){
                spellName += Character.toUpperCase(s.charAt(0)) + s.substring(1) + " ";
            }
            spellName = spellName.substring(0, spellName.length()-1);
        }

        public B setCustomModel(int modelId){
            obj.customModelData = modelId;
            return getThis();
        }

        public B addDescription(String description){
            obj.description = description;
            return getThis();
        }

        public B addManaDrain(int manaDrain){
            obj.manaDrain = manaDrain;
            return getThis();
        }

        public B addCastDelay(double castDelay){
            obj.castDelay = castDelay;
            return getThis();
        }

        public B addRechargeTime(double rechargeTime){
            obj.rechargeTime = rechargeTime;
            return getThis();
        }
    }

    /**
     * Clones a spell
     *
     * @return a clone of a spell
     */
    @Override
    public abstract Spell clone();

    @Override
    public String getDescription(){
        return description;
    }

    @Override
    public int getManaDrain() {
        return manaDrain;
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
    public boolean getHasCast() { return hasCast; }

    @Override
    public void setHasCast(boolean hasCast) { this.hasCast = hasCast; }

    @Override
    public String getInternalSpellName() {
        return internalSpellName;
    }

    @Override
    public String getSpellName() {
        return spellName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCustomModelData() {
        return customModelData;
    }

    @Override
    public SpellType getSpellType() {
        return type;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public List<DelayedSpell> getDelayedSpells() { return new ArrayList<>(delayedSpells); }

    @Override
    public int getTrueManaDrain(){
        int manaUsed = 0;
        for (Spell.DelayedSpell ds : delayedSpells){
            manaUsed += ds.getManaDrain();
        }
        return manaUsed + getManaDrain();
    }

    @Override
    public double getTrueCastDelay(){
        int castDelay = 0;
        for (Spell.DelayedSpell ds : delayedSpells){
            castDelay += ds.getCastDelay();
        }
        return castDelay + getCastDelay();
    }

    public enum SpellType{
        PROJECTILE(ChatColor.RED),
        STATIC_PROJECTILE(ChatColor.YELLOW),
        PASSIVE(ChatColor.GRAY),
        UTILITY(ChatColor.DARK_PURPLE),
        PROJECTILE_MODIFIER(ChatColor.BLUE),
        MATERIAL(ChatColor.GREEN),
        MULTICAST(ChatColor.AQUA),
        OTHER(ChatColor.GOLD);

        ChatColor spellColor;

        SpellType(ChatColor spellColor){
            this.spellColor = spellColor;
        }
    }

    public enum TriggerType{
        NONE,
        INSTANT,
        COLLISION,
        TIMER, // Executes after half of the lifetime // TODO: Change this (??)
        EXPIRATION
    }

    public static class DelayedSpell{
        boolean hasCast = false;
        List<ISpell> spellsToCast;
        TriggerType whenToCast;

        public DelayedSpell(List<ISpell> spellsToCast, TriggerType whenToCast){
            this.spellsToCast = spellsToCast;
            this.whenToCast = whenToCast;
        }

        public void setHasCast(boolean hasCast) { this.hasCast = hasCast; }

        public boolean getHasCast() { return hasCast; }

        public double getCastDelay(){
            double castDelay = 0;
            for (ISpell spell : spellsToCast){
                castDelay += spell.getTrueCastDelay();
            }
            return castDelay;
        }

        public int getManaDrain(){
            int manaDrain = 0;
            for (ISpell spell : spellsToCast){
                manaDrain += spell.getTrueManaDrain();
            }
            return manaDrain;
        }

        public List<ISpell> getSpellsToCast() { return spellsToCast; }

        public TriggerType getWhenToCast() { return whenToCast; }
    }
}
