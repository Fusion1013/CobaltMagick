package se.fusion1013.plugin.cobaltmagick.world.structures.cauldron.scenes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.*;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleCircleText;
import se.fusion1013.plugin.cobaltcore.particle.styles.glyph.ParticleStyleGlyph;
import se.fusion1013.plugin.cobaltcore.util.animation.EasingUtil;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.scene.Scene;
import se.fusion1013.plugin.cobaltmagick.scene.SceneEvent;

import java.util.Collection;

public class Albedo {

    // ----- VARIABLES -----

    private static Player amuletOwner;
    private static final Scene ALBEDO_SCENE = new Scene("albedo");
    private static final boolean IS_SCENES_REGISTERED = registerSceneEvents();

    private static final Vector CAULDRON_OFFSET = new Vector(0.5, 3, 0.5);

    // ----- INIT -----

    public static void start(Location location, Player amuletOwner) {
        Albedo.amuletOwner = amuletOwner;
        initParticles();

        reduceTick = 0;
        reduceIntegrityTick = 0;
        itemParticleTick = 0;

        ALBEDO_SCENE.play(location);
    }

    // ----- REGISTER SCENE -----

    private static boolean registerSceneEvents() {
        ALBEDO_SCENE.addEvent(new SceneEvent(0, 0, Albedo::startMusic));
        ALBEDO_SCENE.addEvent(new SceneEvent(0, 0, Albedo::createItem));
        ALBEDO_SCENE.addEvent(new SceneEvent(0, 16000, Albedo::itemPrepareParticles));
        ALBEDO_SCENE.addEvent(new SceneEvent(8000, 74000, Albedo::itemParticles));
        ALBEDO_SCENE.addEvent(new SceneEvent(8800, 74000, Albedo::tickBoids));
        ALBEDO_SCENE.addEvent(new SceneEvent(47000, 57000, Albedo::reduceBoidSize));
        ALBEDO_SCENE.addEvent(new SceneEvent(54000, 74000, Albedo::reduceIntegrity));
        ALBEDO_SCENE.addEvent(new SceneEvent(52000, 74000, Albedo::centerBoids));
        ALBEDO_SCENE.addEvent(new SceneEvent(74000, 74000, Albedo::fillCauldron));
        ALBEDO_SCENE.addEvent(new SceneEvent(62000, 74000, Nigredo::cauldronBubbles));
        ALBEDO_SCENE.addEvent(new SceneEvent(31500, 62000, Albedo::displayGlyphs));
        ALBEDO_SCENE.addEvent(new SceneEvent(31500, 64000, Albedo::displaySquare));
        ALBEDO_SCENE.addEvent(new SceneEvent(74000, 74000, Albedo::finalize));
        return true;
    }

    // ----- SCENE EVENTS -----

