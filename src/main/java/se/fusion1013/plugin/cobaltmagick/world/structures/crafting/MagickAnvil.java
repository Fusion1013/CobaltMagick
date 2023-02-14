package se.fusion1013.plugin.cobaltmagick.world.structures.crafting;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.crafting.ICobaltRecipe;
import se.fusion1013.plugin.cobaltcore.item.crafting.RecipeManager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.crafting.custom.MagickAnvilRecipe;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

import java.util.*;

/**
 * Used by throwing an item on top of the <code>MagickAnvil</code> structure.
 * Supports multi-item inputs.
 */
public class MagickAnvil implements IStorageObject, Runnable {

    //region FIELDS

    private static final NamespacedKey IGNORE_ITEM_KEY = new NamespacedKey(CobaltMagick.getInstance(), "magick_anvil_ignore");

    private static final String[] POTENTIAL_SPELLS = new String[] {
            "spark_bolt",
            "bubble_spark",
            "burst_of_air"
    };

    private boolean canPickupItems = true;

    private UUID uuid;
    private Location location;

    private BukkitTask particleTask;

    private List<Item> heldItems = new ArrayList<>();
    private List<String> heldItemNames = new ArrayList<>();

    // Craft task
    private BukkitTask craftTask;
    private int currentTick = 0;
    private int craftTick;

    //endregion

    //region CONSTRUCTORS

    public MagickAnvil() {}

    //endregion

    //region LOADING/UNLOADING

