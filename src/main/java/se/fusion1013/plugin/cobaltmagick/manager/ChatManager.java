package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskAsynchronously(CobaltMagick.getInstance(), () -> event.setMessage(HexUtils.colorify(event.getMessage())));
    }

    @Override
    public void reload() {
        CobaltMagick.getInstance().getServer().getPluginManager().registerEvents(this, CobaltMagick.getInstance());
    }

    @Override
    public void disable() {

    }
}
