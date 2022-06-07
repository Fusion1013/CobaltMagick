package se.fusion1013.plugin.cobaltmagick.world;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.manager.MagickConfigManager;
import se.fusion1013.plugin.cobaltmagick.util.BlockUtil;

import java.util.List;

public class WorldEvents implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();

        // Drops Budded Amethyst if Player is using Silk Touch Netherite Pickaxe to break it
        if (event.getBlock().getType() == Material.BUDDING_AMETHYST && heldItem.containsEnchantment(Enchantment.SILK_TOUCH) && heldItem.getType() == Material.NETHERITE_PICKAXE && (Boolean)ConfigManager.getInstance().getFromConfig(CobaltMagick.getInstance(), "magick.yml", "breakable-amethyst-buds")) {
            Location blockLocation = event.getBlock().getLocation().add(new Vector(.5, .5, .5));
            World world = blockLocation.getWorld();
            if (world != null) {
                Item item = (Item)world.spawnEntity(blockLocation, EntityType.DROPPED_ITEM);
                item.setItemStack(new ItemStack(Material.BUDDING_AMETHYST, 1));
            }
        }
    }

    @EventHandler
    public void onPortalCreation(PortalCreateEvent event){
        if ((boolean) ConfigManager.getInstance().getFromConfig(CobaltMagick.getInstance(), "magick.yml", "unstable-nether-portals") && event.getReason() == PortalCreateEvent.CreateReason.FIRE) unstableNetherPortalEvent(event);
    }

    private void unstableNetherPortalEvent(PortalCreateEvent event){
        List<BlockState> blocks = event.getBlocks();
        event.setCancelled(true);
        Entity entity = event.getEntity();
        if (entity == null) return;

        Vector averagePosition = BlockUtil.getAveragePosition(blocks);

        World world = entity.getWorld();
        Location loc = new Location(world, averagePosition.getX(), averagePosition.getY(), averagePosition.getZ());

        BlockUtil.setBlocks(blocks, Material.AIR);
        world.createExplosion(loc, 4, true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if ((boolean)ConfigManager.getInstance().getFromConfig(CobaltMagick.getInstance(), "magick.yml", "unstable-end-portals")) unstableEndPortalEvent(event);
    }

    private void unstableEndPortalEvent(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (block == null) return;
        if (block.getType() != Material.END_PORTAL_FRAME || player.getInventory().getItemInMainHand().getType() != Material.ENDER_EYE) return;

        world.playSound(block.getLocation(), "cobalt.poof", 1, 1);
        world.spawnParticle(Particle.SMOKE_NORMAL, block.getLocation().add(.5, .5, .5), 10, .2, .2, .2, 0);

        EnderSignal signal = (EnderSignal)world.spawnEntity(block.getLocation().add(new Vector(.5, 0, .5)), EntityType.ENDER_SIGNAL);
        signal.setTargetLocation(block.getLocation().add(new Vector(.5, 2, .5)));
        signal.setDropItem(false);

        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 90, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                world.playSound(block.getLocation(), "cobalt.perk_seal", 1, 1);
                BlockUtil.createExplosion(signal.getLocation(), world, 8, true, true, true);
            }
        }.runTaskLater(CobaltMagick.getInstance(), 90);

        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);

        event.setCancelled(true);
    }
}
