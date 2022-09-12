package se.fusion1013.plugin.cobaltmagick.world.structures.laser;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;
import se.fusion1013.plugin.cobaltcore.world.chunk.IChunkBound;

import java.util.*;

public abstract class AbstractLaser implements IChunkBound<AbstractLaser> {

    // ----- VARIABLES -----

    private final UUID uuid;

    private final Location startLocation;
    private final Map<Location, Boolean> activators = new HashMap<>();

    static Material[] passableBlocks = new Material[] {
            Material.IRON_BARS,
            Material.IRON_TRAPDOOR, Material.OAK_TRAPDOOR, Material.SPRUCE_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR, Material.MANGROVE_TRAPDOOR, Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR,
            Material.IRON_DOOR, Material.OAK_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR, Material.MANGROVE_DOOR, Material.CRIMSON_DOOR, Material.WARPED_DOOR,
            Material.GLASS
    };

    // ----- CONSTRUCTORS -----

    public AbstractLaser(Location startLocation) {
        this.startLocation = startLocation;
        this.uuid = UUID.randomUUID();
    }

    public AbstractLaser(Location startLocation, UUID uuid) {
        this.startLocation = startLocation;
        this.uuid = uuid;
    }

    // ----- METHODS -----

    public void tick() {
        // Set all activators to false
        activators.replaceAll((l, v) -> false);

        // Perform laser step
        Location initialLocation = startLocation.clone();
        performLaserStep(Color.WHITE, initialLocation, new Vector());

        // Set activators
        for (Location location : activators.keySet()) {
            if (activators.get(location)) BlockPlacementManager.addBlock(Material.SEA_LANTERN, location);
            else BlockPlacementManager.addBlock(Material.REDSTONE_LAMP, location);
        }

        activators.entrySet().removeIf(locationBooleanEntry -> !locationBooleanEntry.getValue());
    }

    private void performLaserStep(Color laserColor, Location startLocation, Vector incomingDirection) {

        Material blockType = startLocation.getBlock().getType();
        Vector incomingReverse = incomingDirection.clone().multiply(-1);

        if (blockType == Material.LODESTONE) {
            if (!incomingReverse.equals(new Vector(1, 0, 0))) splitLaser(laserColor, startLocation, new Vector(1, 0, 0));
            if (!incomingReverse.equals(new Vector(-1, 0, 0))) splitLaser(laserColor, startLocation, new Vector(-1, 0, 0));
            if (!incomingReverse.equals(new Vector(0, 0, 1))) splitLaser(laserColor, startLocation, new Vector(0, 0, 1));
            if (!incomingReverse.equals(new Vector(0, 0, -1))) splitLaser(laserColor, startLocation, new Vector(0, 0, -1));

            return;

        } else if (blockType.name().contains("STAINED_GLASS")) {

            // Tint laser the same color as the glass
            laserColor = BlockUtil.getBlockColor(startLocation.getBlock().getType());
            splitLaser(laserColor, startLocation, incomingDirection);

        } else if (BlockUtil.getBlockColor(blockType) == laserColor || !startLocation.getBlock().isSolid()) {

            // Pass through the block if it has the same color, or if the block is not solid
            splitLaser(laserColor, startLocation, incomingDirection);
        } else {
            // Check if the block is in the passable blocks list
            for (Material material : passableBlocks) {
                if (material == blockType) {
                    splitLaser(laserColor, startLocation, incomingDirection);
                    return;
                }
            }
        }

        if (startLocation.getBlock().getType() == Material.REDSTONE_LAMP || startLocation.getBlock().getType() == Material.SEA_LANTERN) {
            activators.put(startLocation, true);
            splitLaser(laserColor, startLocation, incomingDirection);
        }

        if (startLocation.getBlock().getBlockData() instanceof Directional directional) {
            // Redirect the laser unless the direction is back from where the laser came from
            if (!incomingReverse.equals(directional.getFacing().getDirection())) splitLaser(laserColor, startLocation, directional.getFacing().getDirection());
        }
    }

    private ParticleGroup createLaserGroup(Color color) {
        ParticleGroup group = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(
                        new ParticleStyleLine.ParticleStyleLineBuilder()
                                .setParticle(Particle.REDSTONE)
                                .setOffset(new Vector(.1, .1, .1))
                                .setExtra(new Particle.DustOptions(color, 1))
                                .setDensity(1)
                                .build())
                .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder()
                        .setParticle(Particle.REDSTONE)
                        .setExtra(new Particle.DustOptions(color, 2))
                        .setDensity(1)
                        .build())
                .build();
        group.setIntegrity(.1);
        return group;
    }

    private void splitLaser(Color laserColor, Location startLocation, Vector direction) {
        Location nextLocation = findNextBlock(startLocation, direction);
        if (nextLocation == null) return;
        createLaserGroup(laserColor).display(startLocation.clone().add(.5, .5, .5), nextLocation.clone().add(.5, .5, .5));
        if (!isWithinBounds(nextLocation)) return;
        performLaserStep(laserColor, nextLocation, direction);
    }

    private Location findNextBlock(Location initialLocation, Vector direction) {
        Location currentStepLocation = initialLocation.clone().add(direction);
        int step = 0;
        while ((currentStepLocation.getBlock().isReplaceable() && step < 200) && isWithinBounds(currentStepLocation)) {
            currentStepLocation.add(direction);
            step++;
        }

        if (step >= 200) return null;

        return currentStepLocation;
    }

    private boolean isWithinBounds(Location location) {
        if (location.getY() > location.getWorld().getMaxHeight()) return false;
        if (location.getY() < location.getWorld().getMinHeight()) return false;

        return true;
    }

    // ----- GETTERS / SETTERS -----


    @Override
    public AbstractLaser getObject() {
        return this;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public Location getStartLocation() {
        return startLocation;
    }
}
