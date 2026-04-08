package se.fusion1013.cobaltmagick.alchemy.cauldron;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.components.conditions.NearbyBlockCondition;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphData;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.AlchemyManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;
import se.fusion1013.cobaltmagick.alchemy.glyph.GlyphUtil;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionCreator;
import se.fusion1013.cobaltmagick.alchemy.potion.PotionManager;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

import java.util.*;

public class CauldronManager extends Manager<CobaltMagick> implements Listener {

    private static final Random random = new Random();
    private static final FileConfiguration CONFIG = CobaltMagick.getInstance().getConfig();
    private static final Map<Location, ICauldronInstance> CAULDRON_INSTANCES = new HashMap<>();

    private void clearCauldron(Location location, ICauldronInstance cauldronInstance, boolean success) {
        World world = location.getWorld();
        int amountCleared = cauldronInstance.clearHeldItems();
        if (amountCleared <= 0) return;
        if (success) {
            world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
            world.spawnParticle(Particle.HAPPY_VILLAGER, location.toCenterLocation(), 20, 1, 1, 1, 0);
        } else {
            world.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
            world.spawnParticle(Particle.SMOKE, location.toCenterLocation(), 20, 1, 1, 1, 0);
        }

        world.setBlockData(location, Material.CAULDRON.createBlockData());

        CAULDRON_INSTANCES.remove(location);
    }

    @EventHandler
    public void onCauldronChange(CauldronLevelChangeEvent event) {
        ICauldronInstance instance = getCauldronInstance(event.getBlock().getLocation());
        if (instance.getHeldItems().length == 0) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
//        if (!event.getPlayer().isOp() && !CONFIG.getBoolean("enable_alchemy")) return;

        Block block = event.getBlock();
        if (block.getType() != Material.WATER_CAULDRON) return;

        ICauldronInstance cauldronInstance = CAULDRON_INSTANCES.get(block.getLocation());
        if (cauldronInstance == null) return;
        CAULDRON_INSTANCES.remove(block.getLocation());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (!event.getPlayer().isOp() && !CONFIG.getBoolean("enable_alchemy")) return;

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material blockMaterial = block.getType();
        if (blockMaterial != Material.WATER_CAULDRON) return;

        if (block.getBlockData() instanceof Levelled levelled) {
            if (levelled.getLevel() == 0) return;
        }

        tryInsertCauldronItem(block.getLocation(), event);
    }

    private void tryInsertCauldronItem(Location location, PlayerInteractEvent event) {
        ICauldronInstance cauldronInstance = getCauldronInstance(location);

        if (!cauldronInstance.hasValidGlyph()) return;

        ItemStack heldItemStack = event.getItem();
        if (heldItemStack == null) return;

        if (heldItemStack.getType() == Material.BUCKET) return;
        if (heldItemStack.getType() == Material.WATER_BUCKET) return;
        if (heldItemStack.getType() == Material.LAVA_BUCKET) return;
        if (heldItemStack.getType() == Material.POTION) return;
        if (heldItemStack.getType().getMaxStackSize() == 1) return;

        Player player = event.getPlayer();
        World world = location.getWorld();

        ItemStack stackToInsert = heldItemStack.clone();
        boolean insertOne = player.isSneaking();
        if (insertOne) stackToInsert.setAmount(1);

        event.setCancelled(true);

        player.getInventory().getItemInMainHand().setAmount(heldItemStack.getAmount() - stackToInsert.getAmount());

        world.playSound(location, Sound.BLOCK_DECORATED_POT_INSERT, 1, 1);
        world.spawnParticle(Particle.POOF, location.toCenterLocation(), 5, .2, .2, .2, 0);

        if (stackToInsert.getType() == Material.GLASS_BOTTLE) {
            boolean success = finalizeCauldronRecipe(location, cauldronInstance, event);
            clearCauldron(location, cauldronInstance, success);
            return;
        }

        cauldronInstance.insertItem(stackToInsert);
    }

