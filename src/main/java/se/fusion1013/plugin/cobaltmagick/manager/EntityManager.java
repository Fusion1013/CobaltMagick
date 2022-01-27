package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.entity.*;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;

public class EntityManager extends Manager implements Runnable {

    private static EntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>EntityManager</code>.
     *
     * @return The object of this class
     */
    public static EntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new EntityManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    private static List<ICustomEntity> inbuiltCustomEntities = new ArrayList<>();

    private List<ICustomEntity> summonedCustomEntities;

    public EntityManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
        INSTANCE = this;
    }

    private static void registerCustomEntities() {
        inbuiltCustomEntities = new ArrayList<>();

        // ----- Wand Entity -----

        Wand wandEntityWand = new Wand(false, 1, 0, 4, 1000, 1000, 9, 0, new ArrayList<>(), 0);
        List<ISpell> wandEntitySpells = new ArrayList<>();
        wandEntitySpells.add(SpellManager.getSpell(10)); // Set default spell for wand entity
        wandEntityWand.setSpells(wandEntitySpells);
        register(new SentientWand(wandEntityWand));

        // ----- HIGH ALCHEMIST ENTITY -----

        ICustomEntity high_alchemist = register(new HighAlchemistEntity.HighAlchemistBuilder()
                .setMaxHealth(500)
                .addBossbar("High Alchemist", 50, BarColor.BLUE, BarStyle.SEGMENTED_10)
                .addAlwaysDropItem(CustomItemManager.CRYSTAL_KEY.getItemStack())
                .addXpDrop(6000, 10)
                .scaleHealth(1.7)
                .build());

    }

    private static ICustomEntity register(ICustomEntity entity) {
        inbuiltCustomEntities.add(entity);
        return entity;
    }

    /**
     * Returns a list of all custom entity names
     *
     * @return
     */
    public List<String> getCustomEntityNames() {
        List<String> names = new ArrayList<>();
        for (ICustomEntity entity : inbuiltCustomEntities) {
            names.add(entity.getInbuiltName());
        }
        return names;
    }

    public void spawnCustomEntity(ICustomEntity entity, Location location) {
        if (entity != null) {
            ICustomEntity summoned = entity.clone();
            summoned.spawn(location);
            summonedCustomEntities.add(summoned);
        }
    }

    public void spawnCustomEntity(String entity, Location location) {
        ICustomEntity customEntity = getCustomEntity(entity);
        if (customEntity != null) {
            ICustomEntity newEntity = customEntity.clone();
            newEntity.spawn(location);
            summonedCustomEntities.add(newEntity);
        }
    }

    private ICustomEntity getCustomEntity(String entityName) {
        for (ICustomEntity customEntity : inbuiltCustomEntities) {
            if (customEntity.getInbuiltName().equalsIgnoreCase(entityName)) return customEntity;
        }
        return null;
    }

    @Override
    public void reload() {
        registerCustomEntities();
        summonedCustomEntities = new ArrayList<>();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltMagick.getInstance(), this, 0, 1);
    }

    @Override
    public void disable() {
        for (ICustomEntity customEntity : summonedCustomEntities) {
            customEntity.kill();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < summonedCustomEntities.size(); i++) {
            ICustomEntity customEntity = summonedCustomEntities.get(i);

            customEntity.tick();
            if (!customEntity.isAlive()) {
                summonedCustomEntities.remove(i);
                i--;
            }
        }
    }
}