    @Override
    public void onLoad() {
        this.particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 5);
    }

    @Override
    public void onUnload() {
        if (particleTask != null) particleTask.cancel();
    }

    //endregion

    //region TICK

    @Override
    public void run() {
        location.getWorld().spawnParticle(Particle.END_ROD, location.toCenterLocation(), 2, .4, .4, .4, .05);
        location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.toCenterLocation(), 1, .3, .3, .3, 0);
    }

    //endregion

    //region TRIGGER

    @Override
    public void onTrigger(Object... args) {
        Item item = (Item) args[0];

        if (!canPickupItems) return;

        if (item.getWorld() != location.getWorld()) return;
        if (item.getLocation().distanceSquared(location) >= 5*5) return;
        if (item.getItemStack().getItemMeta().getPersistentDataContainer().has(IGNORE_ITEM_KEY)) return;

        addCraftItem(item);
    }

    private void addCraftItem(Item item) {

        // Add items to the lists
        heldItems.add(item);
        String itemName = CustomItemManager.getItemName(item.getItemStack());
        heldItemNames.add(itemName);
        Collections.sort(heldItemNames);

        // Prepare the item entity
        toggleItemEffects(item, true);

        // Create the crafting loop task if it does not already exist
        if (craftTask == null) startIdleLoop();
        else if (craftTask.isCancelled()) startIdleLoop();

        // Spawn particles at the newly added item
        item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation(), 4, .1, .1, .1, .1);
        item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 2, .1, .1, .1, .2);

        item.getWorld().playSound(item.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.BLOCKS, 1, 1);
    }

    //endregion

    //region IDLE/CRAFTING LOOP

    private void startIdleLoop() {
        Random r = new Random();
        craftTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), () -> idleLoop(r), 0, 1);
    }

    private void idleLoop(Random r) {
        // Run item-specific idle effects
        for (Item item : heldItems) {
            item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation().clone().add(0, .5, 0), 2, .1, .1, .1, .05);
            item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation().clone().add(0, .5, 0), 1, .1, .1, .1, .1);
        }

        // Attempt to craft an item
        craftLoop(r);

        // If it has been more than 20 seconds, and it is not currently crafting, abort the craft
        if (currentTick >= 200 && craftTick <= 0) {
            for (Item item : heldItems) toggleItemEffects(item, false);
            heldItems.clear();
            heldItemNames.clear();

            cancelTask();
        }

        currentTick++;
    }

    private void craftLoop(Random r) {
        Location center = location.toCenterLocation().add(0, 1, 0);

        // Try to find a valid recipe
        MagickAnvilRecipe recipe = findRecipe();
        if (recipe == null) return;

        canPickupItems = false;

        // Run item-specific crafting effects
        for (Item item : heldItems) spawnRandomSpell(r, item.getLocation());

        // Complete the craft
        if (craftTick >= 100) {

            // Spawn new items
            clearInput();
            spawnItems(recipe.getOutput(), center);
            cancelTask();
        }

        craftTick++;
    }

    private void cancelTask() {
        // Cancel task
        if (craftTask != null) {
            currentTick = 0;
            craftTick = 0;
            craftTask.cancel();
        }
    }

    private void spawnRandomSpell(Random r, Location location) {
        // Cast spells in random directions
        if (currentTick % 4 == 0) {
            String spellName = POTENTIAL_SPELLS[r.nextInt(POTENTIAL_SPELLS.length)];
            ISpell spell = SpellManager.getSpell(spellName);
            if (spell != null) spell.clone().castSpell(null, null, GeometryUtil.getPointOnSphere(1), location);
        }
    }

    private MagickAnvilRecipe findRecipe() {
        Map<String, ICobaltRecipe> recipes = RecipeManager.getRecipesOfType("magick_anvil");

        for (String s : recipes.keySet()) {
            ICobaltRecipe recipe = recipes.get(s);
            if (recipe instanceof MagickAnvilRecipe magickAnvilRecipe) {

                List<String> recipeItemNames = new ArrayList<>();

                for (ItemStack stack : magickAnvilRecipe.getInput()) {
                    recipeItemNames.add(CustomItemManager.getItemName(stack));
                }

                Collections.sort(recipeItemNames);

                if (recipeItemNames.equals(heldItemNames)) return magickAnvilRecipe;
            }
        }

        return null;
    }

    //endregion

    //region ITEM MANAGEMENT

    private void toggleItemEffects(Item item, boolean isActive) {
        if (isActive) item.teleport(location.toCenterLocation().add(0, 1, 0));
        if (isActive) item.setVelocity(new Vector());
        item.setCanPlayerPickup(!isActive);
        item.setCanMobPickup(!isActive);
        item.setGlowing(isActive);
        item.setGravity(!isActive);
    }

    private void clearInput() {
        for (Item item : heldItems) Bukkit.getScheduler().runTask(CobaltMagick.getInstance(), item::remove);
        heldItems.clear();
        heldItemNames.clear();
    }

    private void spawnItems(ItemStack[] items, Location location) {
        World world = location.getWorld();

        Bukkit.getScheduler().runTask(CobaltMagick.getInstance(), () -> {
            // Spawn items
            for (ItemStack itemStack : items) {
                world.spawn(location, Item.class, item -> {
                    item.setItemStack(itemStack);
                    item.setGlowing(true);
                    item.getPersistentDataContainer().set(IGNORE_ITEM_KEY, PersistentDataType.BYTE, (byte)1);
                });
            }

            // Item Spawn effects
            world.spawnParticle(Particle.END_ROD, location, 40, .1, .1, .1, .5);
            world.spawnParticle(Particle.SPELL_WITCH, location, 20, .1, .1, .1, .6);
            world.playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.BLOCKS, 1, 1);

            canPickupItems = true;
        });
    }

    //endregion

    //region JSON INTEGRATION

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("location", JsonUtil.toJson(location));
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        this.location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));
    }

    //endregion

    //region COMMAND INTEGRATION

    @Override
    public void fromCommandArguments(Object[] objects) {
        // TODO
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
        };
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public void setValue(String s, Object o) {
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        info.add("Location: " + location.toVector());
        return info;
    }

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectIdentifier() {
        return "magick_anvil";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    //endregion

    //region CLONE CONSTRUCTOR & METHOD

    public MagickAnvil(MagickAnvil target) {
        this.canPickupItems = target.canPickupItems;

        this.uuid = target.uuid;
        this.location = target.location;

        if (target.craftTask != null) this.craftTask = target.craftTask;
        this.currentTick = target.currentTick;
        this.craftTick = target.craftTick;

        heldItems = target.heldItems;
        heldItemNames = target.heldItemNames;

        if (target.particleTask != null) this.particleTask = target.particleTask;
    }

    @Override
    public MagickAnvil clone() {
        return new MagickAnvil(this);
    }

    //endregion
}
