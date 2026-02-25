package se.fusion1013.cobaltmagick.alchemy.cauldron;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.PotionUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.potion.IPotionRecipe;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CauldronManager extends Manager<CobaltMagick> implements Listener {

    private static final Random random = new Random();
    private static final Map<Location, ICauldronInstance> CAULDRON_INSTANCES = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material blockMaterial = block.getType();
        if (blockMaterial != Material.CAULDRON) return;

        if (block instanceof Levelled levelled) {
            if (levelled.getLevel() == 0) return;
        }

        tryTriggerCauldron(block.getLocation(), event);
    }

    private void tryTriggerCauldron(Location location, PlayerInteractEvent event) {
        ICauldronInstance instance = CAULDRON_INSTANCES.get(location);
        boolean runEffectsIfInvalid = true;
        boolean insertNewInstance = false;

        if (instance == null) {
            // Create a new cauldron instance
            IPotionRecipe recipe = PotionManager.getInstance().getRecipeMatchingGlyph(location);
            if (recipe == null) return;
            instance = new CauldronInstance(recipe);
            runEffectsIfInvalid = false;
            insertNewInstance = true;
        }

        ItemStack heldItem = event.getItem();
        if (heldItem == null) return;

        ItemStack requiredItem = instance.getCurrentRequiredItem();

        boolean isCorrectRecipe = CustomItemManager.compare(heldItem, requiredItem);
        isCorrectRecipe = isCorrectRecipe && instance.potionRecipe().validateGlyph(location);

        if (!isCorrectRecipe && !runEffectsIfInvalid) return;

        Player player = event.getPlayer();
        World world = location.getWorld();

        event.setCancelled(true);
        player.getInventory().getItemInMainHand().setAmount(heldItem.getAmount() - 1);
        if (isCorrectRecipe) {
            world.playSound(location, Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);
            instance.incrementProgress();

            if (instance.isRecipeDone()) finalizeCauldronRecipe(location, instance, event);
        } else {
            world.playSound(location, Sound.BLOCK_DECORATED_POT_INSERT_FAIL, 1, 1);
            CAULDRON_INSTANCES.remove(location); // Discard progress if invalid item
        }

        if (insertNewInstance) CAULDRON_INSTANCES.put(location, instance);
    }

    private void finalizeCauldronRecipe(Location location, ICauldronInstance instance, PlayerInteractEvent event) {
        CauldronState state = new CauldronState();
        instance.potionRecipe().addGlyphToCauldronState(location, state);

        givePotionItem(instance, state, event);
        decayBlocks(location, instance, state);

        CAULDRON_INSTANCES.remove(location);
    }

    private void decayBlocks(Location location, ICauldronInstance instance, CauldronState state) {
        int decayBase = Math.max(state.getDecay() - 50, 0);
        int decayGuaranteed = decayBase / 10;
        int decayExtraChance = decayBase % 10;
        int decayTotal = decayGuaranteed + random.nextInt(10) < decayExtraChance ? 1 : 0;
        instance.potionRecipe().decay(location, decayTotal);
    }

    private static void givePotionItem(ICauldronInstance instance, CauldronState state, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PotionEffectType effectType = instance.potionRecipe().getPotionEffectType();
        ItemStack potionItem = new ItemStack(Material.POTION);

        int duration = 8 * 20 + Math.max(state.getDuration(), 1) * 2 * 20; // TODO ???

        PotionEffectTypeCategory resultCategory = effectType.getCategory();
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

        // VARIANCE
        int extraPotionsCount = state.getVariance() / 50;
        for (int i = 0; i < extraPotionsCount; i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.BENEFICIAL : PotionEffectTypeCategory.HARMFUL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (duration * 1.2f), 0), false);
        }

        // WILD
        int wildPotionsCount = Math.max(0, state.getWild()) / 50;
        for (int i = 0; i < wildPotionsCount; i++) {
            PotionEffectType type = PotionUtil.getRandomInCategory(resultCategory == PotionEffectTypeCategory.HARMFUL ? PotionEffectTypeCategory.HARMFUL : PotionEffectTypeCategory.BENEFICIAL);
            potionMeta.addCustomEffect(new PotionEffect(type, (int) (duration * 0.2f), 0), false);
        }

        int amplifier = Math.max(0, state.getPotency()) / 100;

        potionMeta.addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);

        potionMeta.customName(
                Component.text("Potion of ")
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.translatable(effectType.translationKey())
                                .decoration(TextDecoration.ITALIC, false))
        );

        potionItem.setItemMeta(potionMeta);

        player.getInventory().addItem(potionItem);
    }

    private void tickCauldrons() {
        CAULDRON_INSTANCES.forEach(CauldronManager::tickCauldron);
    }

    private static void tickCauldron(Location location, ICauldronInstance cauldronInstance) {
        World world = location.getWorld();
        world.spawnParticle(Particle.LAVA, location.toCenterLocation(), 1, 0, 0, 0, 0);
    }

    public CauldronManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {
            tickCauldrons();
        }, 0, 1);
    }

    @Override
    public void disable() {

    }
}
