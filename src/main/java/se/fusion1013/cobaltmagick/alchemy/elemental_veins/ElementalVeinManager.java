package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.commands.system.CommandManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.CobaltRegistry;
import se.fusion1013.cobaltCore.manager.registry.RegistryProviderStorage;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ElementalVeinManager extends Manager<CobaltMagick> {

    private static final Random random = new Random();
    private static final CobaltRegistry<IElementalVein> ELEMENTAL_VEINS = new CobaltRegistry<>();

    public static void loadElementalVeins(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "elemental_veins/", new RegistryProviderStorage<>(ELEMENTAL_VEINS), new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return ElementalVeinLoader.loadElementalVein(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return ElementalVeinLoader.loadElementalVein(json);
            }
        }, overwrite);
    }

    public static void reloadElementalVeins() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadElementalVeins(plugin, true);
        }
    }

    public ElementalVeinManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this::displayElementalVeins, 0, 1);

        reloadElementalVeins();
        CommandManager.registerReloadMethod("elemental_veins", ElementalVeinManager::reloadElementalVeins, ELEMENTAL_VEINS::getNames);
    }

    @Override
    public void disable() {

    }

    private void displayElementalVeins() {
        Player player = Bukkit.getPlayer("Fusion1013");
        if (player == null) return;

        World world = player.getWorld();
        for (IElementalVein vein : ELEMENTAL_VEINS.values()) {
            displayElementalVein(player, world, vein);
        }
    }

    private void displayElementalVein(Player player, World world, IElementalVein elementalVein) {
        List<ParametricSpline2D.Point2D> points = elementalVein.getPoints();

        for (ParametricSpline2D.Point2D point : points) {
            if (Math.abs(point.x() - player.getX()) > 250) continue;
            if (Math.abs(point.y() - player.getZ()) > 250) continue;
            if (random.nextFloat() < 0.9) continue;
            Block highestBlock = world.getHighestBlockAt((int) point.x(), (int) point.y());
            world.spawnParticle(
                    elementalVein.getParticle(),
                    new Location(world, point.x(), highestBlock.getY() + 10, point.y()),
                    elementalVein.getParticleCount(),
                    elementalVein.getParticleOffset(),
                    elementalVein.getParticleOffset(),
                    elementalVein.getParticleOffset(),
                    0,
                    null,
                    true
            );
        }
    }

    public static Collection<IElementalVein> getElementalVeins() {
        return ELEMENTAL_VEINS.values();
    }

    public static IElementalVein getElementalVein(String id) {
        return ELEMENTAL_VEINS.get(id);
    }

    private static ElementalVeinManager INSTANCE;

    public static ElementalVeinManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElementalVeinManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }
}
