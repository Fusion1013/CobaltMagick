package se.fusion1013.plugin.cobaltmagick.scene;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.*;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.*;
import se.fusion1013.plugin.cobaltcore.util.Laser;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.royawesome.jlibnoise.MathHelper.clamp;

public class CauldronEntryScene {

    // ----- VARIABLES -----

    public static int affectedPlayerRadius = 50;

    double timer;
    long time;

    SceneEvent[] eventList = new SceneEvent[] {
            CENTER_SPHERE,
            START_MUSIC_EVENT,
            FIRST_BOOF_EVENT,
            SECOND_BOOF_EVENT,
            THIRD_BOOF_EVENT,
            THIRDPOINTTWO_BOOF_EVENT,
            FOURTH_BOOF_EVENT,
            FIFTH_BOOF_EVENT,
            SIXTH_BOOF_EVENT,
            SEVENTH_BOOF_EVENT,
            EIGHTH_BOOF_EVENT,
            NINTH_BOOF_EVENT,
            TENTH_BOOF_EVENT,
            ELEVENTH_BOOF_EVENT,
            TWELVETH_BOOF_EVENT,
            THIRTEENTH_BOOF_EVENT,
            THIRD_BOOF_EVENT,
            TELEPORT_GLYPHS,
            STRUCTURE_BUILDING,
            STRUCTURE_CIRCLES,
            SPINNING_CUBES,
            EXPAND_TELEPORT_GLYPHS,
            ROTATE_TELEPORT_GLYPHS,
            EXPANDING_CIRCLE_1,
            EXPANDING_CIRCLE_2,
            EXPANDING_CIRCLE_3,
            EXPANDING_CIRCLE_4,
            EXPANDING_CIRCLE_5,
            EXPANDING_CIRCLE_6,
            EXPANDING_CIRCLE_7,
            EXPANDING_CIRCLE_8,
            EXPANDING_END_MIST,
            PLAYER_LINES,
            END_BEAMS,
            BLINDNESS,
            TELEPORT,
            REMOVE_STRUCTURE
    };

    // Particles
    public static ParticleStyleSphere centerSphereStyle;
    public static ParticleGroup centerSphere = new ParticleGroup("center_sphere");

    // Teleport Glyphs
    public static ParticleGroup teleportGlyphs = new ParticleGroup("teleport_glyphs");
    public static double teleportGlyphOffset = 4;
    public static double teleportGlyphCompress = .4;
    public static double teleportGlyphRotationY = .001;
    public static double teleportGlyphRotationZ = 0;

    public static ParticleStyleGalactic t1Glyph;
    public static ParticleStyleGalactic e1Glyph;
    public static ParticleStyleGalactic l1Glyph;
    public static ParticleStyleGalactic e2Glyph;
    public static ParticleStyleGalactic p1Glyph;
    public static ParticleStyleGalactic o1Glyph;
    public static ParticleStyleGalactic r1Glyph;
    public static ParticleStyleGalactic t2Glyph;

    // Structure Circles
    public static ParticleGroup structureCircles = new ParticleGroup("structure_circles");

    // Spinning Cubes
    public static ParticleStyleCube spinningCube1;
    public static ParticleGroup cubeSpinGroup = new ParticleGroup("cube_spin_group");

    // Expanding circle
    public static BukkitTask circleTask;

    // Dancing

    // Expanding Mist
    public static double radius = 0;

    // Player Lines
    public static ParticleStyleLine buildLine;
    public static ParticleGroup buildLineGroup = new ParticleGroup("player_line_group");

    // ----- CONSTRUCTORS -----

    public CauldronEntryScene() {
        timer = 0;
        time = System.currentTimeMillis();

        for (SceneEvent event : eventList) event.executed = false;
    }

    // ----- SETUP -----

