package se.fusion1013.plugin.cobaltmagick.world.structures.hidden;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.storage.IActivatableStorageObject;
import se.fusion1013.plugin.cobaltcore.util.JsonUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HiddenParticle implements IActivatableStorageObject, Runnable {

    // ----- VARIABLES -----

    private Location location;
    private String particleGroupName = "";
    private UUID uuid;

    private boolean isActive = false;
    private BukkitTask task;

    // ----- CONSTRUCTORS -----

    public HiddenParticle() {}

    public HiddenParticle(Location location, String particleGroupName) {
        this.location = location;
        this.particleGroupName = particleGroupName;
        this.uuid = UUID.randomUUID();
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 0, 1);
    }

    @Override
    public void onUnload() {
        if (this.task != null) this.task.cancel();
    }

    // ----- ACTIVATION -----

    @Override
    public void activate(Object... objects) {
        isActive = true;
    }

    @Override
    public void deactivate(Object... objects) {
        isActive = false;
    }

    @Override
    public void run() {
        if (isActive) {
            ParticleGroup group = ParticleGroupManager.getParticleGroup(particleGroupName);
            if (group == null) return;
            group.display(location);
        }
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = new JsonObject();
        jo.add("location", JsonUtil.toJson(location));
        jo.addProperty("particle_group_name", particleGroupName);
        jo.addProperty("uuid", uuid.toString());
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        this.location = JsonUtil.toLocation(jsonObject.getAsJsonObject("location"));
        this.particleGroupName = jsonObject.get("particle_group_name").getAsString();
        this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
    }

    // ----- COMMAND INTEGRATION METHODS -----

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {
                new LocationArgument("location"),
                new StringArgument("particle_group_name").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleGroupManager.getParticleGroupNames()))
        };
    }

    @Override
    public void fromCommandArguments(Object[] objects) {
        this.location = (Location) objects[0];
        this.particleGroupName = (String) objects[1];
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public HiddenParticle(HiddenParticle target) {
        this.location = target.location;
        this.particleGroupName = target.particleGroupName;
        this.uuid = target.uuid;
        this.isActive = target.isActive;
        this.task = target.task;
    }

    @Override
    public HiddenParticle clone() {
        return new HiddenParticle(this);
    }

    // ----- GETTERS / SETTERS -----

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
        return "hidden_particle";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void setValue(String key, Object value) {
        switch (key) {
            case "location" -> this.location = (Location) value;
            case "particle_group_name" -> this.particleGroupName = (String) value;
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = new ArrayList<>();
        info.add("Location: " + location.toVector());
        info.add("Particle Group: " + particleGroupName);
        return info;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

}
