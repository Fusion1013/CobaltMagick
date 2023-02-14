package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.item.system.ItemRarity;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Spell implements ISpell, Cloneable {

    // Hidden Attributes
    double radius = .1;

    int id;
    String internalSpellName;
    String spellName;
    String description;
    static final int maxDescriptionLineLength = 30;
    int customModelData = 1;
    SpellType type;
    public static NamespacedKey SPELL_KEY = new NamespacedKey(CobaltMagick.getInstance(), "spell");
    int[] spellTiers;
    double[] spellTierWeights;

    boolean hasCast = false;

    int count = 1; // The number of spells in the itemstack

    // Spell Item
    private ICustomItem spellItem;

    // Shown Attributes
    boolean consumeOnUse;
    int manaDrain;
    double castDelay;
    double rechargeTime;

    List<DelayedSpell> delayedSpells = new ArrayList<>();

    LivingEntity caster;
    Wand wand;

    List<String> tags = new ArrayList<>();

    //region CONSTRUCTORS

    public Spell(int id, String internalSpellName, SpellType type, Map<?, ?> data) {
        this.id = id;
        this.internalSpellName = internalSpellName;
        generateSpellName();
        this.type = type;

        // Data loading
        if (data.containsKey("radius")) radius = (double) data.get("radius");
        if (data.containsKey("description")) description = (String) data.get("description");

        if (data.containsKey("spell_tiers")) {
            List<Integer> tiers = ((ArrayList<Integer>) data.get("spell_tiers"));
            spellTiers = new int[tiers.size()];
            for (int i = 0; i < tiers.size(); i++) spellTiers[i] = tiers.get(i);
        }
        if (data.containsKey("spell_tier_weights")) {
            List<Double> tierWeights = (ArrayList<Double>) data.get("spell_tier_weights");
            spellTierWeights = new double[tierWeights.size()];
            for (int i = 0; i < tierWeights.size(); i++) spellTierWeights[i] = tierWeights.get(i);
        }

        if (data.containsKey("count")) count = (int) data.get("count");

        if (data.containsKey("consume_on_use")) consumeOnUse = (boolean) data.get("consume_on_use");
        if (data.containsKey("mana_drain")) manaDrain = (int) data.get("mana_drain");

        if (data.containsKey("cast_delay")) castDelay = (double) data.get("cast_delay");
        if (data.containsKey("recharge_time")) rechargeTime = (double) data.get("recharge_time");

        // TODO: Tags
    }

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

        this.manaDrain = spell.getManaDrain();
        this.castDelay = spell.getCastDelay();
        this.rechargeTime = spell.getRechargeTime();
        this.description = spell.getDescription();
        this.hasCast = spell.getHasCast();
        this.delayedSpells = spell.getDelayedSpells();
        this.consumeOnUse = spell.getConsumeOnUse();

        this.count = spell.getCount();

        this.radius = spell.getRadius();

        this.caster = spell.getCaster();
        this.wand = spell.getWand();

        this.tags = spell.getTags();

        this.spellItem = spell.spellItem;
        this.spellTiers = spell.spellTiers;
    }

    //endregion

    // ----- CASTING METHODS -----

    /**
     * Performs operations that need to be done before a spell can be cast
     */
    @Override
    public void performPreCast(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos){
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster) {
        this.wand = wand;
        this.caster = caster;
    }

    // ----- ITEM CREATION -----

    private ICustomItem createSpellItem() {
        var customItem = CustomItemManager.getCustomItem(internalSpellName);
        if (customItem != null) return customItem;

        List<String> lore = getLore();
        lore.add("");
        lore.add(type.spellColor + type.name().replaceAll("_", " "));

        CobaltItem.Builder itemBuilder = new CobaltItem.Builder(internalSpellName)
                .material(Material.CLOCK)
                .amount(count)
                .editMeta(itemMeta -> {
                    itemMeta.getPersistentDataContainer().set(SPELL_KEY, PersistentDataType.INTEGER, id);
                    return itemMeta;
                })
                .rarity(ItemRarity.MYSTIC)
                .rarityLore(lore.toArray(new String[0]))
                .modelData(customModelData)
                .category(MagickItemCategory.SPELL);

        if (consumeOnUse) itemBuilder.itemName(type.spellColor + spellName + " (-)");
        else itemBuilder.itemName(type.spellColor + spellName);

        return itemBuilder.build();
    }

    public String getFormattedName() {
        if (consumeOnUse) return type.spellColor + spellName + " (-)";
        else return type.spellColor + spellName;
    }

    // ----- GETTERS / SETTERS -----

    public String getHexIcon() {

        String reducedId = String.valueOf(id);
        reducedId = reducedId.substring(1);

        String typeId = String.valueOf(id);
        typeId = typeId.substring(0, 1);

        int hexCode = 57344 + Integer.parseInt(reducedId) + (256 * (Integer.parseInt(typeId) - 1));
        char[] chars = Character.toChars(hexCode);
        StringBuilder hexString = new StringBuilder();
        for (char c : chars) hexString.append(c);
        return hexString.toString();
    }

    /**
     * Gets a new <code>ItemStack</code> representing a castable spell
     * Automatically generates the lore for the spell
     *
     * @return a new <code>ItemStack</code> representing the spell
     */
    public ItemStack getSpellItem(){
        if (spellItem == null) {
            spellItem = createSpellItem();
        }
        ItemStack stack = spellItem.getItemStack();
        stack.setAmount(count);
        return stack;
    }

    /**
     * Gets the <code>CustomItem</code> representing this spell, or creates it if it does not yet exist.
     *
     * @return the <code>CustomItem</code> representing this spell.
     */
    @Override
    public ICustomItem getSpellCustomItem() {
        if (spellItem == null) this.spellItem = createSpellItem();
        return spellItem;
    }

    /**
     * Gets a new <code>ItemStack</code> representing a castable spell, with the given lore
     *
     * @param lore lore to add to the item
     * @return a new <code>ItemStack</code> representing the spell
     */
    public ItemStack getSpellItem(List<String> lore) {

        return CustomItemManager.getItemStack(internalSpellName);

        /*
        lore.add("");
        lore.add(type.spellColor + type.name().replaceAll("_", " "));

        ItemStack stack = new ItemStack(Material.CLOCK, count);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(SPELL_KEY, PersistentDataType.INTEGER, id);

            if (consumeOnUse) meta.setDisplayName(type.spellColor + spellName + " (-)");
            else meta.setDisplayName(type.spellColor + spellName);
            meta.setLore(lore);

            meta.setCustomModelData(customModelData);

            stack.setItemMeta(meta);
        }

        return stack;
         */
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
        SpellType overrideSpellType;

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

        private void generateSpellName(){
            String[] strings = internalSpellName.split("_");
            spellName = "";
            for (String s : strings){
                spellName += Character.toUpperCase(s.charAt(0)) + s.substring(1) + " ";
            }
            spellName = spellName.substring(0, spellName.length()-1);
        }

        protected abstract T createObj();
        protected abstract B getThis();

        public B setSpellTiers(int... tier) {
            obj.setSpellTiers(tier);
            return getThis();
        }

        public B setSpellTierWeights(double... spellTierWeights) {
            obj.setSpellTierWeights(spellTierWeights);
            return getThis();
        }

        public B addTag(String tag){
            obj.addTag(tag);
            return getThis();
        }

        public B setRadius(double radius){
            obj.setRadius(radius);
            return getThis();
        }

        public B overrideSpellName(String name) {
            this.spellName = name;
            return getThis();
        }

        /**
         * Overrides the type of this spell
         *
         * @param spellType type to override to
         * @return the builder
         */
        public B overrideSpellType(SpellType spellType){
            obj.setSpellType(spellType);
            return getThis();
        }

        public B consumeOnUse(int defaultCount){
            obj.consumeOnUse = true;
            obj.setCount(defaultCount);
            return getThis();
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

    protected void generateSpellName(){
        String[] strings = internalSpellName.split("_");
        spellName = "";
        for (String s : strings){
            spellName += Character.toUpperCase(s.charAt(0)) + s.substring(1) + " ";
        }
        spellName = spellName.substring(0, spellName.length()-1);
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
    public void setHasCast(boolean hasCast) {
        this.hasCast = hasCast;
    }

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
    public boolean getConsumeOnUse() {
        return consumeOnUse;
    }

    @Override
    public void setConsumeOnUse(boolean consume) {
        this.consumeOnUse = consume;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public List<DelayedSpell> getDelayedSpells() { return new ArrayList<>(delayedSpells); }

    @Override
    public double getRadius(){
        return this.radius;
    }

    @Override
    public void setRadius(double radius){
        this.radius = radius;
    }

    @Override
    public Wand getWand() {
        return this.wand;
    }

    @Override
    public LivingEntity getCaster() {
        return this.caster;
    }

    @Override
    public int getTrueManaDrain(){
        int manaUsed = 0;
        for (Spell.DelayedSpell ds : delayedSpells){
            manaUsed += ds.getManaDrain();
        }
        return manaUsed + getManaDrain();
    }

    @Override
    public void setSpellTiers(int... spellTier) {
        this.spellTiers = spellTier;
    }

    @Override
    public void setSpellTierWeights(double... spellTierWeights) {
        this.spellTierWeights = spellTierWeights;
    }

    @Override
    public int[] getSpellTiers() {
        if (spellTiers == null) return new int[0];
        else return spellTiers;
    }

    @Override
    public double[] getSpellTierWeights() {
        if (spellTierWeights == null) return new double[0];
        else return spellTierWeights;
    }

    @Override
    public double getTrueCastDelay(){
        int castDelay = 0;
        for (Spell.DelayedSpell ds : delayedSpells){
            castDelay += ds.getCastDelay();
        }
        return castDelay + getCastDelay();
    }

    public enum TriggerType{
        NONE,
        INSTANT,
        COLLISION,
        TIMER, // Executes after half of the lifetime // TODO: Change this (??)
        EXPIRATION,
        COLLISIONOREXPIRATION
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

    public void setSpellType(SpellType spellType){
        this.type = spellType;
    }

    public void addTag(String tag){
        tags.add(tag);
    }

    public List<String> getTags(){
        return new ArrayList<>(tags);
    }

    public void setCaster(LivingEntity caster){
        this.caster = caster;
    }
}
