package se.fusion1013.cobaltmagick.alchemy.cauldron.effect;

import org.bukkit.*;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronManager;
import se.fusion1013.cobaltmagick.alchemy.cauldron.ICauldronInstance;

import java.util.Random;
import java.util.Set;

public class CauldronEffectManager extends Manager<CobaltMagick> {

    private static final Random random = new Random();

    public CauldronEffectManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), this::tickCauldrons, 0, 1);
    }

    @Override
    public void disable() {

    }

    private void tickCauldrons() {
        CauldronManager.getCauldronInstances().forEach(ci -> tickCauldron(ci.getLocation(), ci));
    }

    private static void tickCauldron(Location location, ICauldronInstance cauldronInstance) {
        World world = location.getWorld();

        if (cauldronInstance.isEmpty()) tickEmptyCauldron(world, location, cauldronInstance);
        else tickNonEmptyCauldron(world, location, cauldronInstance);
    }

    private static void tickEmptyCauldron(World world, Location location, ICauldronInstance instance) {

    }

    private static void tickNonEmptyCauldron(World world, Location location, ICauldronInstance instance) {
        instance.tick();
        world.spawnParticle(Particle.EFFECT, location.toCenterLocation(), 4, .2, .2, .2, 0, new Particle.Spell(Color.WHITE, 0));
        CauldronEffectUtil.displayCraftingEffect(location);

        // TODO: Rework this to bind an effect to the vein instead of having to do a lookup?
        if (instance.getVeinDistance("earth") <= 4) CauldronEffectUtil.displayEarthVeinEffect(location);
        if (instance.getVeinDistance("air") <= 4) CauldronEffectUtil.displayAirVeinEffect(location);
        if (instance.getVeinDistance("fire") <= 4) CauldronEffectUtil.displayFireVeinEffect(location);
        if (instance.getVeinDistance("water") <= 4) CauldronEffectUtil.displayWaterVeinEffect(location);
        if (instance.getVeinDistance("aether") <= 4) CauldronEffectUtil.displayAetherVeinEffect(location);

        CauldronEffectUtil.playCraftingSounds(location);

        Set<Vector> vectors = instance.getGlyphVectors();
        if (vectors == null) return;

        for (Vector vector : vectors) {
            Location blockLocation = location.clone().add(vector).toCenterLocation();
            if (random.nextFloat() > 0.8f) world.spawnParticle(Particle.END_ROD, blockLocation, 1, .5, .5, .5, 0);
        }
    }
}