    private ICauldronInstance getCauldronInstance(Location location) {
        if (CAULDRON_INSTANCES.containsKey(location)) return CAULDRON_INSTANCES.get(location);
        CauldronInstance newCauldronInstance = new CauldronInstance(location);
        CAULDRON_INSTANCES.put(location, newCauldronInstance);
        return newCauldronInstance;
    }

    private boolean finalizeCauldronRecipe(Location location, ICauldronInstance instance, PlayerInteractEvent event) {
        ICauldronRecipe recipe = instance.getValidRecipe();
        CobaltMagick.getInstance().getLogger().info("Player " + event.getPlayer().getName() + " finished potion recipe " + recipe.getInternalName());

        CauldronState state = createCauldronState(location, instance, recipe);
        if (state == null) return false;


        if (state.getFailure() != 0 && random.nextInt(0, 100) < Math.min(98, state.getFailure())) {
            location.getWorld().createExplosion(location, 8, true);
        } else {
            recipe.execute(location, state, instance.getLowestItemCount());
        }

        decayBlocks(location, instance, state, recipe.getElementalAffinity());

        CAULDRON_INSTANCES.remove(location);

        return true;
    }

    public static CauldronState createCauldronState(Location location) {
        ICauldronInstance instance = CAULDRON_INSTANCES.get(location);
        if (instance == null) return null;
        return createCauldronState(location, instance, instance.getValidRecipe());
    }

    private static CauldronState createCauldronState(Location location, ICauldronInstance instance, ICauldronRecipe recipe) {
        if (recipe == null) return null;
        CauldronState state = new CauldronState();

        GlyphUtil.addGlyphToCauldronState(location, instance.getGlyphVectors(location), state);
        addNearbyBlocksToState(location, state);

        CobaltMagick.getInstance().getLogger().info(state.toString());

        return state;
    }

    private static void addNearbyBlocksToState(Location location, CauldronState state) {
        for (AlchemyBlockProperties property : AlchemyManager.getProperties()) {
            if (property.isInternal()) continue;
            addNearbyBlocksToState(location, state, property);
        }
    }

    private static void addNearbyBlocksToState(Location location, CauldronState state, AlchemyBlockProperties property) {
        for (Material material : property.getMaterials()) {
            int count = NearbyBlockCondition.findNearbyBlocks(location, location.getWorld(), new Vector(8, 8, 8), material);
            for (int i = 0; i < count; i++) {
                state.update(property);
            }
        }
    }

    private void decayBlocks(Location location, ICauldronInstance instance, CauldronState state, IElementalAffinity elementalAffinity) {
        int affinity = PotionCreator.getElementalAffinityLevel(location, Arrays.stream(elementalAffinity.getElementalAffinities()).toList());
        int decayBase = Math.max(state.getDecay() - (30 * affinity) - 20, 0);
        int decayGuaranteed = decayBase / 10;
        int decayExtraChance = decayBase % 10;
        int decayTotal = decayGuaranteed + (random.nextInt(10) < decayExtraChance ? 1 : 0);
        instance.decay(location, decayTotal);
    }

    public CauldronManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }

    public static List<ICauldronInstance> getCauldronInstances() {
        return CAULDRON_INSTANCES.values().stream().toList();
    }

    public static List<ICauldronRecipe> getCauldronRecipes() {
        List<ICauldronRecipe> recipes = new ArrayList<>();
        recipes.addAll(Arrays.stream(PotionManager.getInstance().getRecipes()).toList());
        return recipes;
    }

    public static ICauldronRecipe[] getRecipesMatchingGlyph(GlyphData glyph) {
        return getCauldronRecipes()
                .stream()
                .filter(r -> r.getGlyph().equalsIgnoreCase(glyph.category() + "." + glyph.name()))
                .toArray(ICauldronRecipe[]::new);
    }
}
