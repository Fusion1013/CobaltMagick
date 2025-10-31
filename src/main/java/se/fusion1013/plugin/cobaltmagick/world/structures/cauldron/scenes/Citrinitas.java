package se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes;

import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.RelativeTeleportFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleCircle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleFinnishGlyph;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;
import se.fusion1013.plugin.cobaltmagick.commands.ArmorStandTestCommand;
import se.fusion1013.plugin.cobaltmagick.scene.Scene;
import se.fusion1013.plugin.cobaltmagick.scene.SceneEvent;

import java.util.ArrayList;
import java.util.List;

public class Citrinitas {

    // ----- VARIABLES -----

    private static Player amuletOwner;
    private static final Scene CITRINITAS_SCENE = new Scene("citrinitas");
    private static final boolean IS_SCENES_REGISTERED = registerSceneEvents();

    private static final Vector CAULDRON_OFFSET = new Vector(0.5, 3, 0.5);

    // Values
    private static double CAULDRON_Y_OFFSET = 0;
    private static final double INNER_PILLAR_OFFSET = 7;
    private static final Vector SPHERE_END_OFFSET = new Vector(0, 6, 0);

    // ----- INIT -----

    public static void start(Location location, Player amuletOwner) {
        Citrinitas.amuletOwner = amuletOwner;
        initParticles();

        rotateTick = 0;
        cauldronEntities = null;
        cauldronMoveTick = 0;

        CITRINITAS_SCENE.play(location);

    }

    // ----- REGISTER SCENE -----

    private static boolean registerSceneEvents() {
        CITRINITAS_SCENE.addEvent(new SceneEvent(0, 0, Citrinitas::startMusic));
        CITRINITAS_SCENE.addEvent(new SceneEvent(0, 27000, Citrinitas::middleSphereStart));
        CITRINITAS_SCENE.addEvent(new SceneEvent(1000, 1000, Citrinitas::addVelocity));
        CITRINITAS_SCENE.addEvent(new SceneEvent(27000, 64500, Citrinitas::middleSphereIdle));
        CITRINITAS_SCENE.addEvent(new SceneEvent(27000, 64500, Citrinitas::middleLines));
        CITRINITAS_SCENE.addEvent(new SceneEvent(27000, 27000, Citrinitas::middleParticleExplosion));
        CITRINITAS_SCENE.addEvent(new SceneEvent(39800, 39800, Citrinitas::middleParticleExplosion));
        CITRINITAS_SCENE.addEvent(new SceneEvent(59000, 89000, Citrinitas::middlePointsIdle));
        CITRINITAS_SCENE.addEvent(new SceneEvent(60500, 60500, Citrinitas::rotateMiddlePoints));
        CITRINITAS_SCENE.addEvent(new SceneEvent(64500, 64500, Citrinitas::middleParticleExplosion));
        CITRINITAS_SCENE.addEvent(new SceneEvent(64500, 89000, Citrinitas::middleCylinder));

        CITRINITAS_SCENE.addEvent(new SceneEvent(66000, 88000, Citrinitas::clearRoofSlow));

        CITRINITAS_SCENE.addEvent(new SceneEvent(94000, 94000, Citrinitas::giveBlindness));

        CITRINITAS_SCENE.addEvent(new SceneEvent(95000, 95000, Citrinitas::createCauldronEntities));
        // TODO: Remove cauldron structure

        CITRINITAS_SCENE.addEvent(new SceneEvent(100000, 175000, Citrinitas::rotatePlayers));
        CITRINITAS_SCENE.addEvent(new SceneEvent(100000, 100000, Citrinitas::clearCauldronStructure));

        CITRINITAS_SCENE.addEvent(new SceneEvent(107000, 107000, Citrinitas::clearBlindness));
        CITRINITAS_SCENE.addEvent(new SceneEvent(110000, 158000, Citrinitas::moveCauldronOffset));
        CITRINITAS_SCENE.addEvent(new SceneEvent(110000, 158000, Citrinitas::moveCauldronEntities));

        CITRINITAS_SCENE.addEvent(new SceneEvent(170000, 170000, Citrinitas::removeCauldronEntities));

        return true;
    }

    //region SCENE EVENTS

    //region PHASE 1 (Prepare Roof Clear)

