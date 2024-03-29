package se.fusion1013.plugin.cobaltmagick.item.create;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.MagickItemCategory;

import java.util.Objects;

import static se.fusion1013.plugin.cobaltcore.item.CustomItemManager.register;

public class CreateTools {

    public static void create() {}

    /*
    public static ICustomItem RUNE_LOCK_OVERRIDE_MODULE = register(new CobaltItem.Builder("rune_lock_override_module")
            .material(Material.EMERALD)
            .itemName(HexUtils.colorify("&bRune Lock Override Module"))
            .extraLore(
                    HexUtils.colorify("&o&7Reads & mimics the signals of runes"),
                    HexUtils.colorify("&o&7required by a Rune Lock")
            )
            .modelData(1009)
            // .addShapedRecipe("-%-", "%&%", "-%-",
            //         new AbstractCustomItem.ShapedIngredient('-', Material.REDSTONE),
            //         new AbstractCustomItem.ShapedIngredient('%', CreateMaterialIngots.HEPATIZON_INGOT.getItemStack()),
            //         new AbstractCustomItem.ShapedIngredient('&', Material.CYAN_STAINED_GLASS_PANE)
            // )
            .category(MagickItemCategory.TOOL)
            .build());

    public static ICustomItem TONGS = register(new CustomItem.CustomItemBuilder("tongs", Material.WOODEN_AXE, 1)
            .setCustomName(HexUtils.colorify("&8Tongs"))
            .addShapedRecipe(
                    "#-#",
                    "-%-",
                    "%-%",
                    new AbstractCustomItem.ShapedIngredient('%', Material.STICK),
                    new AbstractCustomItem.ShapedIngredient('#', Material.IRON_INGOT)
            )
            .setCustomModel(1).setItemCategory(MagickItemCategory.TOOL).build());

    public static ICustomItem HAMMER = register(new CustomItem.CustomItemBuilder("hammer", Material.STONE_AXE, 1)
            .setCustomName(HexUtils.colorify("&8Hammer"))
            .addShapedRecipe(
                    "-#-",
                    "-%#",
                    "%--",
                    new AbstractCustomItem.ShapedIngredient('%', Material.STICK),
                    new AbstractCustomItem.ShapedIngredient('#', Material.IRON_INGOT)
            )
            .setCustomModel(1).setItemCategory(MagickItemCategory.TOOL).build());
     */

    // Item Used to Reactivate Sculk Shrieker
    /*
    public static final ICustomItem SHRIEKER_CATALYST = register(new CobaltItem.Builder("shrieker_catalyst")
            .material(Material.CLOCK)
            .itemName(HexUtils.colorify("&eShrieker Catalyst"))
            .addShapedRecipe("-*-", "*%*", "-*-", new AbstractCustomItem.ShapedIngredient('*', ECHO_INGOT.getItemStack()), new AbstractCustomItem.ShapedIngredient('%', Material.SCULK_CATALYST))
            .addItemActivator(ItemActivator.PLAYER_RIGHT_CLICK_BLOCK, (item, event, slot) -> {
                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;

                ICustomItem interactedCustom = CustomItemManager.getCustomItem(interactEvent.getItem());
                if (interactedCustom == null) return;
                if (!interactedCustom.compareTo(item.getItemStack())) return;

                Block block = interactEvent.getClickedBlock();
                if (block == null) return;
                if (block.getType() == Material.SCULK_SHRIEKER) {
                    CobaltCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> {
                        // Create the block data
                        BlockData data = CobaltCore.getInstance().getServer().createBlockData("minecraft:sculk_shrieker[can_summon=true,shrieking=false,waterlogged=false]");
                        if (block.getBlockData().equals(data)) return;

                        // Set the block data
                        block.setBlockData(data);
                        PlayerUtil.reduceHeldItemStack(interactEvent.getPlayer(), 1); // TODO: The item is potentially in the offhand

                        // Display particles and play sound
                        block.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, block.getLocation().clone().add(new Vector(.5, .5, .5)), 20, .3, .3, .3, 0, null);
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_PLACE, SoundCategory.BLOCKS, 1, 1);
                        block.getWorld().playSound(block.getLocation(), Sound.ENTITY_WARDEN_NEARBY_CLOSEST, SoundCategory.BLOCKS, 1, 1);
                    });
                }
            })
            .setCustomModel(2001).setItemCategory(MagickItemCategory.TOOL).addTag("dream_item")
            .build());
     */

    public static final ICustomItem ECHO_TOTEM = register(new CobaltItem.Builder("echo_totem")
            .material(Material.ENCHANTED_BOOK)
            .itemName(HexUtils.colorify("&eEcho Totem"))
            .extraLore(HexUtils.colorify("&o&7Teleports you to your spawnpoint, though it comes at a cost..."))
            .itemActivatorAsync(ItemActivator.PLAYER_RIGHT_CLICK, (item, event, slot) -> {
                PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
                if (!item.compareTo(interactEvent.getPlayer().getInventory().getItemInMainHand())) return;

                Player player = interactEvent.getPlayer();
                Location spawnLocation = player.getBedSpawnLocation();
                player.playEffect(EntityEffect.TELEPORT_ENDER);
                PlayerUtil.reduceHeldItemStack(player, 1);
                CobaltMagick.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> {
                    PlayerUtil.dropPercentageOfInventory(player, .5);
                    player.teleport(Objects.requireNonNullElseGet(spawnLocation, () -> player.getWorld().getSpawnLocation()));
                    player.playEffect(EntityEffect.TOTEM_RESURRECT);
                });
            })
            .modelData(1001).category(MagickItemCategory.TOOL)
            .build());

}
