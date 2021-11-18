package se.fusion1013.plugin.cobalt.util;

import org.bukkit.Location;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.commands.WarpCommand;

import javax.management.Query;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

public class Warp {

    private int id;
    private String name;
    private UUID owner;
    private Location location;
    private PrivacyLevel privacyLevel;

    public Warp(String name, UUID owner, Location location){
        this.id = hashCode();
        this.name = name;
        this.owner = owner;
        this.location = location;
        this.privacyLevel = PrivacyLevel.PRIVATE;
    }

    public void setPrivacyLevel(String privacyLevel){
        if (privacyLevel.equalsIgnoreCase("private")) this.privacyLevel = PrivacyLevel.PRIVATE;
        else if (privacyLevel.equalsIgnoreCase("public")) this.privacyLevel = PrivacyLevel.PUBLIC;
    }

    /*
    public static List<Warp> getWarps(String name, UUID user, ToIntFunction<WarpCommand.WarpOwnerType> sortingFunction, boolean looseMatch, boolean onlyOwned){
        List<Warp> warpQuery = Cobalt.getInstance().getRDatabase().getWarpByName(name);
    }
     */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public double getShortX(){
        return (double)Math.round(location.getX()*100)/100;
    }
    public double getShortY(){
        return (double)Math.round(location.getY()*100)/100;
    }
    public double getShortZ(){
        return (double)Math.round(location.getZ()*100)/100;
    }

    public enum PrivacyLevel{
        PRIVATE, PUBLIC
    }
}