    private void initParticles() {

        centerSphere = new ParticleGroup("center_sphere");
        teleportGlyphs = new ParticleGroup("teleport_glyphs");
        structureCircles = new ParticleGroup("structure_circles");
        cubeSpinGroup = new ParticleGroup("cube_spin_group");
        buildLineGroup = new ParticleGroup("player_line_group");

        teleportGlyphOffset = 4;
        teleportGlyphCompress = .4;
        teleportGlyphRotationY = .001;
        teleportGlyphRotationZ = 0;

        radius = 0;

        // Center Sphere
        centerSphereStyle = new ParticleStyleSphere.ParticleStyleSphereBuilder("center_sphere")
                .setDensity(10)
                .setRadius(0)
                .build();
        centerSphere.addParticleStyle(centerSphereStyle);

        // Teleport Glyphs
        t1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("t1")
                .setParticle(Particle.END_ROD)
                .setLetter('t')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(t1Glyph);
        e1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("e1")
                .setParticle(Particle.END_ROD)
                .setLetter('e')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(e1Glyph);
        l1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("l1")
                .setParticle(Particle.END_ROD)
                .setLetter('l')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(l1Glyph);
        e2Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("e2")
                .setParticle(Particle.END_ROD)
                .setLetter('e')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(e2Glyph);
        p1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("p1")
                .setParticle(Particle.END_ROD)
                .setLetter('p')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(p1Glyph);
        o1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("o1")
                .setParticle(Particle.END_ROD)
                .setLetter('o')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(o1Glyph);
        r1Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("r1")
                .setParticle(Particle.END_ROD)
                .setLetter('r')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(r1Glyph);
        t2Glyph = new ParticleStyleGalactic.ParticleStyleGalacticBuilder("t2")
                .setParticle(Particle.END_ROD)
                .setLetter('t')
                .setCompress(teleportGlyphCompress)
                .build();
        teleportGlyphs.addParticleStyle(t2Glyph);

        // Set glyph offsets & rotations
        teleportGlyphs.setStyleOffset("t1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("e1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("l1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("e2", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("p1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("o1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("r1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("t2", new Vector(0, 0, teleportGlyphOffset));

        teleportGlyphs.setStyleRotation("t1", new Vector(0, 0, 0), new Vector(0, .001, 0));
        teleportGlyphs.setStyleRotation("e1", new Vector(0, Math.toRadians(45), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("l1", new Vector(0, Math.toRadians(90), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("e2", new Vector(0, Math.toRadians(135), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("p1", new Vector(0, Math.toRadians(180), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("o1", new Vector(0, Math.toRadians(225), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("r1", new Vector(0, Math.toRadians(270), 0), new Vector(0, teleportGlyphRotationY, 0));
        teleportGlyphs.setStyleRotation("t2", new Vector(0, Math.toRadians(315), 0), new Vector(0, teleportGlyphRotationY, 0));

        // Structure Circles
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_1")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(3)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_2")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(3)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_3")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(3)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_4")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(3)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );

        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_5")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(2)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_6")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(2)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_7")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(2)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );
        structureCircles.addParticleStyle(new ParticleStyleCircle.ParticleStyleCircleBuilder("pillar_circle_8")
                .setParticle(Particle.TOTEM)
                .setSpeed(0)
                .setCount(1)
                .setRadius(2)
                .setIterations(2)
                .setAngularVelocity(0, 1.4, 0)
                .build()
        );

        structureCircles.setStyleOffset("pillar_circle_1", new Vector(15, -16.49, 0));
        structureCircles.setStyleOffset("pillar_circle_2", new Vector(-15, -16.49, 0));
        structureCircles.setStyleOffset("pillar_circle_3", new Vector(0, -16.49, 15));
        structureCircles.setStyleOffset("pillar_circle_4", new Vector(0, -16.49, -15));

        structureCircles.setStyleOffset("pillar_circle_5", new Vector(9.5, -16.49, 9.5));
        structureCircles.setStyleOffset("pillar_circle_6", new Vector(9.5, -16.49, -9.5));
        structureCircles.setStyleOffset("pillar_circle_7", new Vector(-9.5, -16.49, 9.5));
        structureCircles.setStyleOffset("pillar_circle_8", new Vector(-9.5, -16.49, -9.5));

        // Spinning Cubes
        spinningCube1 = new ParticleStyleCube("spinning_cube_1");
        spinningCube1.setParticle(Particle.DUST_COLOR_TRANSITION);
        spinningCube1.setData(new Particle.DustTransition(Color.ORANGE, Color.RED, 1));
        spinningCube1.setEdgeLength(0);
        spinningCube1.setParticlesPerEdge(1);
        cubeSpinGroup.addParticleStyle(spinningCube1);
        cubeSpinGroup.setStyleRotationSpeed("spinning_cube_1", new Vector(.04, .04, .04));

        // Player Lines
        buildLine = new ParticleStyleLine.ParticleStyleLineBuilder("player_line")
                .setDensity(4)
                .setParticle(Particle.DUST_COLOR_TRANSITION)
                .setExtra(new Particle.DustTransition(Color.AQUA, Color.WHITE, 1))
                .build();
        buildLineGroup.addParticleStyle(buildLine);

        playersWithLasers = new ArrayList<>();

    }

    // ----- RUN -----

    BukkitTask task;

    public void play(Location location) {
        initParticles();

        task = Bukkit.getScheduler().runTaskTimer(CobaltMagick.getInstance(), () -> {

            // Do stuff
            for (SceneEvent event : eventList) event.attemptRun(location, timer);

            // Increment Timer
            timer += System.currentTimeMillis() - time;
            time = System.currentTimeMillis();

            // CobaltMagick.getInstance().getLogger().info("Current Timer: " + timer);

            if (timer >= 200000) task.cancel();
        }, 0, 1);
    }

    public static SceneEvent CENTER_SPHERE = new SceneEvent(0, 197000, CauldronEntryScene::centerSphere);

    public static SceneEvent START_MUSIC_EVENT = new SceneEvent(0, 0, CauldronEntryScene::startMusicEvent);

    public static SceneEvent FIRST_BOOF_EVENT = new SceneEvent(14963, 14963, CauldronEntryScene::boofEvent);
    public static SceneEvent SECOND_BOOF_EVENT = new SceneEvent(22352, 22352, CauldronEntryScene::boofEvent);
    public static SceneEvent THIRD_BOOF_EVENT = new SceneEvent(29756, 29756, CauldronEntryScene::boofEvent);
    public static SceneEvent THIRDPOINTTWO_BOOF_EVENT = new SceneEvent(37142, 37142, CauldronEntryScene::boofEvent);
    public static SceneEvent FOURTH_BOOF_EVENT = new SceneEvent(44507, 44507, CauldronEntryScene::boofEvent);
    public static SceneEvent FIFTH_BOOF_EVENT = new SceneEvent(51910, 51910, CauldronEntryScene::boofEvent);
    public static SceneEvent SIXTH_BOOF_EVENT = new SceneEvent(61137, 61137, CauldronEntryScene::boofEvent);
    public static SceneEvent SEVENTH_BOOF_EVENT = new SceneEvent(64839, 64839, CauldronEntryScene::boofEvent);
    public static SceneEvent EIGHTH_BOOF_EVENT = new SceneEvent(68519, 68519, CauldronEntryScene::boofEvent);
    public static SceneEvent NINTH_BOOF_EVENT = new SceneEvent(72221, 72221, CauldronEntryScene::boofEvent);
    public static SceneEvent TENTH_BOOF_EVENT = new SceneEvent(75916, 75916, CauldronEntryScene::boofEvent);
    public static SceneEvent ELEVENTH_BOOF_EVENT = new SceneEvent(79611, 79611, CauldronEntryScene::boofEvent);
    public static SceneEvent TWELVETH_BOOF_EVENT = new SceneEvent(83281, 83281, CauldronEntryScene::boofEvent);
    public static SceneEvent THIRTEENTH_BOOF_EVENT = new SceneEvent(86983, 86983, CauldronEntryScene::boofEvent);

    // Teleport Glyphs
    public static SceneEvent TELEPORT_GLYPHS = new SceneEvent(61137, 197000, CauldronEntryScene::teleportGlyphs);
    public static SceneEvent EXPAND_TELEPORT_GLYPHS = new SceneEvent(122062, 142000, CauldronEntryScene::expandTeleportGlyphs);
    public static SceneEvent ROTATE_TELEPORT_GLYPHS = new SceneEvent(151000, 172000, CauldronEntryScene::rotateTeleportGlyphs);

    // Structure Building
    public static SceneEvent STRUCTURE_BUILDING = new SceneEvent(64839, 155000, CauldronEntryScene::buildStructure);

    // Structure Circles
    public static SceneEvent STRUCTURE_CIRCLES = new SceneEvent(68519, 155000, CauldronEntryScene::structureCircles);

    // Spinning Cubes
    public static SceneEvent SPINNING_CUBES = new SceneEvent(92489, 122062, CauldronEntryScene::spinningCubes);

    // Expanding Circles
    public static SceneEvent EXPANDING_CIRCLE_1 = new SceneEvent(122062, 122062, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_2 = new SceneEvent(125749, 125749, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_3 = new SceneEvent(129423, 129423, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_4 = new SceneEvent(133125, 133125, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_5 = new SceneEvent(136841, 136841, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_6 = new SceneEvent(140543, 140543, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_7 = new SceneEvent(144188, 144188, (location -> expandingCircle(location, 14, .3)));
    public static SceneEvent EXPANDING_CIRCLE_8 = new SceneEvent(147932, 147932, (location -> expandingCircle(location, 14, .3)));

    // Expanding End Mist
    public static SceneEvent EXPANDING_END_MIST = new SceneEvent(155000, 200000, CauldronEntryScene::expandingEndMist);

    // Player Lines
    // public static SceneEvent PLAYER_LINES = new SceneEvent(155000, 181136, CauldronEntryScene::playerLines); // TODO: Change end time to when teleport happens
    public static SceneEvent PLAYER_LINES = new SceneEvent(158981, 181136, CauldronEntryScene::playerLines); // TODO: Change end time to when teleport happens

    // End Beams
    public static SceneEvent END_BEAMS = new SceneEvent(155000, 155000, CauldronEntryScene::createEndBeams);

    // Player Blindness
    public static SceneEvent BLINDNESS = new SceneEvent(180175, 184092, CauldronEntryScene::blindness);

    // Teleport
    public static SceneEvent TELEPORT = new SceneEvent(181136, 181136, CauldronEntryScene::teleport);

    // Remove Structure
    public static SceneEvent REMOVE_STRUCTURE = new SceneEvent(190000, 190000, CauldronEntryScene::removeStructure);

    // ----- METHODS -----

    public static void createEndBeams(Location location) {
        // TODO: Fix Beams
        /*
        try {
            new Laser.CrystalLaser(location.clone().add(new Vector(15, -4, 0)), location.clone().add(new Vector(0, -2, 0)), 30, 100).start(CobaltMagick.getInstance());
            new Laser.CrystalLaser(location.clone().add(new Vector(-15, -6, 0)), location.clone().add(new Vector(0, -2, 0)), 30, 100).start(CobaltMagick.getInstance());
            new Laser.CrystalLaser(location.clone().add(new Vector(0, -5, 15)), location.clone().add(new Vector(0, -2, 0)), 30, 100).start(CobaltMagick.getInstance());
            new Laser.CrystalLaser(location.clone().add(new Vector(0, -8, -15)), location.clone().add(new Vector(0, -2, 0)), 30, 100).start(CobaltMagick.getInstance());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
         */
    }

    public static void structureCircles(Location location) {
        structureCircles.display(location);
    }

    public static void teleport(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (location.distanceSquared(player.getLocation()) <= 200*200) {
                player.teleport(new Location(location.getWorld(), 6096.778, -16, -3072.192, -32.6f, -1.9f));
            }
        }
    }

    public static void blindness(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (location.distanceSquared(player.getLocation()) <= 200*200) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false));
            }
        }
    }

    public static void expandingEndMist(Location location) {
        radius = lerp(radius, 16, .002);
        location.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, location, 40, radius, radius, radius, .02, new Particle.DustTransition(Color.AQUA, Color.WHITE, 1), true);
    }

    public static void spinningCubes(Location location) {
        spinningCube1.setEdgeLength(lerp(spinningCube1.getEdgeLength(), 10, .02));
        cubeSpinGroup.display(location);
    }

    public static void expandingCircle(Location location, double targetRadius, double lerp) {
        double radius = 0;
        ParticleStyleCircle circle = new ParticleStyleCircle.ParticleStyleCircleBuilder("expanding_circle")
                .setRadius(radius)
                .setParticle(Particle.FLAME)
                .setIterations(60)
                .build();
        ParticleGroup group = new ParticleGroup("circle_group");
        group.addParticleStyle(circle);
        Random r = new Random();
        circle.setRotation(new Vector(r.nextDouble(0, 360), r.nextDouble(0, 360), r.nextDouble(0, 360)));
        circleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), () -> {
            circle.setRadius(lerp(circle.getRadius(), targetRadius, lerp));
            group.display(location);

            if (circle.getRadius() >= targetRadius-.5) circleTask.cancel();
        }, 0, 1);
    }

    static List<Player> playersWithLasers;

    public static void playerLines(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distanceSquared(location) <= affectedPlayerRadius*affectedPlayerRadius) {
                // playerLineGroup.display(location, player.getLocation());

                buildLineGroup.display(location, player.getLocation());

                // TODO: Fix guardian beams
                /*
                if (!playersWithLasers.contains(player) && (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR)) {
                    try {
                        Laser.GuardianLaser laser = new Laser.GuardianLaser(location, player.getLocation(), 26, 100);
                        laser.attachEndEntity(player);
                        laser.start(CobaltMagick.getInstance());
                        playersWithLasers.add(player);
                    } catch (ReflectiveOperationException ex) {
                        ex.printStackTrace();
                    }
                }
                 */

                // Add levitation
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 0, false, false));
            }
        }
    }

    public static void rotateTeleportGlyphs(Location location) {
        teleportGlyphRotationY = lerp(teleportGlyphRotationY, .05, .05);
        teleportGlyphRotationZ = lerp(teleportGlyphRotationZ, .01, .005);

        teleportGlyphs.setStyleRotationSpeed("t1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("e1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("l1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("e2", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("p1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("o1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("r1", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
        teleportGlyphs.setStyleRotationSpeed("t2", new Vector(0, teleportGlyphRotationY, teleportGlyphRotationZ));
    }

    public static void expandTeleportGlyphs(Location location) {
        teleportGlyphOffset = lerp(teleportGlyphOffset, 32, .2);
        teleportGlyphCompress = lerp(teleportGlyphCompress, .8, .01);

        teleportGlyphs.setStyleOffset("t1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("e1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("l1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("e2", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("p1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("o1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("r1", new Vector(0, 0, teleportGlyphOffset));
        teleportGlyphs.setStyleOffset("t2", new Vector(0, 0, teleportGlyphOffset));

        t1Glyph.setCompress(teleportGlyphCompress);
        e1Glyph.setCompress(teleportGlyphCompress);
        l1Glyph.setCompress(teleportGlyphCompress);
        e2Glyph.setCompress(teleportGlyphCompress);
        p1Glyph.setCompress(teleportGlyphCompress);
        o1Glyph.setCompress(teleportGlyphCompress);
        r1Glyph.setCompress(teleportGlyphCompress);
        t2Glyph.setCompress(teleportGlyphCompress);
    }

    private static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    public static void teleportGlyphs(Location location) {
        teleportGlyphs.display(location);
    }

    public static void centerSphere(Location location) {
        centerSphereStyle.setRadius(lerp(centerSphereStyle.getRadius(), 6, .0003));
        centerSphere.display(location);
    }

    public static void boofEvent(Location location) {
        location.getWorld().spawnParticle(Particle.FLAME, location, 40, .1, .1, .1, .8, null, true);
    }

    public static void startMusicEvent(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) <= affectedPlayerRadius*affectedPlayerRadius) p.playSound(location, "cobalt.only_the_beginning", 10, 1); // TODO: Only play for players within radius
        }
    }

    public static void buildStructure(Location location) {

        Random r = new Random();

        for (int y = 0; y < structure.length; y++) {
            String[] layer = structure[y];

            for (int x = 0; x < layer.length; x++) {
                String strip = layer[x];

                for (int z = 0; z < strip.length(); z++) {
                    if (r.nextDouble() < .9) continue; // Randomly skip blocks

                    char c = strip.charAt(z);
                    int offsetX = layer.length / 2;
                    int offsetZ = strip.length() / 2;

                    Location blockLocation = location.clone().add(new Vector(x-offsetX, y-16, z-offsetZ));

                    if (c == 'x' && blockLocation.getBlock().getType() == Material.AIR) {
                        buildLineGroup.display(location, blockLocation);

                        Material material = Material.AIR;
                        switch (r.nextInt(0, 4)) {
                            case 0 -> material = Material.POLISHED_DEEPSLATE;
                            case 1 -> material = Material.DEEPSLATE_BRICKS;
                            case 2 -> material = Material.COBBLED_DEEPSLATE;
                            case 3 -> material = Material.DEEPSLATE_TILES;
                        }

                        blockLocation.getBlock().setType(material);
                        return;
                    } else if (c == 'y' && blockLocation.getBlock().getType() == Material.AIR) {
                        buildLineGroup.display(location, blockLocation);

                        Material material = Material.AIR;
                        switch (r.nextInt(0, 4)) {
                            case 0 -> material = Material.OXIDIZED_COPPER;
                            case 1 -> material = Material.WEATHERED_COPPER;
                            case 2 -> material = Material.EXPOSED_COPPER;
                            case 3 -> material = Material.COPPER_BLOCK;
                        }

                        blockLocation.getBlock().setType(material);
                        return;
                    }

                }
            }
        }
    }

    public static void removeStructure(Location location) {
        for (int y = 0; y < structure.length; y++) {
            String[] layer = structure[y];

            for (int x = 0; x < layer.length; x++) {
                String strip = layer[x];

                for (int z = 0; z < strip.length(); z++) {
                    char c = strip.charAt(z);
                    int offsetX = layer.length / 2;
                    int offsetZ = strip.length() / 2;

                    Location blockLocation = location.clone().add(new Vector(x-offsetX, y-16, z-offsetZ));

                    if (c != '-') blockLocation.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public static String[][] structure = {
            {
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------xx-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "xxx---------------------------xxx",
                    "xyx---------------------------xyx",
                    "xxx---------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------xx-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------"
            },
            {
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------x-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "xxx---------------------------xxx",
                    "xyx---------------------------xyx",
                    "xxx---------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------xx-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------"
            },
            {
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------x-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "xxx---------------------------xxx",
                    "xyx---------------------------xyx",
                    "xx----------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------xx-----------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------"
            },
            {
                    "---------------xxx---------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------xx------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-xx---------------------------xxx",
                    "xyx---------------------------xyx",
                    "xx----------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------xx-----------------x-------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xx----------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------"
            },
            {
                    "----------------xx---------------",
                    "---------------xyx---------------",
                    "----------------xx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------x-------",
                    "------xx------------------x------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-x----------------------------xx-",
                    "xyx---------------------------xyx",
                    "xx----------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------x-----------------x-------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xx----------------",
                    "---------------xyx---------------",
                    "---------------xxx---------------"
            },
            {
                    "---------------------------------",
                    "---------------xyx---------------",
                    "----------------xx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------x-------",
                    "-------x------------------x------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-x----------------------------xx-",
                    "xyx---------------------------xyx",
                    "------------------------------xxx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------x-----------------x-------",
                    "------xx-----------------xx------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xx----------------",
                    "---------------xyx---------------",
                    "----------------xx---------------"
            },
            {
                    "---------------------------------",
                    "---------------xyx---------------",
                    "----------------xx---------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------x-------",
                    "-------x-------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-x----------------------------xx-",
                    "xyx---------------------------xyx",
                    "------------------------------x--",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------x-----------------x-------",
                    "------x------------------x-------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------x-----------------",
                    "---------------xyx---------------",
                    "----------------x----------------"
            },
            {
                    "---------------------------------",
                    "---------------xy----------------",
                    "----------------x----------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------------x-",
                    "xy----------------------------xyx",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------x-------",
                    "------x--------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------x-----------------",
                    "---------------xyx---------------",
                    "----------------x----------------"
            },
            {
                    "---------------------------------",
                    "----------------y----------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "x-----------------------------xy-",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "------x--------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xy----------------",
                    "----------------x----------------"
            },
            {
                    "---------------------------------",
                    "----------------y----------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "x------------------------------y-",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------xy----------------",
                    "---------------------------------"
            },
            {
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "-------------------------------y-",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "----------------y----------------",
                    "---------------------------------"
            },
            {
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "---------------------------------",
                    "----------------y----------------",
                    "---------------------------------"
            }
    };
}
