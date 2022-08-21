package se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes;

import com.sk89q.worldedit.math.MathUtils;
import io.papermc.paper.entity.RelativeTeleportFlag;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleCircle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleFinnishGlyph;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleText;
import se.fusion1013.plugin.cobaltcore.util.VectorUtil;
import se.fusion1013.plugin.cobaltmagick.scene.Scene;
import se.fusion1013.plugin.cobaltmagick.scene.SceneEvent;
import se.fusion1013.plugin.cobaltmagick.util.VectorUtils;

public class Nigredo {

    // ----- VARIABLES -----

    private static Player amuletOwner;
    private static final Scene NIGREDO_SCENE = new Scene("nigredo");
    private static final boolean IS_SCENES_REGISTERED = registerSceneEvents();

    private static final Vector CAULDRON_OFFSET = new Vector(0.5, 3, 0.5);

    // ----- INIT -----

    public static void start(Location location, Player amuletOwner) {
        Nigredo.amuletOwner = amuletOwner;
        initParticles();
        NIGREDO_SCENE.play(location);
    }

    // ----- REGISTER SCENE -----

    private static boolean registerSceneEvents() {
        NIGREDO_SCENE.addEvent(new SceneEvent(0, 0, Nigredo::startMusic));
        NIGREDO_SCENE.addEvent(new SceneEvent(3000, 14000, Nigredo::prepareAmuletOwner));
        NIGREDO_SCENE.addEvent(new SceneEvent(14000, 65000, Nigredo::freezeAmuletOwner));
        NIGREDO_SCENE.addEvent(new SceneEvent(14000, 65000, Nigredo::drainBloodParticle));

        // Health events
        NIGREDO_SCENE.addEvent(new SceneEvent(19000, 19000, (location -> setHealth(location, 18))));
        NIGREDO_SCENE.addEvent(new SceneEvent(24000, 24000, (location -> setHealth(location, 16))));
        NIGREDO_SCENE.addEvent(new SceneEvent(29000, 29000, (location -> setHealth(location, 14))));
        NIGREDO_SCENE.addEvent(new SceneEvent(34000, 34000, (location -> setHealth(location, 12))));
        NIGREDO_SCENE.addEvent(new SceneEvent(39000, 39000, (location -> setHealth(location, 10))));
        NIGREDO_SCENE.addEvent(new SceneEvent(44000, 44000, (location -> setHealth(location, 8))));
        NIGREDO_SCENE.addEvent(new SceneEvent(49000, 49000, (location -> setHealth(location, 6))));
        NIGREDO_SCENE.addEvent(new SceneEvent(54000, 54000, (location -> setHealth(location, 4))));
        NIGREDO_SCENE.addEvent(new SceneEvent(59000, 59000, (location -> setHealth(location, 2))));

        // Particle events
        NIGREDO_SCENE.addEvent(new SceneEvent(17500, 64000, location -> amuletOwnerCircles(location, 2.2)));
        NIGREDO_SCENE.addEvent(new SceneEvent(22000, 60000, Nigredo::nigredoText));
        NIGREDO_SCENE.addEvent(new SceneEvent(30000, 60000, Nigredo::cauldronBubbles));

        NIGREDO_SCENE.addEvent(new SceneEvent(65000, 65000, Nigredo::fillCauldron));
        NIGREDO_SCENE.addEvent(new SceneEvent(65000, 65000, Nigredo::reset));

        return true;
    }

    // ----- SCENE EVENTS -----