    public static void startMusic(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) <= 100*100) p.playSound(location, "cobalt.music.transcendence", 10, 1);
        }
    }

    public static void createItem(Location location) {
        World world = location.getWorld();
        world.spawn(location.clone().add(.5, 2.5, .5), Item.class, item -> {
            item.setGravity(false);
            item.setCanMobPickup(false);
            item.setCanPlayerPickup(false);
            item.setGlowing(true);
            item.setVelocity(new Vector());
            item.setItemStack(ItemManager.OUR_MATTER.getItemStack());
        });

        // LINE_GROUP.display(location.toCenterLocation(), location.clone().add(.5, 3, .5));
    }

    static int itemParticleTick = 0;

    public static void itemPrepareParticles(Location location) {
        double heightOffset = EasingUtil.easeInOutSine(itemParticleTick, 0, 3, 160);
        ITEM_CIRCLE_STYLE.setExtraSetting("radius", (double) EasingUtil.easeInSine(itemParticleTick, 0, 2, 160));
        ITEM_GROUP.display(location.toCenterLocation().add(0, heightOffset, 0));

        if (itemParticleTick >= 160) ITEM_CIRCLE_STYLE.setExtraSetting("radius", (double) EasingUtil.easeInOutSine(itemParticleTick-160, 2, 0, 160));

        itemParticleTick++;
    }

    public static void itemParticles(Location location) {
        location.getWorld().spawnParticle(Particle.END_ROD, location.clone().add(CAULDRON_OFFSET), 1, .01, .01, .01, .02);
    }

    public static void tickBoids(Location location) {
        BOID_GROUP.display(location.clone().add(CAULDRON_OFFSET));
    }

    static int reduceTick = 0;

    public static void reduceBoidSize(Location location) {
        int boidStartSize = 10000;
        int boidSizeDifference = -5000;

        int newBoidSize = (int) EasingUtil.easeInOutSine(reduceTick, boidStartSize, boidSizeDifference, 200);

        BOID_STYLE.setWidth(newBoidSize);
        BOID_STYLE.setDepth(newBoidSize);
        BOID_STYLE.setHeight(newBoidSize);

        reduceTick++;
    }

    static int reduceIntegrityTick = 0;

    public static void reduceIntegrity(Location location) {
        BOID_GROUP.setIntegrity(EasingUtil.easeInOutSine(reduceIntegrityTick, 1, 0f, 400));
        reduceIntegrityTick++;
    }

    public static void centerBoids(Location location) {
        SMALL_BOID_GROUP.display(location.clone().add(CAULDRON_OFFSET));
    }

    public static void displayGlyphs(Location location) {
        ALBEDO_TEXT_GROUP.display(location.clone().add(0, 10, 0));
    }

    public static void displaySquare(Location location) {
        SQUARE_GROUP.display(location.clone().add(0, 30, 0));
    }

    public static void fillCauldron(Location location) {
        World world = location.getWorld();

        // Remove nearby armor stands & items
        Collection<Entity> entities = world.getNearbyEntities(location, 2, 5, 2);
        for (Entity entity : entities) {
            if (entity instanceof Item item) item.remove();
            if (entity instanceof ArmorStand stand) stand.remove();
        }

        world.spawn(location.clone().add(new Vector(.34, -.8, .34)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.WHITE_CARPET);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.34, -.8, .66)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.WHITE_CARPET);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.66, -.8, .34)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.WHITE_CARPET);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
        world.spawn(location.clone().add(new Vector(.66, -.8, .66)), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            ItemStack hat = new ItemStack(Material.WHITE_CARPET);
            armorStand.getEquipment().setItem(EquipmentSlot.HEAD, hat);
        });
    }

    public static void finalize(Location location) {
        // Set new player max health
        AttributeInstance healthInstance = amuletOwner.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthInstance != null) healthInstance.setBaseValue(healthInstance.getBaseValue() + 16);

        // Particle line towards amulet owner
        LINE_GROUP.display(location.toCenterLocation(), amuletOwner.getLocation().clone().add(0, 1, 0));
        LINE_HEART_GROUP.display(location.toCenterLocation(), amuletOwner.getLocation().clone().add(0, 1, 0));

        // Give advancement to all nearby players
        MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);

        if (advancementManager != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                advancementManager.grantAdvancement(p, "progression", "albedo");
            }
        }
    }

    // ----- PARTICLES -----

    private static void initParticles() {
        BOID_STYLE = new ParticleStyleBoids.Builder()
                .setParticle(Particle.END_ROD)
                .setWidth(10000).setHeight(10000).setDepth(10000)
                .setAmount(200)
                .setIgnoreCount(100)
                .setMoveDistance(10)
                .setCohesion(10).setAlignment(50).setSeparation(100)
                .build();
        BOID_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(BOID_STYLE)
                .build();

        ALBEDO_TEXT_STYLE = new ParticleStyleCircleText.Builder()
                .setParticle(Particle.END_ROD)
                .setCompress(.4)
                .setText("albedoalbedoalbedoalbedo")
                .setGlyphStyle((ParticleStyleGlyph) ParticleStyleManager.getDefaultParticleStyle("finnish_glyph"))
                .setAngularVelocity(0, .01, 0)
                .setRadius(40)
                .build();
        ALBEDO_TEXT_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(ALBEDO_TEXT_STYLE)
                .setIntegrity(.2)
                .build();

        SQUARE_STYLE = new ParticleStyleSquare.Builder()
                .setParticle(Particle.END_ROD)
                .setRadius(12.5)
                .setCount(5)
                .setIterations(12)
                .setOffset(new Vector(.55, 1.5, .55))
                .build();
        SQUARE_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(SQUARE_STYLE)
                .setIntegrity(.2)
                .build();

        LINE_STYLE = new ParticleStyleLine.ParticleStyleLineBuilder()
                .setParticle(Particle.END_ROD)
                .setOffset(new Vector(.05, .05, .05))
                .setCount(3)
                .build();
        LINE_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(LINE_STYLE)
                .build();

        ITEM_CIRCLE_STYLE = new ParticleStyleCircle.ParticleStyleCircleBuilder()
                .setParticle(Particle.DUST_COLOR_TRANSITION)
                .setExtra(new Particle.DustTransition(Color.BLACK, Color.GRAY, 2))
                .setIterations(3)
                .setRadius(.1)
                .setAngularVelocity(0, 5, 0)
                .build();
        ITEM_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(ITEM_CIRCLE_STYLE)
                .build();

        SMALL_BOID_STYLE = new ParticleStyleBoids.Builder()
                .setParticle(Particle.END_ROD)
                .setWidth(1000).setHeight(1000).setDepth(1000)
                .setAmount(10)
                .setMoveDistance(9)
                .setCohesion(500).setAlignment(4).setSeparation(100)
                .build();
        SMALL_BOID_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(SMALL_BOID_STYLE)
                .build();

        LINE_HEART_STYLE = new ParticleStyleLine.ParticleStyleLineBuilder()
                .setParticle(Particle.HEART)
                .setOffset(new Vector(.1, .1, .1))
                .setCount(2)
                .build();
        LINE_HEART_GROUP = new ParticleGroup.ParticleGroupBuilder()
                .addStyle(LINE_HEART_STYLE)
                .build();
    }

    private static ParticleStyleBoids BOID_STYLE;
    private static ParticleGroup BOID_GROUP;

    private static ParticleStyleCircleText ALBEDO_TEXT_STYLE;
    private static ParticleGroup ALBEDO_TEXT_GROUP;

    private static ParticleStyle SQUARE_STYLE;
    private static ParticleGroup SQUARE_GROUP;

    private static ParticleStyle LINE_STYLE;
    private static ParticleGroup LINE_GROUP;

    private static ParticleStyle ITEM_CIRCLE_STYLE;
    private static ParticleGroup ITEM_GROUP;

    private static ParticleStyleBoids SMALL_BOID_STYLE;
    private static ParticleGroup SMALL_BOID_GROUP;

    private static ParticleStyle LINE_HEART_STYLE;
    private static ParticleGroup LINE_HEART_GROUP;

}
