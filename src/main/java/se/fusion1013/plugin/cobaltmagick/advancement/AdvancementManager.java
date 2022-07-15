package se.fusion1013.plugin.cobaltmagick.advancement;

import eu.endercentral.crazy_advancements.JSONMessage;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;

public class AdvancementManager extends Manager implements Listener {

    // ----- CONSTRUCTORS -----

    public AdvancementManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    eu.endercentral.crazy_advancements.manager.AdvancementManager manager;

    @Override
    public void reload() {

        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        ItemStack icon = new ItemStack(Material.NETHER_STAR);
        JSONMessage description = new JSONMessage(new TextComponent("This is a Test Advancement"));
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;
        AdvancementDisplay display = new AdvancementDisplay(icon, "Test", "Test Description", frame, visibility);
        display.setBackgroundTexture("textures/block/nether_bricks.png");

        Advancement rootAdvancement = new Advancement(new NameKey("test_root_advancement", "test_root_advancement_key"), display);
        Advancement advancement = new Advancement(rootAdvancement, new NameKey("test_advancement", "test_advancement_key"), display);

        manager = new eu.endercentral.crazy_advancements.manager.AdvancementManager(new NameKey("advancement_manager", "advancement_manager_key"));
        manager.addAdvancement(rootAdvancement);
        manager.addAdvancement(advancement);

        CobaltMagick.getInstance().getLogger().info("Created advancement");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            Player player = event.getPlayer();
            manager.addPlayer(player);
            for (Advancement advancement : manager.getAdvancements()) {
                manager.grantAdvancement(player, advancement);
                CobaltMagick.getInstance().getLogger().info("Granted player " + player.getName() + " advancement " + advancement.getName());
                manager.saveProgress(player);
            }
        }, 20*5);
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static AdvancementManager INSTANCE = null;
    /**
     * Returns the object representing this <code>AdvancementManager</code>.
     *
     * @return The object of this class
     */
    public static AdvancementManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new AdvancementManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
