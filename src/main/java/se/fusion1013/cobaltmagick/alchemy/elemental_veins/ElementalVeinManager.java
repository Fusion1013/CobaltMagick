package se.fusion1013.cobaltmagick.alchemy.elemental_veins;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.manager.registry.FileLoadedRegistry;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ElementalVeinManager extends Manager<CobaltMagick> {

    private static final Random random = new Random();
    private static final FileLoadedRegistry<IElementalVein> ELEMENTAL_VEINS = new FileLoadedRegistry<>(
            CobaltMagick.getInstance(),
            "elemental_veins",
            ElementalVein::new,
            ElementalVein::new,
            (p, e) -> {
            }
    );

    public ElementalVeinManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this::displayElementalVeins, 0, 1);
        ELEMENTAL_VEINS.reload();
    }

    @Override
    public void disable() {

    }

    private void displayElementalVeins() {
        Player player = Bukkit.getPlayer("Fusion1013");
        if (player == null) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.STRUCTURE_VOID) return;

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
            player.spawnParticle(
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
