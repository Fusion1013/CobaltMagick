package se.fusion1013.plugin.cobaltmagick.world.structures;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootEntry;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.register.*;

import java.util.Random;

public class MagickStructureManager extends Manager implements Listener, CommandExecutor {

    // ----- LOOT TABLES -----

    // -- GENERIC

    public static CustomLootTable GENERIC_DEAD_LOOT = new CustomLootTable(new CustomLootTable.LootTarget[] {CustomLootTable.LootTarget.CHEST, CustomLootTable.LootTarget.BARREL, CustomLootTable.LootTarget.SHULKER_BOX},
            new LootPool(9,
                    new LootEntry(new ItemStack(Material.ROTTEN_FLESH), 4, 19),
                    new LootEntry(new ItemStack(Material.GUNPOWDER), 4, 11),
                    new LootEntry(new ItemStack(Material.SPIDER_EYE), 1, 2),
                    new LootEntry(new ItemStack(Material.BONE), 2, 7)
            )
    );

    // ----- CONSTRUCTORS -----

    public MagickStructureManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("magick_structure", this);
        Bukkit.getPluginManager().registerEvents(this, CobaltMagick.getInstance());

        // Register Structures
        if (ConfigManager.getInstance().getBooleanFromConfig(CobaltMagick.getInstance(), "magick.yml", "disable-natural-structure-generation")) return;

        // generateStructures();
    }

    private void generateStructures() {
        // ----- STRUCTURES: 6xxx -----

        // Meditation Cube
        // NOTE: Insides always spawn at 5000 -30 5000

        // Choose outside location for meditation cube
        Random r = new Random();

        int width = r.nextInt(-1000, 1000);
        Vector pos;

        if (r.nextBoolean()) pos = new Vector(width, 0, 2000);
        else pos = new Vector(2000, 0, width);

        Vector outsidePortalLocation = setFromHeight(pos, World.Environment.NORMAL);

        /*
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                Location location = new Location(world, pos.getX(), pos.getY(), pos.getZ());
                location = location.toHighestLocation();

                outsidePortalLocation = new Vector(location.getX(), location.getY(), location.getZ());
            }
        }
         */

        final Vector medOutGenPos = outsidePortalLocation.clone();

        StructureManager.registerAlwaysGenerate(CreateMeditationCube.createMeditationCubeOutsides(91, new Vector(5022, -20, 5015)), seed -> medOutGenPos);
        StructureManager.registerAlwaysGenerate(CreateMeditationCube.createMeditationCubeInsides(90, medOutGenPos.clone().add(new Vector(0, 10, 0))), new Vector(5000, -30, 5000));

        for (IStructure wandShrineStructure : CreateWandShrines.create(60)) StructureManager.register(wandShrineStructure);
        CreateChestStructures.create();
        CreateMusicBoxStructures.create();

        // High Alchemist Dungeon
        StructureManager.registerAlwaysGenerate(CreateHighAlchemistDungeon.create(), seed -> {
            Random r2 = new Random(seed);

            int xLoc = r2.nextInt(-1000, 1000);
            int zLoc = r2.nextInt(-1000, 1000);
            Vector location = new Vector(xLoc, 10, zLoc);

            highAlchemistDungeonLocation = location;

            return location;
        });

        // Cauldron
        Vector cauldronLocation = new Vector(6032, -52, -3111);
        Vector entryPortalLocation = cauldronLocation.clone().add(new Vector(43, 32, 105));
        StructureManager.registerAlwaysGenerate(CreateCauldron.createCauldron(100, entryPortalLocation), cauldronLocation);

        // Big tree
        Vector treeLocation = setFromHeight(new Vector(370, 0, 760), World.Environment.NORMAL).subtract(new Vector(60, 0, 50));
        StructureManager.registerAlwaysGenerate(CreateTree.createTree(110), treeLocation);
    }

    private static Vector setFromHeight(Vector vector, World.Environment environment) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == environment) {
                Location location = new Location(world, vector.getX(), vector.getY(), vector.getZ());
                location = location.toHighestLocation();

                return new Vector(location.getX(), location.getY(), location.getZ());
            }
        }

        return vector;
    }

    public static Vector highAlchemistDungeonLocation = new Vector();
    public static World highAlchemistWorld = Bukkit.getWorlds().get(0);;

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static MagickStructureManager INSTANCE = null;
    /**
     * Returns the object representing this <code>MagickStructureManager</code>.
     *
     * @return The object of this class
     */
    public static MagickStructureManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new MagickStructureManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