    public static void startMusic(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) <= 100*100) p.playSound(location, "cobalt.music.shes_back", 10, 1);
        }
    }

    /**
     * Move the amulet owner to floating position above the cauldron.
     *
     * @param location the cauldron location.
     */
    public static void prepareAmuletOwner(Location location) {
        amuletOwner.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 10, 0, false, false));
        amuletOwner.setGravity(false); // Remove gravity from them

        // Move the amulet mover smoothly towards the top of the cauldron
        Vector newLocation = VectorUtil.lerp(amuletOwner.getLocation().toVector(), location.toVector().clone().add(CAULDRON_OFFSET), 0.03);
        amuletOwner.teleport(amuletOwner.getLocation().clone().set(newLocation.getX(), newLocation.getY(), newLocation.getZ()), PlayerTeleportEvent.TeleportCause.PLUGIN, true, true, RelativeTeleportFlag.YAW, RelativeTeleportFlag.PITCH);
        // Particles between cauldron & amulet owner
        LINE.display(location.clone().add(new Vector(.5, .5, .5)), amuletOwner.getLocation().clone().add(new Vector(0, 1, 0)));
    }

    /**
     * Freezes the amulet owner at the <code>Location</code>.
     *
     * @param location the cauldron location.
     */
    public static void freezeAmuletOwner(Location location) {
        Location realLocation = location.clone().add(CAULDRON_OFFSET);
        amuletOwner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 200, false, false));
        amuletOwner.teleport(amuletOwner.getLocation().clone().set(realLocation.getX(), realLocation.getY(), realLocation.getZ()), PlayerTeleportEvent.TeleportCause.PLUGIN, true, true, RelativeTeleportFlag.YAW, RelativeTeleportFlag.PITCH);
    }

    public static void drainBloodParticle(Location location) {
        BLOOD.display(amuletOwner.getLocation());
    }

    private static double circleTick = 0;

    public static void amuletOwnerCircles(Location location, double radiusTarget) {
        circleTick++;

        Location center = amuletOwner.getLocation().clone().add(0, 1, 0);
        CIRCLE_1.setRadius(lerp(CIRCLE_1.getRadius(), radiusTarget, 0.03));
        CIRCLE_2.setRadius(lerp(CIRCLE_2.getRadius(), radiusTarget/2, 0.01));
        CIRCLE_3.setRadius(lerp(CIRCLE_3.getRadius(), radiusTarget/2, 0.01));

        CIRCLE.setStyleOffset("circle_medium_1", new Vector(0, 2 + (Math.sin((circleTick / 60) * 2 * Math.PI) / 3), 0));
        CIRCLE.setStyleOffset("circle_medium_2", new Vector(0, -2 - (Math.sin((circleTick / 60) * 2 * Math.PI) / 3), 0));

        // Display
        CIRCLE.display(center);
    }

    public static void nigredoText(Location location) {
        NIGREDO_TEXT.display(location.clone().add(new Vector(0, 5, 0)));
        NIGREDO_CIRCLE.display(location.clone().add(new Vector(0, 30, 0)));
    }

    public static void cauldronBubbles(Location location) {
        World world = location.getWorld();
        world.spawnParticle(Particle.SCULK_SOUL, location.clone().add(new Vector(.5, .5, .5)), 3, 0.25, 0.3, 0.25, 0);
    }

    public static void setHealth(Location location, double health) {
        amuletOwner.setHealth(health);
        amuletOwner.playEffect(EntityEffect.HURT);
    }

    public static void fillCauldron(Location location) {
        World world = location.getWorld();
        world.spawn(location.clone().add(new Vector(.35, -1.5, .35)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.BLACK_CONCRETE);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.35, -1.5, .65)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.BLACK_CONCRETE);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.65, -1.5, .35)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.BLACK_CONCRETE);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.65, -1.5, .65)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.BLACK_CONCRETE);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
    }

    public static void reset(Location location) {
        // Reset player gravity
        amuletOwner.setGravity(true);
    }

    // ----- PARTICLES -----

    private static void initParticles() {

        // Player Line
        LINE.addParticleStyle(new ParticleStyleLine.ParticleStyleLineBuilder()
                .setDensity(4)
                .setParticle(Particle.DUST_COLOR_TRANSITION)
                .setExtra(new Particle.DustTransition(Color.AQUA, Color.WHITE, 1))
                .build());

        // Blood
        BLOOD.addParticleStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                .setParticle(Particle.FALLING_LAVA)
                .setCount(5)
                .setOffset(new Vector(.2, .1, .2))
                .build());

        // Circle
        CIRCLE_1 = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.END_ROD)
                .setIterations(8)
                .setCount(1)
                .setSpeed(0)
                .setRadius(0)
                .setAngularVelocity(0, 2, 0)
                .build();
        CIRCLE.addParticleStyle(CIRCLE_1);

        CIRCLE_2 = CIRCLE_1.clone();
        CIRCLE_2.setAngularVelocity(0, -2, 0);
        CIRCLE_2.setName("circle_medium_1");
        CIRCLE_3 = CIRCLE_1.clone();
        CIRCLE_3.setAngularVelocity(0, -2, 0);
        CIRCLE_3.setName("circle_medium_2");
        CIRCLE_1.setOffset(new Vector(0, .01, 0));
        CIRCLE.addParticleStyle(CIRCLE_2);
        CIRCLE.addParticleStyle(CIRCLE_3);
        CIRCLE.setStyleOffset("circle_medium_1", new Vector(0, 2, 0));
        CIRCLE.setStyleOffset("circle_medium_2", new Vector(0, -2, 0));

        // Nigredo Text
        ParticleStyleText nigredoText1 = new ParticleStyleText("nigredo_text_1");
        nigredoText1.setParticle(Particle.END_ROD);
        nigredoText1.setOffset(new Vector(.5, .5, .5));
        nigredoText1.setCount(20);
        nigredoText1.setExtraSetting("text", "nigredo");
        nigredoText1.setExtraSetting("style", "finnish_glyph");
        nigredoText1.setExtraSetting("spacing", 3.4);
        nigredoText1.setExtraSetting("compress", .4);
        ParticleStyleText nigredoText2 = nigredoText1.clone();
        ParticleStyleText nigredoText3 = nigredoText1.clone();
        ParticleStyleText nigredoText4 = nigredoText1.clone();
        nigredoText2.setName("nigredo_text_2");
        nigredoText3.setName("nigredo_text_3");
        nigredoText4.setName("nigredo_text_4");

        nigredoText1.setSkipTicks(2);
        nigredoText2.setSkipTicks(2);
        nigredoText3.setSkipTicks(2);
        nigredoText4.setSkipTicks(2);

        NIGREDO_TEXT.addParticleStyle(nigredoText1);
        NIGREDO_TEXT.addParticleStyle(nigredoText2);
        NIGREDO_TEXT.addParticleStyle(nigredoText3);
        NIGREDO_TEXT.addParticleStyle(nigredoText4);
        NIGREDO_TEXT.setStyleOffset("nigredo_text_1", new Vector(0, 0, 30));
        NIGREDO_TEXT.setStyleOffset("nigredo_text_2", new Vector(0, 0, 30));
        NIGREDO_TEXT.setStyleOffset("nigredo_text_3", new Vector(0, 0, 30));
        NIGREDO_TEXT.setStyleOffset("nigredo_text_4", new Vector(0, 0, 30));
        NIGREDO_TEXT.setStyleRotation("nigredo_text_1", new Vector(0, 0, 0), new Vector(0, 0.0001, 0));
        NIGREDO_TEXT.setStyleRotation("nigredo_text_2", new Vector(0, Math.PI/2, 0), new Vector(0, 0.0001, 0));
        NIGREDO_TEXT.setStyleRotation("nigredo_text_3", new Vector(0, Math.PI, 0), new Vector(0, 0.0001, 0));
        NIGREDO_TEXT.setStyleRotation("nigredo_text_4", new Vector(0, Math.PI * 1.5, 0), new Vector(0, 0.0001, 0));
        NIGREDO_TEXT.setIntegrity(.2);
    }

    private static ParticleGroup LINE = new ParticleGroup();
    private static ParticleGroup BLOOD = new ParticleGroup();

    private static ParticleGroup CIRCLE = new ParticleGroup();
    private static ParticleStyleCircle CIRCLE_1;
    private static ParticleStyleCircle CIRCLE_2;
    private static ParticleStyleCircle CIRCLE_3;

    private static ParticleGroup NIGREDO_TEXT = new ParticleGroup();

    private static ParticleGroup NIGREDO_CIRCLE = new ParticleGroup.ParticleGroupBuilder().addStyle(
            new ParticleStyleCircle.ParticleStyleCircleBuilder()
                    .setParticle(Particle.END_ROD)
                    .setRadius(10)
                    .setCount(8)
                    .setIterations(24)
                    .setOffset(new Vector(.3, 1.5, .3))
                    .setAngularVelocity(0, 2, 0)
                    .build())
            .build();

    // ----- UTIL -----

    private static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

}
