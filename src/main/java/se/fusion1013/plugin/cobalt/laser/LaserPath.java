package se.fusion1013.plugin.cobalt.laser;

import org.bukkit.Location;

public class LaserPath {
    private Location startLocation;
    private Location endLocation;
    private LaserColor color;

    public LaserPath(Location startLocation, Location endLocation){
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public void setColor(){

    }

    public void display(){

    }

    public Location getStartLocation(){
        return startLocation;
    }

    public Location getEndLocation(){
        return endLocation;
    }

    public enum LaserColor{
        PURPLE(),
        BLUE();

        LaserColor(){
        }
    }
}
