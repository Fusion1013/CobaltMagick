package se.fusion1013.cobaltmagick.spell;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.loader.SpellLoader;

import java.util.HashMap;
import java.util.Map;

public class SpellManager extends Manager<CobaltMagick> {

    public static final NamespacedKey SPELL_KEY = new NamespacedKey(CobaltMagick.getInstance(), "spell");
    private static final Map<String, ISpellTemplate> SPELL_TEMPLATES = new HashMap<>();

    public SpellManager(CobaltMagick plugin) {
        super(plugin);
    }

    public static void loadSpellFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "spells/", new IProviderStorage<INameProvider>() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getSpellTemplate(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getSpellTemplate(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return SpellLoader.loadSpell(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return SpellLoader.loadSpell(json);
            }
        }, overwrite);
    }

    public static void reloadSpells() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadSpellFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        loadSpellFiles(CobaltMagick.getInstance(), false);
    }

    @Override
    public void disable() {

    }

    public static ISpellTemplate register(INameProvider spell) {
        return register((ISpellTemplate) spell);
    }

    public static ISpellTemplate register(ISpellTemplate spell) {
        SPELL_TEMPLATES.put(spell.getInternalName(), spell);
        return spell;
    }

    public static ISpellTemplate getSpellTemplate(String id) {
        return SPELL_TEMPLATES.get(id);
    }

    public static String[] getSpellNames() {
        return SPELL_TEMPLATES.keySet().toArray(new String[0]);
    }

    private static SpellManager INSTANCE;

    public static SpellManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpellManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
