package se.fusion1013.plugin.cobaltmagick.world.structures.cauldron;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.PlayerUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.util.constants.ItemConstants;
import se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes.Albedo;
import se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes.Citrinitas;
import se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes.Nigredo;
import se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes.Rubedo;

import java.util.UUID;

public class CauldronManager extends Manager implements Listener {

    // ----- VARIABLES -----

    public static final Location CAULDRON_LOCATION = new Location(Bukkit.getWorld("WORLD"), 6139, -11, -3008); // TODO

    // ----- LISTENERS -----

    @EventHandler
    public static void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getLocation().equals(CAULDRON_LOCATION)) return;
        if (event.getClickedBlock().getType() != Material.CAULDRON) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Trigger cauldron stage 1 (Nigredo) // TODO: Check if previous stages have been completed
        if (ItemManager.DEATH_BOUND_AMULET.compareTo(event.getPlayer().getInventory().getItemInMainHand())) executeNigredo(event.getPlayer());

        // Trigger cauldron stage 2 (Albedo)
        if (ItemManager.OUR_MATTER.compareTo(event.getPlayer().getInventory().getItemInMainHand())) executeAlbedo(event.getPlayer());

        // Trigger cauldron stage 3 (Citrinitas)
        if (false) executeCitrinitas(event.getPlayer());

        // Trigger cauldron stage 4 (Rubedo)
        if (false) executeRubedo(event.getPlayer());
    }

    // ----- CAULDRON STAGES -----

    private static void executeNigredo(Player player) {
        // Nigredo Description:
        // Connect beam to amulet owner
        // Lift amulet owner into air above cauldron
        // Damage them until almost death (Should not be able to take other damage during this)
        // Do cool particle stuff around them while this is happening
        // Give some (permanent) buff to this player (??)

        // Get amulet owner & check if they are near the cauldron
        ItemStack amuletItem = player.getInventory().getItemInMainHand();
        String amuletOwnerUUID = amuletItem.getItemMeta().getPersistentDataContainer().get(ItemConstants.DEATH_BOUND_ORB_OWNER, PersistentDataType.STRING);
        if (amuletOwnerUUID == null) return;
        Player amuletOwner = Bukkit.getPlayer(UUID.fromString(amuletOwnerUUID));
        if (amuletOwner == null) return;
        if (amuletOwner.getLocation().distanceSquared(CAULDRON_LOCATION) > 20*20) return; // Check if player is within 20 blocks of cauldron center

        // Remove amulet item from player inventory
        PlayerUtil.reduceHeldItemStack(player, 1);

        // Initiate Nigredo Scene
        Nigredo.start(CAULDRON_LOCATION, amuletOwner);
    }

    private static void executeAlbedo(Player player) {
        PlayerUtil.reduceHeldItemStack(player, 1);

        Player amuletOwner = Bukkit.getPlayer("Fusion1013");
        if (amuletOwner == null) return;

        Albedo.start(CAULDRON_LOCATION, amuletOwner);
    }

    private static void executeCitrinitas(Player player) {
        PlayerUtil.reduceHeldItemStack(player, 1);

        Player amuletOwner = Bukkit.getPlayer("Fusion1013");
        if (amuletOwner == null) return;

        Citrinitas.start(CAULDRON_LOCATION, amuletOwner);
    }

    private static void executeRubedo(Player player) {
        PlayerUtil.reduceHeldItemStack(player, 1);

        Player amuletOwner = Bukkit.getPlayer("Fusion1013");
        if (amuletOwner == null) return;

        Rubedo.start(CAULDRON_LOCATION, amuletOwner);
    }

    // ----- CONSTRUCTORS -----

    public CauldronManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }
}
