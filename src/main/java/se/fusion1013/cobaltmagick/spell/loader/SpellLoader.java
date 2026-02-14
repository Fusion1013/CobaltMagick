package se.fusion1013.cobaltmagick.spell.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;
import se.fusion1013.cobaltmagick.spell.AbstractSpellTemplate;
import se.fusion1013.cobaltmagick.spell.ISpellTemplate;
import se.fusion1013.cobaltmagick.spell.SpellTemplate;

public class SpellLoader extends AbstractFileLoader<ISpellTemplate, AbstractSpellTemplate> {

    @Override
    public ISpellTemplate load(YamlConfiguration yaml) {
        return loadSpell(yaml);
    }

    public static ISpellTemplate loadSpell(YamlConfiguration yaml) {
        return SpellTemplate.load(yaml);
    }

    @Override
    public ISpellTemplate load(JsonObject json) {
        return loadSpell(json);
    }

    public static ISpellTemplate loadSpell(JsonObject json) {
        return null;
    }

    @Override
    protected IFileLoaderComponent<AbstractSpellTemplate>[] getLoaders() {
        return new IFileLoaderComponent[0];
    }
}
