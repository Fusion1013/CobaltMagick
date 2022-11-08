package se.fusion1013.plugin.cobaltmagick.world.structures.crafting;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;
import se.fusion1013.plugin.cobaltmagick.wand.WandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Used by throwing an item on top of the <code>MagickAnvil</code> structure.
 * Supports multi-item inputs.
 */
public class MagickAnvil implements IStorageObject, Runnable {

    // ----- VARIABLES -----

    private static final ISpell[] POTENTIAL_SPELLS = new ISpell[] {
            SpellManager.SPARK_BOLT,
            SpellManager.BUBBLE_SPARK,
            SpellManager.BURST_OF_AIR
    };

    private UUID uuid;
    private Location location;

    private BukkitTask particleTask;

    // ----- CONSTRUCTORS -----

    public MagickAnvil() {}

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        this.particleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 5);
    }

    @Override
    public void onUnload() {
        if (particleTask != null) particleTask.cancel();
    }

    // ----- TICK -----

    @Override
    public void run() {
        location.getWorld().spawnParticle(Particle.END_ROD, location.toCenterLocation(), 2, .4, .4, .4, .05);
        location.getWorld().spawnParticle(Particle.SPELL_WITCH, location.toCenterLocation(), 1, .3, .3, .3, 0);
    }

    // ----- TRIGGER -----

    @Override
    public void onTrigger(Object... args) {
        Item item = (Item) args[0];

        if (item.getWorld() != location.getWorld()) return;
        if (item.getLocation().distanceSquared(location) >= 5*5) return;

        if (executeCraft(item)) {
            item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation(), 4, .1, .1, .1, .1);
            item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation(), 2, .1, .1, .1, .2);

            item.getWorld().playSound(item.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.BLOCKS, 1, 1);
        }
    }

    BukkitTask craftTask;
    int currentTick = 0;

    private boolean executeCraft(Item item) {
        ItemStack stack = item.getItemStack();

        if (
                !ItemManager.BROKEN_SPELL.compareTo(stack) &&
                !ItemManager.BROKEN_WAND.compareTo(stack) &&
                !CustomItemManager.getCustomItem(stack).getInternalName().equalsIgnoreCase("emerald_tablet_i") &&
                !CustomItemManager.getCustomItem(stack).getInternalName().equalsIgnoreCase("emerald_tablet_ii")
        ) return false;

        Random r = new Random();

        craftTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), () -> {

            // Float item & Give properties
            item.teleport(location.toCenterLocation().add(0, 1, 0));
            item.setVelocity(new Vector());
            item.setCanPlayerPickup(false);
            item.setCanMobPickup(false);
            item.setGlowing(true);
            item.setGravity(false);

            // Particle Effects
            item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation().clone().add(0, .5, 0), 2, .1, .1, .1, .05);
            item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation().clone().add(0, .5, 0), 1, .1, .1, .1, .1);

            // Cast spells in random directions
            if (currentTick % 4 == 0) {
                ISpell spell = POTENTIAL_SPELLS[r.nextInt(POTENTIAL_SPELLS.length)];
                spell.clone().castSpell(null, null, GeometryUtil.getPointOnSphere(1), item.getLocation());
            }

            // Cancel task if tick == 100
            if (currentTick >= 100) {
                // Spawn new item
                if (ItemManager.BROKEN_SPELL.compareTo(stack)) {
                    item.setItemStack(SpellManager.REPAIRED_SPELL.getSpellItem());
                }

                if (ItemManager.BROKEN_WAND.compareTo(stack)) {
                    int level = 5;
                    int cost = 20 * level;
                    Wand wand = WandManager.getInstance().createWand(cost, level, true);
                    item.setItemStack(wand.getWandItem());

                    // Grant advancement
                    MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);
                    if (advancementManager == null) return;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().distanceSquared(location) > 50 * 50) continue;

                        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> advancementManager.grantAdvancement(player, "progression", "broken_wand"), 10);
                    }
                }

                if (CustomItemManager.getCustomItem(stack).getInternalName().equalsIgnoreCase("emerald_tablet_i")) {
                    item.setItemStack(CustomItemManager.getCustomItemStack("emerald_tablet_i_reforged"));
                }

                if (CustomItemManager.getCustomItem(stack).getInternalName().equalsIgnoreCase("emerald_tablet_ii")) {
                    item.setItemStack(CustomItemManager.getCustomItemStack("emerald_tablet_ii_reforged"));
                }

                // Item Spawn effects
                item.getWorld().spawnParticle(Particle.END_ROD, item.getLocation().clone().add(0, .5, 0), 40, .1, .1, .1, .5);
                item.getWorld().spawnParticle(Particle.SPELL_WITCH, item.getLocation().clone().add(0, .5, 0), 20, .1, .1, .1, .6);

                // Sound
                item.getWorld().playSound(item.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.BLOCKS, 1, 1);

                // Reset item
                item.setGravity(true);
                item.setCanPlayerPickup(true);
                item.setCanMobPickup(true);

                // Cancel task
                if (craftTask != null) {
                    currentTick = 0;
                    craftTask.cancel();
                }
            }

            currentTick++;

        }, 0, 1);

        return true;
    }

    // ----- JSON INTEGRATION -----

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

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        // TODO
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
        };
    }

    // ----- GETTERS / SETTERS -----

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

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MagickAnvil(MagickAnvil target) {
        this.uuid = target.uuid;
        this.location = target.location;

        if (target.craftTask != null) this.craftTask = target.craftTask;
        this.currentTick = target.currentTick;
        if (target.particleTask != null) this.particleTask = target.particleTask;
    }

    @Override
    public MagickAnvil clone() {
        return new MagickAnvil(this);
    }
}
