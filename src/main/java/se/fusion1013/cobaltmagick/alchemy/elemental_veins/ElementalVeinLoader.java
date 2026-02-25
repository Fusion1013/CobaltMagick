package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.util.INameProvider;

public class ElementalVeinLoader {

    public static IElementalVein loadElementalVein(YamlConfiguration yaml) {
        return new ElementalVein(yaml);
    }

    public static INameProvider loadElementalVein(JsonObject json) {
        return new ElementalVein(json);
    }
}
