package se.fusion1013.cobaltmagick.spell;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.projectile.IProjectileTemplate;
import se.fusion1013.cobaltmagick.spell.projectile.ISpellProjectile;
import se.fusion1013.cobaltmagick.spell.projectile.ProjectileTemplate;
import se.fusion1013.cobaltmagick.spell.projectile.SpellProjectile;

import java.util.ArrayList;
import java.util.List;

public class SpellManager extends Manager<CobaltMagick> {

    public static final NamespacedKey SPELL_ID_KEY = new NamespacedKey(CobaltMagick.getInstance(), "spell");
    private static final FileLoadedRegistry<ISpellTemplate> SPELL_TEMPLATES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "spells",
            SpellTemplate::load,
            SpellTemplate::load,
            (p, s) -> {
            }
    );
    private static final FileLoadedRegistry<IProjectileTemplate> PROJECTILE_TEMPLATES = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "projectile_templates",
            ProjectileTemplate::new,
            ProjectileTemplate::new,
            (p, s) -> {
            }
    );

    private static final List<ISpellProjectile> ACTIVE_PROJECTILES = new ArrayList<>();

    public SpellManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        SPELL_TEMPLATES.reload();
        PROJECTILE_TEMPLATES.reload();
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this::tickSpells, 1, 1);
    }

    @Override
    public void disable() {

    }

    public void createSpell(SpellProjectile spell) {
        if (spell == null) return;
        ACTIVE_PROJECTILES.add(spell);
    }

    private void tickSpells() {
        for (int i = ACTIVE_PROJECTILES.size() - 1; i >= 0; i--) {
            ISpellProjectile spellProjectile = ACTIVE_PROJECTILES.get(i);
            if (spellProjectile == null || spellProjectile.isDead()) {
                ACTIVE_PROJECTILES.remove(i);
                continue;
            }
            spellProjectile.tick();
            spellProjectile.display();
        }
    }

    public static ISpellInstance getSpellInstance(String id, String state, int slot) {
        ISpellTemplate spellTemplate = getSpellTemplate(id);
        return new SpellInstance(spellTemplate, state, slot);
    }

    public static ISpellTemplate getSpellTemplate(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer persistent = meta.getPersistentDataContainer();
        if (!persistent.has(SPELL_ID_KEY)) {
            CobaltMagick.getInstance().getLogger().info("Item does not have spell key");
            return null;
        }

        String spellId = persistent.get(SPELL_ID_KEY, PersistentDataType.STRING);
        return getSpellTemplate(spellId);
    }

    public static ISpellTemplate getSpellTemplate(String id) {
        return SPELL_TEMPLATES.get(id);
    }

    public static String[] getSpellNames() {
        return SPELL_TEMPLATES.getNames();
    }

    public static IProjectileTemplate getProjectileTemplate(String key) {
        return PROJECTILE_TEMPLATES.get(key);
    }

    public static String[] getProjectileTemplateNames() {
        return PROJECTILE_TEMPLATES.getNames();
    }

    private static SpellManager INSTANCE;

    public static SpellManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpellManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
