package se.fusion1013.plugin.cobaltmagick.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MusicBoxEvent extends Event implements Cancellable {

    private final Location location;
    private final String sound;
    private final int id;
    private final Player player;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public MusicBoxEvent(Location location, String sound, int id, Player player) {
        this.location = location;
        this.sound = sound;
        this.id = id;
        this.player = player;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public String getSound() {
        return sound;
    }

    public Player getPlayer() {
        return player;
    }
}
