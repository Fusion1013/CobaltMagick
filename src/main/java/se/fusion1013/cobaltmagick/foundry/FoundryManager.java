package se.fusion1013.cobaltmagick.foundry;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltCore.util.PlayerUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.pedestal.PedestalManager;

public class FoundryManager extends Manager<CobaltMagick> implements Listener {

    private static final FileLoadedRegistry<IFoundryRecipe> FOUNDRY_RECIPES = new FileLoadedRegistry<>(CobaltMagick.getInstance(),
            "foundry_recipes",
            FoundryRecipe::create,
            FoundryRecipe::create,
            (p, r) -> {
                p.sendMessage("Loaded recipe: " + r.getDisplayName());
            });

    public static final NamespacedKey FOUNDRY_TEMPLATE_KEY = new NamespacedKey(CobaltMagick.getInstance(), "foundry_template");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        if (event.getHand().getGroup() != EquipmentSlotGroup.MAINHAND) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        PersistentDataContainerView persistentDataContainer = item.getPersistentDataContainer();

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.ANVIL) return;

        Location blockLocation = event.getClickedBlock().getLocation();
        if (blockLocation.clone().add(0, -2, 0).getBlock().getType() != Material.STRUCTURE_BLOCK) return;

        // Template
        if (persistentDataContainer.has(FOUNDRY_TEMPLATE_KEY)) {
            event.setCancelled(true);
            tryPlaceTemplateFromItem(event.getPlayer(), item, blockLocation, persistentDataContainer.get(FOUNDRY_TEMPLATE_KEY, PersistentDataType.STRING));
            return;
        }

        // Mold
        if (persistentDataContainer.has(new NamespacedKey(CobaltCore.getInstance(), "mold"))) {
            event.setCancelled(true);
            tryTriggerFoundry(event.getPlayer(), item, block.getLocation());
        }
    }

    private void tryTriggerFoundry(Player player, ItemStack moldItem, Location blockLocation) {
        for (IFoundryRecipe recipe : FOUNDRY_RECIPES.values()) {
            if (!recipe.isCorrectMold(moldItem)) continue;
            if (!recipe.validate(blockLocation)) continue;

            PlayerUtil.reduceHeldItemStack(player, 1);

            recipe.execute(blockLocation);

            return;
        }

        blockLocation.getWorld().playSound(blockLocation, Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
    }

    private boolean tryPlaceTemplateFromItem(Player player, ItemStack item, Location blockLocation, @Nullable String recipeName) {
        IFoundryRecipe recipe = getRecipe(recipeName);
        if (recipe == null) return false;
        if (player.getCooldown(item) > 0) return false;

        player.setCooldown(item, 40);
        clearFoundryHousings(blockLocation);
        recipe.placeTemplate(blockLocation, true);
        return true;
    }

    public static void clearFoundryHousings(Location center) {
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location blockLocation = center.clone().add(x, 0, z);
                if (blockLocation.getBlock().getType() == Material.ANVIL) continue;
                PedestalManager.remove(blockLocation.getBlock());
            }
        }
    }

    public FoundryManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
        FOUNDRY_RECIPES.reload();
    }

    @Override
    public void disable() {

    }

    public static int getRecipeAmount() {
        return FOUNDRY_RECIPES.values().size();
    }

    public static String[] getRecipeNames() {
        return FOUNDRY_RECIPES.getNames();
    }

    public static IFoundryRecipe getRecipe(String name) {
        return FOUNDRY_RECIPES.get(name);
    }

    private static FoundryManager INSTANCE;

    public static FoundryManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FoundryManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
