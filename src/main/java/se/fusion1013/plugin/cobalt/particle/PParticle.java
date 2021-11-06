package se.fusion1013.plugin.cobalt.particle;

import org.bukkit.Location;

public class PParticle {
    private Location location;
    private double speed;
    private double xOff, yOff, zOff;
    private boolean directional;
    private Object overrideData;
    private float size;

    public PParticle(Location location, double xOff, double yOff, double zOff, double speed, boolean directional, Object overrideData){
        this.location = location;
        this.xOff = xOff;
        this.yOff = yOff;
        this.zOff = zOff;
        this.speed = speed;
        this.directional = directional;
        this.overrideData = overrideData;
    }

    public PParticle(Location location, double xOff, double yOff, double zOff, double speed, boolean directional){
        this(location, xOff, yOff, zOff, speed, directional, null);
    }

    public PParticle(Location location, double xOff, double yOff, double zOff, double speed){
        this(location, xOff, yOff, zOff, speed, false, null);
    }

    public PParticle(Location location){
        this(location, 0.0F, 0.0F, 0.0F, 0.0F, false, null);
    }

    public Location getLocation(){
        return this.location;
    }

    public double getSpeed(){
        return this.speed;
    }

    public boolean isDirectional(){
        return this.directional;
    }

    public double getxOff(){
        return this.xOff;
    }

    public double getSize(){
        return this.size;
    }

    public double getyOff(){
        return this.yOff;
    }

    public double getzOff(){
        return this.zOff;
    }

    public Object getOverrideData(){
        return this.overrideData;
    }
}
