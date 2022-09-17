package se.fusion1013.plugin.cobaltmagick.world.structures.portal;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractMagickPortal implements IActivatableStorageObject, Cloneable {

    // ----- VARIABLES -----

    private boolean firstActivation = true;
    protected static final String OBJECT_IDENTIFIER = "magick_portal";
    protected boolean isActive = true;

    // Static variables
    private static final Sound TELEPORT_SOUND = Sound.ENTITY_ILLUSIONER_MIRROR_MOVE; // TODO: Replace with custom sound
    private static final float PORTAL_RADIUS = 2;
    private static final ParticleGroup PORTAL_ACTIVATE = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                    .setParticle(Particle.END_ROD)
                    .setCount(10)
                    .setSpeed(.25)
                    .build())
            .build();
    private static final ParticleGroup PORTAL_AMBIENT_PARTICLE = new ParticleGroup.ParticleGroupBuilder()
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.SPELL_WITCH)
                    .setCount(10)
                    .setSpeed(0)
                    .setDensity(15)
                    .setOffset(new Vector(.2, .2, .2))
                    .setRadius(PORTAL_RADIUS-1)
                    .build())
            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder()
                    .setParticle(Particle.DUST_COLOR_TRANSITION)
                    .setExtra(new Particle.DustTransition(Color.PURPLE, Color.MAROON, 1))
                    .setCount(5)
                    .setOffset(new Vector(.1, .1, .1))
                    .setSpeed(.8)
                    .setDensity(20)
                    .setRadius(PORTAL_RADIUS)
                    .build())
            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                    .setParticle(Particle.END_ROD)
                    .setCount(5)
                    .setSpeed(.1)
                    .build())
            .build();

    private UUID uuid;
    protected Location portalLocation;
    protected Location exitLocation;

    // ----- CONSTRUCTORS -----

    public AbstractMagickPortal(Location portalLocation, Location exitLocation) {
        this.portalLocation = portalLocation;
        this.exitLocation = exitLocation;
        this.uuid = UUID.randomUUID();
    }

    public AbstractMagickPortal(Location portalLocation, Location exitLocation, UUID uuid) {
        this.portalLocation = portalLocation;
        this.exitLocation = exitLocation;
        this.uuid = uuid;
    }

    // ----- PORTAL TICK -----

    protected void tickPortal() {

        if (exitLocation == null) return;
        if (!isActive) return;

        // If first activation tick, play appear sound & extra particles
        if (firstActivation) {
            portalLocation.getWorld().playSound(portalLocation, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1, 1);
            PORTAL_ACTIVATE.display(portalLocation);

            firstActivation = false;
        }

        // Display portal particles
        PORTAL_AMBIENT_PARTICLE.display(portalLocation);

        // Teleport player if close to portal
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != portalLocation.getWorld()) continue;
            if (player.getLocation().distanceSquared(portalLocation) > PORTAL_RADIUS*PORTAL_RADIUS) continue;

            // Teleport Player
            Bukkit.getScheduler().runTask(CobaltMagick.getInstance(), () -> player.teleport(exitLocation));

            // Play teleport sound
            player.playSound(portalLocation, TELEPORT_SOUND, 1, 1);
            player.playSound(exitLocation, TELEPORT_SOUND, 1, 1);
        }
    }

    // ----- ACTIVATABLE -----

    @Override
    public void activate(Object... objects) {
        if (!isActive) this.firstActivation = true;
        this.isActive = true;
    }

    @Override
    public void deactivate(Object... objects) {
        this.isActive = false;
        this.firstActivation = true;
    }

    // ----- JSON STORAGE -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("uuid", uuid.toString());
        jo.add("portal_location", JsonUtil.toJson(portalLocation));
        if (exitLocation != null) jo.add("exit_location", JsonUtil.toJson(exitLocation));
        jo.addProperty("is_active", isActive);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        portalLocation = JsonUtil.toLocation(jsonObject.getAsJsonObject("portal_location"));
        exitLocation = JsonUtil.toLocation(jsonObject.getAsJsonObject("exit_location"));
        if (jsonObject.get("is_active") != null) isActive = jsonObject.get("is_active").getAsBoolean();
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public AbstractMagickPortal(AbstractMagickPortal target) {
        this.uuid = target.uuid;
        this.portalLocation = target.portalLocation;
        this.exitLocation = target.exitLocation;
        this.firstActivation = true;
        this.isActive = target.isActive;
    }

    @Override
    public AbstractMagickPortal clone() {
        return null;
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        this.portalLocation = (Location) objects[0];
        this.exitLocation = (Location) objects[1];
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new LocationArgument("portal_location"),
                new LocationArgument("exit_location")
        };
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "portal_location" -> this.portalLocation = (Location) value;
            case "exit_location" -> this.exitLocation = (Location) value;
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("UUID: " + uuid.toString());
        info.add("Portal Location: " + portalLocation.toVector());
        info.add("Exit Location: " + exitLocation.toVector());
        return info;
    }

    @Override
    public Location getLocation() {
        return portalLocation;
    }

    @Override
    public void setLocation(Location location) {
        this.portalLocation = location;
    }

    @Override
    public UUID getUniqueIdentifier() {
        return uuid;
    }

    @Override
    public void setUniqueIdentifier(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getObjectIdentifier() {
        return OBJECT_IDENTIFIER;
    }

    public Location getPortalLocation() {
        return portalLocation;
    }

    public void setPortalLocation(Location portalLocation) {
        this.portalLocation = portalLocation;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
    }
}
