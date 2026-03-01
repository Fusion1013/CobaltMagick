package se.fusion1013.cobaltmagick.alchemy.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;

public class AlchemyBlockPropertyLoader {

    public static AlchemyBlockProperties loadComponent(YamlConfiguration yaml) {
        return new AlchemyBlockProperties(yaml);
    }

    public static AlchemyBlockProperties loadComponent(JsonObject json) {
        return null;
    }
}
