package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.util.HexUtils;

public class ChatManager extends Manager implements Listener { // TODO: Move to CobaltServer Plugin

    private static ChatManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CustomItemManager</code>.
     *
     * @return The object of this class
     */
    public static ChatManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ChatManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    public ChatManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
        INSTANCE = this;
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event){
        event.setCancelled(true);

        Player p = event.getPlayer();

        String message = event.getMessage();
        message = HexUtils.colorify(message);

        // Do other chat stuff


        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            player.sendMessage("<" + p.getName() + "> " + message);
        }
    }

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }
}
