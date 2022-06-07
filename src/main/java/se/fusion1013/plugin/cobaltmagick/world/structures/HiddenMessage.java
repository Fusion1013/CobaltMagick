package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;

public class HiddenMessage {

    // ----- VARIABLES -----

    String text;
    Location location;
    Vector rotationVector;
    World world;

    Particle particle = Particle.END_ROD;

    final double letterGap = 2;

    boolean enabled = true;


    // ----- CONSTRUCTORS -----

    public HiddenMessage(String text, Location location, double rotation, TextEncryption encryption) {
        this.text = text;
        this.location = location;
        this.rotationVector = new Vector(1, 0, 0);
        rotationVector.rotateAroundY(rotation);
        world = location.getWorld();
    }

    // ----- LOGIC -----

    public void tick() {
        if (!enabled) return;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            double offset = (double)i - ((double)text.length() / 2.0);
        }
    }

    // ----- GETTERS / SETTERS -----

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    // ----- ENUM -----

    public enum TextEncryption {
        GALACTIC,
        CUSTOM_GLYPHS
    }

}
