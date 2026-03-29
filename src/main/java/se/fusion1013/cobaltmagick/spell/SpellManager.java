package se.fusion1013.cobaltmagick.spell;

import org.bukkit.NamespacedKey;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;

public class SpellManager extends Manager<CobaltMagick> {

    public static final NamespacedKey SPELL_KEY = new NamespacedKey(CobaltMagick.getInstance(), "spell");
    private static final FileLoadedRegistry<ISpellTemplate> SPELL_TEMPLATES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "spells",
            SpellTemplate::load,
            SpellTemplate::load,
            (p, s) -> {
            }
    );

    public SpellManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        SPELL_TEMPLATES.reload();
    }

    @Override
    public void disable() {

    }

    public static ISpellTemplate getSpellTemplate(String id) {
        return SPELL_TEMPLATES.get(id);
    }

    public static String[] getSpellNames() {
        return SPELL_TEMPLATES.getNames();
    }

    private static SpellManager INSTANCE;

    public static SpellManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpellManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