    public static void startMusic(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) <= 100*100) p.playSound(location, "cobalt.music.we_are_gods", 10, 1);
        }
    }

    public static void addVelocity(Location location) {
        Location center = location.toCenterLocation();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Vector dir = new Vector(center.getX() - p.getLocation().getX(), (center.getY()-1) - p.getLocation().getY(), center.getZ() - p.getLocation().getZ());
            Vector norm = dir.normalize();
            p.setVelocity(norm.multiply(-1));
        }
    }

    private static int middleSphereTick = 0;

    public static void middleSphereStart(Location location) {
        double heightOffset = EasingUtil.easeInOutSine(middleSphereTick, 0, SPHERE_END_OFFSET.getY(), 540);
        double radius = EasingUtil.easeInOutSine(middleSphereTick, 0.2, 2, 540);

        MIDDLE_SPHERE_STYLE.setExtraSetting("radius", radius);

        MIDDLE_SPHERE_GROUP.display(location.clone().toCenterLocation().add(new Vector(0, heightOffset, 0)));
        middleSphereTick++;
    }

    public static void middleSphereIdle(Location location) {
        MIDDLE_SPHERE_GROUP.display(location.clone().toCenterLocation().add(SPHERE_END_OFFSET));
    }

    public static void middleLines(Location location) { // TODO: Instead of this, have lines that go out from the center to the walls, breaking invisible 'seals'. Send pulses of particles down along the lines
        Location sphereLocation = location.toCenterLocation().add(SPHERE_END_OFFSET);

        END_ROD_LINE_GROUP.display(sphereLocation.clone().add(new Vector(3, 0, 0)), sphereLocation.clone().add(new Vector(30, 0, 0)));
        END_ROD_LINE_GROUP.display(sphereLocation.clone().add(new Vector(-3, 0, 0)), sphereLocation.clone().add(new Vector(-30, 0, 0)));
        END_ROD_LINE_GROUP.display(sphereLocation.clone().add(new Vector(0, 0, 3)), sphereLocation.clone().add(new Vector(0, 0, 30)));
        END_ROD_LINE_GROUP.display(sphereLocation.clone().add(new Vector(0, 0, -3)), sphereLocation.clone().add(new Vector(0, 0, -30)));

        END_ROD_CIRCLE_STYLE_POSX.setOffset(new Vector(1, 0, 0));
        END_ROD_CIRCLE_GROUP_POSX.display(sphereLocation.clone().add(new Vector(3, 0, 0)));
        END_ROD_CIRCLE_STYLE_POSX.setOffset(new Vector(-1, 0, 0));
        END_ROD_CIRCLE_GROUP_POSX.display(sphereLocation.clone().add(new Vector(-3, 0, 0)));

        END_ROD_CIRCLE_STYLE_POSZ.setOffset(new Vector(0, 0, 1));
        END_ROD_CIRCLE_GROUP_POSZ.display(sphereLocation.clone().add(new Vector(0, 0, 3)));
        END_ROD_CIRCLE_STYLE_POSZ.setOffset(new Vector(0, 0, -1));
        END_ROD_CIRCLE_GROUP_POSZ.display(sphereLocation.clone().add(new Vector(0, 0, -3)));

        // -- GLYPHS
        FINNISH_SINGLE_STYLE.setRotation(new Vector(0, 0, 0));
        FINNISH_SINGLE_STYLE.setLetter('s');
        FINNISH_SINGLE_GROUP.display(sphereLocation.clone().add(new Vector(0, 0, 30)));

        FINNISH_SINGLE_STYLE.setRotation(new Vector(0, 90, 0));
        FINNISH_SINGLE_STYLE.setLetter('e');
        FINNISH_SINGLE_GROUP.display(sphereLocation.clone().add(new Vector(30, 0, 0)));

        FINNISH_SINGLE_STYLE.setRotation(new Vector(0, 180, 0));
        FINNISH_SINGLE_STYLE.setLetter('a');
        FINNISH_SINGLE_GROUP.display(sphereLocation.clone().add(new Vector(0, 0, -30)));

        FINNISH_SINGLE_STYLE.setRotation(new Vector(0, 270, 0));
        FINNISH_SINGLE_STYLE.setLetter('l');
        FINNISH_SINGLE_GROUP.display(sphereLocation.clone().add(new Vector(-30, 0, 0)));
    }

    public static void middlePointsIdle(Location location) {
        MIDDLE_CIRCLE_POINTS_GROUP.display(location.clone().toCenterLocation().add(SPHERE_END_OFFSET));
    }

    public static void rotateMiddlePoints(Location location) {
        MIDDLE_CIRCLE_POINTS.setAngularVelocity(0, 7, 10);
    }

    public static void middleCylinder(Location location) {
        MIDDLE_CYLINDER_GROUP.display(location.clone().toCenterLocation());
    }

    public static void middleParticleExplosion(Location location) {
        Location sphereLocation = location.toCenterLocation().add(SPHERE_END_OFFSET);
        END_ROD_POINT_GROUP.display(sphereLocation);
    }

    public static void giveBlindness(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, true, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 1000000, 0, true, false));
        }
    }

    //endregion

    //region PHASE 2 (Clear Roof, Create Cauldron)

    public static void clearRoof(Location location) {
        for (int x = -43; x <= 43; x++) {
            for (int y = 30; y <= 80; y++) {
                for (int z = -43; z <= 43; z++) {
                    Location blockPos = location.clone().add(new Vector(x, y, z));
                    blockPos.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public static void clearRoofSlow(Location location) {
        int width = 86;
        int depth = 86;

        int height = 110;

        int x = 0, z = 0;
        int dx = 0;
        int dz = -1;
        int t = 86;
        int maxI = t*t;

        for (int i = 0; i < maxI; i++) {
            if ((-width/2 <= x) && (x <= width/2) && (-depth/2 <= z) && (z <= width/2)) {
                for (int y = 0; y < 80; y++) {
                    Location replaceLocation = location.clone().add(x, (height - y), z);
                    if (replaceLocation.getBlock().getType() != Material.AIR && replaceLocation.getBlock().getType() != Material.CAVE_AIR) {
                        List<Location> blocks = BlockUtil.generateSphere(replaceLocation, 6, false);
                        for (Location l : blocks) {
                            l.getBlock().setType(Material.AIR);
                        }
                        return;
                    }
                }
            }
            if ((x == z) || ((x < 0) && (x == -z)) || ((x > 0) && (x == 1-z))) {
                t = dx;
                dx = -dz;
                dz = t;
            }

            x += dx;
            z += dz;
        }
    }

    public static void clearCauldronStructure(Location location) {
        for (int x = -43; x <= 43; x++) {
            for (int y = -20; y <= 24; y++) {
                for (int z = -43; z <= 43; z++) {
                    Location blockPos = location.clone().add(new Vector(x, y, z));
                    blockPos.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private static List<Entity> cauldronEntities;
    private static List<Location> defaultCauldronEntityLocations;

    public static void createCauldronEntities(Location location) {
        cauldronEntities = ArmorStandTestCommand.createCauldron(location.toBlockLocation());
        defaultCauldronEntityLocations = new ArrayList<>();
        for (Entity entity : cauldronEntities) defaultCauldronEntityLocations.add(entity.getLocation());
    }

    //endregion

    //region PHASE 3 (Move Cauldron Up)

    public static void clearBlindness(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.removePotionEffect(PotionEffectType.DARKNESS);
        }
    }

    private static int rotateTick = 0;

    public static void rotatePlayers(Location location) {
        Location rotateCenter = location.toCenterLocation().add(new Vector(0, 6 + CAULDRON_Y_OFFSET, 0));
        double radius = 24;

        for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
            double angle = (((Math.PI * 2) / Bukkit.getOnlinePlayers().size()) * i) + ((Math.PI / 400) * rotateTick);
            double xDelta = Math.cos(angle) * radius;
            double zDelta = Math.sin(angle) * radius;

            Player p = Bukkit.getOnlinePlayers().toArray(new Player[0])[i];

            p.teleport(rotateCenter.clone().add(new Vector(xDelta, 0, zDelta)), PlayerTeleportEvent.TeleportCause.PLUGIN, true, true, RelativeTeleportFlag.YAW, RelativeTeleportFlag.PITCH);
            p.lookAt(location.toCenterLocation().add(new Vector(0, 6 + CAULDRON_Y_OFFSET, 0)), LookAnchor.EYES);
        }

        rotateTick++;
    }

    private static int cauldronMoveTick = 0; // TODO: Should be replace by percentage

    public static void moveCauldronOffset(Location location) {
        CAULDRON_Y_OFFSET = EasingUtil.easeInOutSine(cauldronMoveTick, 0, 150, 960);
        cauldronMoveTick++;
    }

    public static void moveCauldronEntities(Location location) {
        for (int i = 0; i < cauldronEntities.size(); i++) {
            Entity entity = cauldronEntities.get(i);
            Location defaultLocation = defaultCauldronEntityLocations.get(i);
            entity.teleport(defaultLocation.clone().add(0, CAULDRON_Y_OFFSET, 0));
        }
    }

    public static void removeCauldronEntities(Location location) {
        if (cauldronEntities == null) return;

        for (Entity entity : cauldronEntities) entity.remove();
        cauldronEntities.clear();
        cauldronEntities = null;
    }

    //endregion

    //endregion

    // ----- PARTICLES -----

    private static void initParticles() {
        MIDDLE_SPHERE_STYLE = new ParticleStyleSphere.ParticleStyleSphereBuilder()
                .setParticle(Particle.END_ROD)
                .setRadius(.2)
                .setDensity(100)
                .build();

        MIDDLE_SPHERE_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(MIDDLE_SPHERE_STYLE)
                .build();


        MIDDLE_CIRCLE_POINTS = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.END_ROD)
                .setCount(10)
                .setOffset(new Vector(.2, .2, .2))
                .setRadius(4)
                .setIterations(2)
                .build();

        MIDDLE_CIRCLE_POINTS_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(MIDDLE_CIRCLE_POINTS)
                .build();


        MIDDLE_CYLINDER_STYLE = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.END_ROD)
                .setRadius(4)
                .setSpeed(6)
                .setCount(0)
                .setOffset(new Vector(0, 1, 0))
                .setIterations(16)
                .setAngularVelocity(0, 10, 0)
                .build();

        MIDDLE_CYLINDER_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(MIDDLE_CYLINDER_STYLE)
                .build();

        END_ROD_LINE_STYLE = new ParticleStyleLine.ParticleStyleLineBuilder()
                .setParticle(Particle.END_ROD)
                .setCount(2)
                .setDensity(1)
                .setOffset(new Vector(.1, .1, .1))
                .build();

        END_ROD_LINE_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(END_ROD_LINE_STYLE)
                .build();

        // -- CIRCLE
        END_ROD_CIRCLE_STYLE_POSX = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.END_ROD)
                .setSpeed(2.2)
                .setCount(0)
                .setRadius(1.5)
                .setIterations(2)
                .setRotation(new Vector(0, 0, 90))
                .setAngularVelocity(0, 10, 0)
                .setOffset(new Vector(1, 0, 0))
                .build();
        END_ROD_CIRCLE_GROUP_POSX = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(END_ROD_CIRCLE_STYLE_POSX)
                .build();

        END_ROD_CIRCLE_STYLE_POSZ = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.END_ROD)
                .setSpeed(2.2)
                .setCount(0)
                .setRadius(1.5)
                .setIterations(2)
                .setRotation(new Vector(90, 0, 0))
                .setAngularVelocity(0, 0, 10)
                .setOffset(new Vector(0, 0, 1))
                .build();
        END_ROD_CIRCLE_GROUP_POSZ = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(END_ROD_CIRCLE_STYLE_POSZ)
                .build();

        // -- POINT
        END_ROD_POINT_STYLE = new ParticleStylePoint.ParticleStylePointBuilder()
                .setParticle(Particle.END_ROD)
                .setSpeed(1)
                .setCount(100)
                .build();

        END_ROD_POINT_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(END_ROD_POINT_STYLE)
                .build();

        // -- FINNISH GLYPH
        FINNISH_SINGLE_STYLE = new ParticleStyleFinnishGlyph.ParticleStyleFinnishGlyphBuilder("f_glyph")
                .setParticle(Particle.END_ROD)
                .setCompress(1.5)
                .setOffset(new Vector(.2, .2, .2))
                .setCount(10)
                .build();
        FINNISH_SINGLE_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(FINNISH_SINGLE_STYLE)
                .build();
    }

    private static ParticleStyleSphere MIDDLE_SPHERE_STYLE;
    private static ParticleGroup MIDDLE_SPHERE_GROUP;

    private static ParticleStyleCircle MIDDLE_CIRCLE_POINTS;
    private static ParticleGroup MIDDLE_CIRCLE_POINTS_GROUP;

    private static ParticleStyleCircle MIDDLE_CYLINDER_STYLE;
    private static ParticleGroup MIDDLE_CYLINDER_GROUP;

    private static ParticleStyleLine END_ROD_LINE_STYLE;
    private static ParticleGroup END_ROD_LINE_GROUP;

    private static ParticleStyleCircle END_ROD_CIRCLE_STYLE_POSX;
    private static ParticleGroup END_ROD_CIRCLE_GROUP_POSX;

    private static ParticleStyleCircle END_ROD_CIRCLE_STYLE_POSZ;
    private static ParticleGroup END_ROD_CIRCLE_GROUP_POSZ;

    private static ParticleStylePoint END_ROD_POINT_STYLE;
    private static ParticleGroup END_ROD_POINT_GROUP;

    private static ParticleStyleFinnishGlyph FINNISH_SINGLE_STYLE;
    private static ParticleGroup FINNISH_SINGLE_GROUP;
}
