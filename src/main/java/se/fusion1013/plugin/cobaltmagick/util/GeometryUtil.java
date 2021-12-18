package se.fusion1013.plugin.cobaltmagick.util;


import org.bukkit.util.Vector;

public class GeometryUtil {

    public static Vector getPointOnSphere(double radius){
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double dx = radius * Math.sin(phi) * Math.cos(theta);
        double dy = radius * Math.sin(phi) * Math.sin(theta);
        double dz = radius * Math.cos(phi);

        return new Vector(dx, dy, dz);
    }

    public static Vector getPointInSphere(double radius){
        double u = Math.random();
        double v = Math.random();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(Math.random());
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double x = r * sinPhi * cosTheta * radius;
        double y = r * sinPhi * sinTheta * radius;
        double z = r * cosPhi * radius;

        return new Vector(x, y, z);
    }

    public enum Shape{
        SPHERE,
        CUBE
    }
}
