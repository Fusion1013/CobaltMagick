package se.fusion1013.cobaltmagick.alchemy.potion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionManager extends Manager<CobaltMagick> implements Listener {

    private static final Map<String, IPotionRecipe> RECIPES = new HashMap<>();

    private static final IPotionRecipe TEST_RECIPE = register(new PotionRecipe(
            "test_recipe", PotionEffectType.REGENERATION, new String[]{"sugar", "rabbit_foot", "glass_bottle"}, "alchemy.sun", List.of()
    ));

    public PotionManager(CobaltMagick plugin) {
        super(plugin);
    }

    private static IPotionRecipe register(IPotionRecipe recipe) {
        RECIPES.put(recipe.getInternalName(), recipe);
        return recipe;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!event.getPlayer().isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        Material blockMaterial = block.getType();
        if (blockMaterial != Material.CAULDRON) return;

        for (IPotionRecipe recipe : RECIPES.values()) {
            if (recipe.validateGlyph(block.getLocation())) {
                CauldronState state = new CauldronState();
                recipe.addGlyphToCauldronState(block.getLocation(), state);

                event.getPlayer().sendMessage("Variance: " + state.getVariance());
                event.getPlayer().sendMessage("Potency: " + state.getPotency());
                event.getPlayer().sendMessage("Duration: " + state.getDuration());
                event.getPlayer().sendMessage("Wild: " + state.getWild());
                event.getPlayer().sendMessage("Decay: " + state.getDecay());
            }
        }
    }

    public IPotionRecipe getRecipeMatchingGlyph(Location cauldronLocation) {
        for (IPotionRecipe recipe : RECIPES.values()) {
            if (recipe.validateGlyph(cauldronLocation)) return recipe;
        }
        return null;
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }

    private static PotionManager INSTANCE;

    public static PotionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PotionManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
