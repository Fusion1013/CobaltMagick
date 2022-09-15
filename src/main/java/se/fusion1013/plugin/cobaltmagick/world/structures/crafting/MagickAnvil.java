package se.fusion1013.plugin.cobaltmagick.world.structures.crafting;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;

import java.util.List;
import java.util.UUID;

/**
 * Used by throwing an item on top of the <code>MagickAnvil</code> structure.
 * Supports multi-item inputs.
 */
public class MagickAnvil implements IStorageObject {

    // ----- VARIABLES -----

    private UUID uuid;
    private Location location;

    // ----- JSON INTEGRATION -----

    @Override
    public JsonObject toJson() {
        return null;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {

    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public void fromCommandArguments(Object[] objects) {

    }

    @Override
    public Argument<?>[] getCommandArguments() {
        return new Argument[] {

        };
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void setValue(String s, Object o) {

    }

    @Override
    public List<String> getInfoStrings() {
        return null;
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
        return "magick_anvil";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public MagickAnvil(MagickAnvil target) {
        this.uuid = target.uuid;
        this.location = target.location;
    }

    @Override
    public MagickAnvil clone() {
        return new MagickAnvil(this);
    }
}
