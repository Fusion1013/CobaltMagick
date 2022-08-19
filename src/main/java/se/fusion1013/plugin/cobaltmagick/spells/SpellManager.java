package se.fusion1013.plugin.cobaltmagick.spells;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleCube;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.util.shapegenerator.CubeGenerator;
import se.fusion1013.plugin.cobaltcore.util.shapegenerator.SphereGenerator;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.spells.movementmodifier.HomingMovementModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.AddSpellModuleModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.ValueSpellModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.*;
import se.fusion1013.plugin.cobaltmagick.util.GeometryUtil;
import se.fusion1013.plugin.cobaltmagick.util.RandomCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manages all spells.
 */
public class SpellManager extends Manager {

    // ----- VARIABLES -----

    public static final Map<Integer, Spell> INBUILT_SPELLS = new HashMap<>();
    static NamespacedKey spellKey = new NamespacedKey(CobaltMagick.getInstance(), "spell");

    private static final Map<Integer, Spell> activeSpells = new HashMap<>();
    private static final Map<Integer, BukkitTask> activeSpellTasks = new HashMap<>();

    // ----- PROJECTILE SPELLS ----- ID: 1+XXX

    public static final Spell SPARK_BOLT = register(new ProjectileSpell.ProjectileSpellBuilder(10, "spark_bolt")
            .addManaDrain(5).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setSpread(-1).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(2, true).setCriticalChance(5))
            .addDescription("A weak but enchanting sparkling projectile")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).setDensity(5).build())
                    .build())
            .setCustomModel(100000)
            .setSpellTiers(0,1,2)
            .setSpellTierWeights(2, 1, 0.5)
            .build());

    public static final Spell BUBBLE_SPARK = register(new ProjectileSpell.ProjectileSpellBuilder(11, "bubble_spark")
            .addManaDrain(5).setRadius(.4).setSpread(22.9).setVelocity(5).setLifetime(2).addCastDelay(-0.08)
            .addExecuteOnEntityCollision(new DamageModule(1, true))
            .addDescription("A bouncy, inaccurate spell")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).build()
            ).build())
            .setCustomModel(100001)
            .setSpellTiers(0,1,2,3)
            .setSpellTierWeights(1, 1, 1, 0.5)
            .build());

    public static final Spell BOUNCING_BURST = register(new ProjectileSpell.ProjectileSpellBuilder(12, "bouncing_burst")
            .addManaDrain(5).setRadius(.4).setSpread(0.6).setVelocity(14).setLifetime(15).addCastDelay(-0.03).addGravity(1)
            .addExecuteOnEntityCollision(new DamageModule(3, true))
            .addExecuteOnEntityCollision(new ExplodeModule(true))
            .addDescription("A very bouncy projectile")
            .setIsBouncy(true).setBounceFriction(new Vector(.99, .9, .99))
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.VILLAGER_HAPPY).setCount(4).setOffset(new Vector(.1, .1, .1)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FALLING_SPORE_BLOSSOM).setSpeed(0).setRadius(.3).setDensity(20).animateRadius(0, 10).build())
                    .build())
            .setCustomModel(100002)
            .setSpellTiers(0,1,6)
            .setSpellTierWeights(1, 1, 1)
            .build());

    public static final Spell FIREBOLT = register(new ProjectileSpell.ProjectileSpellBuilder(13, "firebolt")
            .addManaDrain(50).setRadius(.7).setSpread(2.9).setVelocity(5.3).setLifetime(10).addCastDelay(.5).addGravity(2).consumeOnUse(25)
            .addExecuteOnEntityCollision(new ExplodeModule(true).setsFire().destroysBlocks().overrideRadius(1.3))
            .addExecuteOnDeath(new ExplodeModule(true).setsFire().destroysBlocks().overrideRadius(1.3))
            .addExecuteOnBlockCollision(new ExplodeModule(true).setsFire().destroysBlocks().onlyIfVelocityExceeds(.75).overrideRadius(1.3))
            .addDescription("A bouncy, explosive bolt")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.FLAME).setCount(3).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(100003)
            .setSpellTiers(0,1,2,3,4)
            .setSpellTierWeights(1, 1, 0.5, 0.25, 0.25)
            .build());

    public static final Spell BURST_OF_AIR = register(new ProjectileSpell.ProjectileSpellBuilder(14, "burst_of_air")
            .addManaDrain(5).setRadius(.4).setVelocity(8).setLifetime(0.8).addCastDelay(.05).setSpread(-2)
            .addExecuteOnEntityCollision(new DamageModule(6, true).setKnockback(3))
            .addDescription("A brittle burst of air capable of greatly pushing objects")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).setCount(2).build()
            ).build())
            .setCustomModel(100004)
            .setSpellTiers(1,2)
            .setSpellTierWeights(1, 1)
            .build());

    public static final Spell SPARK_BOLT_WITH_TRIGGER = register(new ProjectileSpell.ProjectileSpellBuilder(15, "spark_bolt_with_trigger")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.COLLISION)
            .setCustomModel(100005)
            .setSpellTiers(0,1,2,3)
            .setSpellTierWeights(1, 0.5, 0.5, 0.5)
            .build());

    public static final Spell SPARK_BOLT_WITH_DOUBLE_TRIGGER = register(new ProjectileSpell.ProjectileSpellBuilder(16, "spark_bolt_with_double_trigger")
            .addManaDrain(15).setRadius(.2).setVelocity(14).setLifetime(1.6).addCastDelay(0.07).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(4, true).setCriticalChance(5))
            .addExecuteOnBlockCollision(new ExplodeModule(true))
            .addExecuteOnEntityCollision(new ExplodeModule(true))
            .addDescription("A spark bolt that casts two new spells upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.COLLISION)
            .addTrigger(Spell.TriggerType.COLLISION)
            .setCustomModel(100006)
            .setSpellTiers(2,3,5,6,10)
            .setSpellTierWeights(1, 0.5, 1, 1, 0.2)
            .build());

    public static final Spell SPARK_BOLT_WITH_TIMER = register(new ProjectileSpell.ProjectileSpellBuilder(17, "spark_bolt_with_timer")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.TIMER)
            .setCustomModel(100007)
            .setSpellTiers(1,2,3)
            .setSpellTierWeights(0.5, 0.5, 0.5)
            .build());

    public static final Spell BLACK_HOLE = register(new ProjectileSpell.ProjectileSpellBuilder(18, "black_hole")
            .addManaDrain(180).setRadius(4).setVelocity(4).setLifetime(2.4).addCastDelay(1.33).consumeOnUse(3)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 4, false).setDropItems().setSlowReplace().setReplaceNonAir())
            .addDescription("A slow orb of void that eats through all obstacles")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(4).setDensity(75).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(4).setDensity(75).build())
                    .build())
            .setCustomModel(100008)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .setSpellTiers(0,2,4,5)
            .setSpellTierWeights(0.8, 0.8, 0.8, 0.8)
            .build());

    public static final Spell DIGGING_BLAST = register(new ProjectileSpell.ProjectileSpellBuilder(19, "digging_blast")
            .addManaDrain(0).setRadius(1).setVelocity(.01).setLifetime(.01).addCastDelay(0.02).addRechargeTime(-0.17)
            .addExecuteOnEntityCollision(new DamageModule(3, true))
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 1, false).setReplaceNonAir().setDropItems())
            .addDescription("More powerful digging")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(10).setOffset(new Vector(.1, .1, .1)).build())
                    .build())
            .setCustomModel(100009)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .setSpellTiers(2,3,4)
            .setSpellTierWeights(0.5, 1, 1)
            .build());

    public static final Spell NUKE = register(new ProjectileSpell.ProjectileSpellBuilder(110, "nuke")
            .addManaDrain(200).setRadius(1).setVelocity(20).setLifetime(4).setSpread(.6).addCastDelay(.33).addRechargeTime(10).addGravity(.9).consumeOnUse(1)
            .addExecuteOnEntityCollision(new DamageModule(75, true))
            .addExecuteOnEntityCollision(new ExplodeModule(true).overrideRadius(10).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(true).overrideRadius(10).setsFire().destroysBlocks())
            .addDescription("Take cover!")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(10).setOffset(new Vector(.5, .5, .5)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(.7).setDensity(20).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(.5).setDensity(10).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(2).setOffset(new Vector(.1, .1, .1)).build())
                    .build())
            .setCustomModel(100010)
            .setSpellTiers(1,5,6,10)
            .setSpellTierWeights(0.3, 1, 1, 0.2)
            .build());

    public static final Spell GIGA_NUKE = register(new ProjectileSpell.ProjectileSpellBuilder(111, "giga_nuke")
            .addManaDrain(500).setRadius(1).setVelocity(20).setLifetime(4).setSpread(.6).addCastDelay(.83).addRechargeTime(13.33).addGravity(.9).consumeOnUse(1)
            .addExecuteOnEntityCollision(new DamageModule(250, true))
            .addExecuteOnEntityCollision(new ExplodeModule(true).overrideRadius(25).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(true).overrideRadius(25).setsFire().destroysBlocks())
            .addDescription("What do you expect?")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(20).setOffset(new Vector(.5, .5, .5)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(.9).setDensity(40).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(.7).setDensity(20).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(4).setOffset(new Vector(.2, .2, .2)).build())
                    .build())
            .setCustomModel(100011)
            .setSpellTiers(10)
            .setSpellTierWeights(1)
            .build());

    public static final Spell ENERGY_ORB = register(new ProjectileSpell.ProjectileSpellBuilder(112, "energy_orb")
            .addManaDrain(30).setRadius(.2).setVelocity(5).setLifetime(1).addCastDelay(0.1).setSpread(1.7) // TODO: Spread modifier that modifies the spread of the entire cast
            .addExecuteOnEntityCollision(new DamageModule(11, true))
            .addExecuteOnCollision(new ExplodeModule(false).destroysBlocks())
            .addExecuteOnCollision(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CRIT).setCount(50).setSpeed(.5).build())
                    .build(), false))
            .addDescription("A slow but powerful orb of energy")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.BLUE, Color.SILVER, 1)).setDensity(60).setRadius(.2).animateRadius(0, 5).build()
            ).build())
            .setCustomModel(100012)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    public static final Spell TELEPORT_BOLT = register(new ProjectileSpell.ProjectileSpellBuilder(113, "teleport_bolt")
            .addManaDrain(40).setRadius(.2).setVelocity(25).setLifetime(1.6).addCastDelay(0.05).setSpread(-2)
            .setCollidesWithEntities(false)
            .addExecuteOnCollision(new TeleportSpellModule(true).teleportPlayer())
            .addExecuteOnDeath(new TeleportSpellModule(true).teleportPlayer())
            .addExecuteOnCollision(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, true))
            .addExecuteOnDeath(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, true))
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.PLAYERS, false).setPitch(2))
            .addDescription("A magical bolt that moves you wherever it ends up flying")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.END_ROD).setCount(5).setOffset(new Vector(.1, .1, .1)).setDensity(5).build())
                    .build())
            .setCustomModel(100013)
            .setSpellTiers(3,4,5,6,7,8)
            .setSpellTierWeights(0.6, 0.6, 0.6, 0.4, 0.4, 0.4)
            .build());

    public static final Spell RETURN = register(new ProjectileSpell.ProjectileSpellBuilder(114, "return")
            .addManaDrain(40).setRadius(.2).setVelocity(0).setLifetime(4).addCastDelay(0.05).setSpread(-2).setMoves(false)
            .addExecuteOnDeath(new TeleportSpellModule(false).teleportPlayer()).setCollidesWithEntities(false)
            .addExecuteOnCollision(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, false))
            .addExecuteOnDeath(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, false))
            .addExecuteOnCast(new SoundSpellModule("cobalt.perk_seal", SoundCategory.PLAYERS, false).setPitch(2))
            .addDescription("After a period of time, you'll be returned to where you cast this spell")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(1).build()
            ).build())
            .setCustomModel(100014)
            .setSpellTiers(0,1,2,4,5,6)
            .setSpellTierWeights(0.6, 0.6, 0.6, 0.4, 0.4, 0.4)
            .build());

    public static final Spell SWAPPER = register(new ProjectileSpell.ProjectileSpellBuilder(115, "swapper")
            .addManaDrain(5).setRadius(.2).setVelocity(25).setLifetime(1.6).addCastDelay(0.05).setSpread(-2)
            .addExecuteOnEntityCollision(new TeleportSpellModule(false).swapWithHit())
            .addExecuteOnEntityCollision(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, false))
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.PLAYERS, false).setPitch(2))
            .addDescription("It was theorized that the source of qualia would be transferred ...But it turns out it was the whole body all along")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(100015)
            .setSpellTiers(0,1,2,4,5,6)
            .setSpellTierWeights(0.05, 0.05, 0.05, 0.05, 0.05, 0.05)
            .build());

    public static final Spell FIREBALL = register(new ProjectileSpell.ProjectileSpellBuilder(116, "fireball")
            .addManaDrain(70).setRadius(1.5).setVelocity(25).setLifetime(10).addCastDelay(0.83).setSpread(4).addGravity(1)
            .addExecuteOnCollision(new ExplodeModule(true).setsFire().destroysBlocks())
            .addExecuteOnEntityCollision(new DamageModule(20, true))
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_BLAZE_SHOOT, SoundCategory.AMBIENT, false).setVolume(8))
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_ENDER_DRAGON_HURT, SoundCategory.AMBIENT, false).setVolume(2))
            .addDescription("A powerful exploding spell")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(7).setOffset(new Vector(.3, .3, .3)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(.6).setDensity(18).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(.4).setDensity(8).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(1).setOffset(new Vector(.1, .1, .1)).build())
                    .build())
            .setCustomModel(100021)
            .setSpellTiers(0,3,4,6)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    // TODO: Make sure modifiers work on summoned entities
    public static final Spell ARROW = register(new ProjectileSpell.ProjectileSpellBuilder(117, "arrow")
            .addManaDrain(15).setRadius(.2).setVelocity(16).setLifetime(1).addCastDelay(.17).setSpread(.6)
            .addExecuteOnCast(new SoundSpellModule(Sound.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, false))
            .addExecuteOnCast(new EntitySpellModule<Arrow>(EntityType.ARROW, false).setKeepSpellVelocity())
            .addDescription("Summons an arrow")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).setCount(1).build())
                    .build())
            .setCustomModel(100016)
            .setSpellTiers(1,2,4,5)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    // TODO: Make sure modifiers work on summoned entities
    public static final Spell BOMB = register(new ProjectileSpell.ProjectileSpellBuilder(118, "bomb")
            .addManaDrain(25).setRadius(.2).setVelocity(19).setLifetime(.5).addCastDelay(1.67).consumeOnUse(3)
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, false))
            .addExecuteOnCast(new EntitySpellModule<TNTPrimed>(EntityType.PRIMED_TNT, false).setKeepSpellVelocity())
            .addDescription("Summons a bomb that destroys ground very efficiently")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_NORMAL).setCount(10).setOffset(new Vector(.3, .3, .3)).build())
                    .build())
            .setCustomModel(100017)
            .setSpellTiers(0,1,2,3,4,5,6)
            .setSpellTierWeights(1, 1, 1, 1, 1, 1, 1)
            .build());

    // TODO: Make sure modifiers work on summoned entities
    public static final Spell BOMB_CART = register(new ProjectileSpell.ProjectileSpellBuilder(119, "bomb_cart")
            .addManaDrain(75).setRadius(.2).setVelocity(13).setLifetime(.5).addCastDelay(1).consumeOnUse(6)
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, false))
            .addExecuteOnCast(new EntitySpellModule<ExplosiveMinecart>(EntityType.MINECART_TNT, false).setKeepSpellVelocity())
            .addDescription("Summons a self-propeled mine cart loaded with explosives")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(4).setOffset(new Vector(.3, .3, .3)).build())
                    .build())
            .setCustomModel(100018)
            .setSpellTiers(0,1,2,3,4,5,6)
            .setSpellTierWeights(0, 0, 0.6, 0.6, 0.6, 0.6, 0.6)
            .build());

    public static final Spell CURSED_SPHERE = register(new ProjectileSpell.ProjectileSpellBuilder(120, "cursed_sphere")
            .addManaDrain(40).setRadius(.2).setSpread(8.6).setVelocity(1).setLifetime(2).addCastDelay(.33).setIsBouncy(true)
            .addDescription("A projectile that brings bad luck to anyone it hits")
            .addExecuteOnTick(new ValueModule(false).setAcceleration(1.2))
            .addExecuteOnEntityCollision(new DamageModule(15, true))
            .addExecuteOnEntityCollision(new EffectModule(false).setPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 9600, 1, false, true)))
            .addExecuteOnEntityCollision(new MethodSpellModule(true).setRunForTicks(220)
                    .addOnEntityHit(((wand, caster, spell, entityHit) -> {
                        // Spawn an angry cloud when the spell hits an entity
                        if (entityHit.isValid()) {
                            Random r = new Random();
                            entityHit.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, entityHit.getLocation().clone().add(new Vector(0, 1+entityHit.getHeight(), 0)), 20, .6, .3, .6, 0, new Particle.DustTransition(Color.RED, Color.ORANGE, 1));
                            entityHit.getWorld().spawnParticle(Particle.FALLING_DRIPSTONE_LAVA, entityHit.getLocation().clone().add(new Vector(0, 1+entityHit.getHeight(), 0)), 10, .4, .1, .4, 0);
                            if (r.nextDouble() > .99) {
                                Vector point = GeometryUtil.getPointInSphere(2);
                                entityHit.getWorld().spawnEntity(entityHit.getLocation().clone().add(point), EntityType.LIGHTNING);
                            }
                        }
                    })))
            // Particles
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setCount(1).setRadius(.4).setDensity(100).setSpeed(0).setExtra(new Particle.DustTransition(Color.RED, Color.ORANGE, 1)).build())
                    .build(), false))
            .addExecuteOnEntityCollision(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setCount(1).setRadius(.4).setDensity(100).setSpeed(0).setExtra(new Particle.DustTransition(Color.RED, Color.ORANGE, 1)).build())
                    .build(), false))
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setCount(10).setOffset(new Vector(.12, .12, .12)).setExtra(new Particle.DustTransition(Color.RED, Color.ORANGE, 2)).build())
                    .build())
            // Sounds
            .addExecuteOnCast(new SoundSpellModule(Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, false))
            .addExecuteOnEntityCollision(new SoundSpellModule(Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, false))
            .setCustomModel(100020)
            .setSpellTiers(1,2,3)
            .setSpellTierWeights(0.3, 0.2, 0.1)
            .build());

    public static final Spell FIREWORKS = register(new ProjectileSpell.ProjectileSpellBuilder(121, "fireworks")
            .addManaDrain(70).setRadius(.2).setVelocity(18).addCastDelay(1).setLifetime(.15).consumeOnUse(25)
            .addDescription("A fiery, explosive projectile")
            // Spawn firework projectile
            .addExecuteOnCast(new EntitySpellModule<>(Firework.class, SpellManager::editFirework, false).setKeepSpellVelocity().setRandomVelocitySpread(5, 5))
            .addExecuteOnCast(new EntitySpellModule<>(Firework.class, SpellManager::editFirework, false).setKeepSpellVelocity().setRandomVelocitySpread(5, 5))
            .addExecuteOnCast(new EntitySpellModule<>(Firework.class, SpellManager::editFirework, false).setKeepSpellVelocity().setRandomVelocitySpread(5, 5))
            // Sounds
            .addExecuteOnCast(new SoundSpellModule(Sound.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS, false))
            .setCustomModel(100019)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(1, 1, 1, 1, 1, 1)
            .build());

    private static void editFirework(Firework firework) {
        Random r = new Random();
        firework.setTicksToDetonate(r.nextInt(20, 40)); // Detonates after 1-2 seconds
        // Create firework effect
        FireworkEffect.Builder effect = FireworkEffect.builder();
        double type = r.nextDouble();
        if (type < .33) effect.with(FireworkEffect.Type.BALL_LARGE);
        else if (type < .66) effect.with(FireworkEffect.Type.STAR);
        else effect.with(FireworkEffect.Type.BURST);
        // Color
        int colorIterations = r.nextInt(1, 3);
        for (int i = 0; i < colorIterations; i++) {
            float hue = r.nextFloat();
            java.awt.Color color = java.awt.Color.getHSBColor(hue, 1, 1);
            effect.withColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        }
        // Extra
        if (r.nextDouble() > .5) effect.trail(true);
        if (r.nextDouble() > .5) effect.flicker(true);
        if (r.nextDouble() > .5) effect.withFade(Color.YELLOW);
        if (r.nextDouble() > .5) effect.withFade(Color.SILVER);
        if (r.nextDouble() > .5) effect.withFade(Color.ORANGE);
        // Add firework effect
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect.build());
        firework.setFireworkMeta(meta);
        firework.setShotAtAngle(true);
        // TODO: Set random spread for each firework
    }

    // ----- STATIC PROJECTILE SPELLS ----- ID: 2+XXX

    public static final Spell SPHERE_OF_BUOYANCY = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(20, "sphere_of_buoyancy")
            .addManaDrain(10).addCastDelay(.25).setRadius(5).setLifetime(120).consumeOnUse(15)
            .addDescription("A field of levitative magic")
            .addExecuteOnTick(new EffectModule(5, false).setPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2, 0, false, false)).animateRadius(0, 10, 5))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .build())
            .setCustomModel(200000)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.3, 0.6, 0.6, 0.3)
            .build());

    public static final Spell GIGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(21, "giga_black_hole")
            .addManaDrain(240).setRadius(8).setLifetime(10).addCastDelay(1.33).consumeOnUse(6)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 8, false).setDropItems().setReplaceNonAir().setSlowReplace().animateRadius(0, 40, 8))
            .addDescription("A growing orb of negative energy that destroys everything in its reach")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(8).setDensity(150).animateRadius(0, 40).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(8).setDensity(150).animateRadius(0, 40).build())
                    .build())
            .setCustomModel(200001)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.8, 0.8, 0.5)
            .build());

    public static final Spell OMEGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(22, "omega_black_hole")
            .addManaDrain(500).setRadius(20).setLifetime(20).addCastDelay(1).addRechargeTime(1.67).consumeOnUse(6)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 20, false).setReplaceNonAir().setSlowReplace().animateRadius(0, 80, 20))
            .addDescription("Even light dies eventually...")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(20).setDensity(300).animateRadius(0, 80).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(20).setDensity(300).animateRadius(0, 80).build())
                    .build())
            .setCustomModel(200002)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .setSpellTiers(10)
            .setSpellTierWeights(1)
            .build());

    public static final Spell SPHERE_OF_STILLNESS = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(23, "sphere_of_stillness")
            .addManaDrain(50).addCastDelay(.25).setRadius(5).setLifetime(120).consumeOnUse(15)
            .addDescription("A field of freezing magic")
            .addExecuteOnTick(new EffectModule(5, false).setFreezing().animateRadius(0, 40, 5))
            .addExecuteOnCast(new ReplaceBlocksModule(Material.SNOW, 5, false).onlySetTopBlocks().setSlowReplace().withDelay(15))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SNOWFLAKE).setRadius(5).setDensity(10).setInSphere().animateRadius(0, 10).build())
                    .build())
            .setCustomModel(200003)
            .setSpellTiers(0,2,4,5)
            .setSpellTierWeights(0.3, 0.6, 0.7, 0.3)
            .build());

    public static final Spell SPHERE_OF_THUNDER = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(24, "sphere_of_thunder")
            .addManaDrain(60).addCastDelay(.25).setRadius(5).setLifetime(120).consumeOnUse(15)
            .addDescription("A field of electrifying magic")
            .addExecuteOnTick(new EntitySpellModule<LightningStrike>(EntityType.LIGHTNING, false).setSummonInSphere(5).setCooldown(15, 15))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setRadius(5).setDensity(10).setInSphere().animateRadius(0, 10).setSpeed(.2).build())
                    .build())
            .setCustomModel(200004)
            .setSpellTiers(1,3,5,6)
            .setSpellTierWeights(0.3, 0.6, 0.8, 0.3)
            .build());

    public static final Spell SPHERE_OF_VIGOUR = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(25, "sphere_of_vigour")
            .addManaDrain(80).addCastDelay(.25).setRadius(5).setLifetime(2.5).consumeOnUse(2)
            .addExecuteOnTick(new EffectModule(5, false).setPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 3)))
            .addDescription("A field of regenerative magic")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setRadius(5).setDensity(5).setInSphere().animateRadius(0, 10).setSpeed(.2).setExtra(new Particle.DustTransition(Color.GREEN, Color.LIME, 1)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setRadius(5).setDensity(5).setInSphere().animateRadius(0, 10).setSpeed(.2).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(255,192,203), 1)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setRadius(5).setDensity(5).setInSphere().animateRadius(0, 10).setSpeed(.2).setExtra(new Particle.DustTransition(Color.GREEN, Color.LIME, 2)).build())
                    .build())
            .setCustomModel(200005)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.3, 0.3, 0.3, 0.3)
            .build());

    public static final Spell TEST_SPELL = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(26, "test_spell")
            .addManaDrain(10).addCastDelay(.2).setRadius(1).setLifetime(30)
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleCube.ParticleStyleCubeBuilder().setParticle(Particle.FLAME).setEdgeLength(5).setParticlesPerEdge(10).setAngularVelocity(.0015, .005, 0).build())
                    .addStyle(new ParticleStyleCube.ParticleStyleCubeBuilder().setParticle(Particle.FLAME).setEdgeLength(7).setParticlesPerEdge(12).setAngularVelocity(.002, -.005, 0).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(1.5).setDensity(40).build())
                    .build())
            .build());

    public static final Spell EXPLOSION_OF_BRIMSTONE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(27, "explosion_of_brimstone")
            .addManaDrain(10).setRadius(3).addCastDelay(.05).setLifetime(.05)
            .addExecuteOnCast(new ExplodeModule(true).destroysBlocks().setsFire())
            .addDescription("A fiery explosion!")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(100).setOffset(new Vector(.3, .3, .3)).setSpeed(1).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.FLAME).setCount(60).setSpeed(.5).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(2).setDensity(40).setSpeed(.5).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(50).setOffset(new Vector(.1, .1, .1)).build())
                    .build())
            .setCustomModel(200006)
            .setSpellTiers(0,1,3,5)
            .setSpellTierWeights(0.5, 0.5, 0.6, 0.6)
            .build());

    // ----- PASSIVE SPELLS ----- ID: 3+XXX

    // ----- UTILITY SPELLS ----- ID: 4+XXX

    public static final Spell LONG_DISTANCE_CAST = register(new ProjectileSpell.ProjectileSpellBuilder(41, "long-distance_cast")
            .addManaDrain(0).setRadius(2).setVelocity(34).setLifetime(0.01).addCastDelay(-.08)
            .addTrigger(Spell.TriggerType.EXPIRATION).setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .addDescription("Casts a spell some distance away from the caster")
            .overrideSpellType(SpellType.UTILITY)
            .setCustomModel(400000)
            .setSpellTiers(0,1,2,4,5,6)
            .setSpellTierWeights(0.6, 0.6, 0.6, 0.6, 0.6, 0.6)
            .build());

    public static final Spell WARP_CAST = register(new ProjectileSpell.ProjectileSpellBuilder(42, "warp_cast")
            .addManaDrain(20).setRadius(2).setVelocity(44).setLifetime(0.01).addCastDelay(.17).setSpread(-6)
            .addTrigger(Spell.TriggerType.COLLISIONOREXPIRATION).setCollidesWithEntities(false) // TODO: Replace the trigger thing
            .addDescription("Makes a spell immediately jump a long distance, stopped by walls")
            .overrideSpellType(SpellType.UTILITY)
            .setCustomModel(400001)
            .setSpellTiers(0,1,2,4,5,6)
            .setSpellTierWeights(0.2, 0.2, 0.2, 0.6, 0.6, 0.6)
            .build());

    public static final Spell ALL_SEEING_EYE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(43, "all-seeing_eye")
            .addManaDrain(100).consumeOnUse(10).setLifetime(1)
            .addExecuteOnCast(new EffectModule(false).setTargetSelf().setPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*4*60, 0)))
            .addExecuteOnTick(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setRadius(20).animateRadius(0, 20).setDensity(200).setInSphere().setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(255,192,203), 1)).build())
                    .build(), false))
            .addDescription("See into the unexplored. But not everywhere...")
            .overrideSpellType(SpellType.UTILITY)
            .setCustomModel(400002)
            .setSpellTiers(0,1,2,3,4,5,6)
            .setSpellTierWeights(0.8, 1, 1, 0.8, 0.6, 0.4, 0.2)
            .build());

    // ----- PROJECTILE MODIFIER SPELLS ----- ID: 5+XXX

    public static final Spell ADD_MANA = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(51, "add_mana")
            .addManaDrain(-30).addCastDelay(.17)
            .addDescription("Immediately adds 30 mana to the wand")
            .setCustomModel(500000)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(1, 1, 1, 1, 1, 1)
            .build());

    public static final Spell FREEZE_CHARGE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(53, "freeze_charge")
            .addManaDrain(10)
            .addSpellModifier(new AddSpellModuleModifier().addOnEntityCollision(new DamageModule(1, false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new ReplaceBlocksModule(Material.SNOW, 5, false).onlySetTopBlocks()))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new EffectModule(5, false).setInstantFreeze(200)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new SoundSpellModule(Sound.BLOCK_SNOW_PLACE, SoundCategory.BLOCKS, false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new ParticleModule(
                    new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SNOWFLAKE).setDensity(100).setSpeed(.1).setRadius(5).setInSphere().build())
                            .build(), false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnTick(new ParticleModule(
                    new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPIT).build())
                            .build(), false)))
            .addDescription("Gives a projectile a frozen charge, that it will release on impact")
            .setCustomModel(500001)
            .setSpellTiers(1,3,4,5)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    public static final Spell ELECTRIC_CHARGE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(54, "electric_charge")
            .addManaDrain(8)
            .addSpellModifier(new AddSpellModuleModifier().addOnEntityCollision(new DamageModule(3, false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new EntitySpellModule(EntityType.LIGHTNING, false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new SoundSpellModule(Sound.ITEM_TRIDENT_THUNDER, SoundCategory.AMBIENT, false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollisionDeath(new ParticleModule(
                    new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setDensity(40).setSpeed(.3).setRadius(1).setInSphere().build())
                            .build(), false)))
            .addSpellModifier(new AddSpellModuleModifier().addOnTick(new ParticleModule(
                    new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.ELECTRIC_SPARK).build())
                            .build(), false)))
            .addDescription("Gives a projectile a electric charge, that it will release on impact")
            .setCustomModel(500002)
            .setSpellTiers(1,2,4,5)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    public static final Spell REDUCE_LIFETIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(55, "reduce_lifetime")
            .addManaDrain(10).addCastDelay(-.25)
            .addSpellModifier(new ValueSpellModifier().addLifetimeModifier(-1.8))
            .addDescription("Reduces the lifetime of a spell")
            .setCustomModel(500003)
            .setSpellTiers(3,4,5,6,10)
            .setSpellTierWeights(0.5, 0.5, 0.5, 0.5, 0.1)
            .build());

    public static final Spell INCREASE_LIFETIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(56, "increase_lifetime")
            .addManaDrain(40).addCastDelay(.22)
            .addSpellModifier(new ValueSpellModifier().addLifetimeModifier(2.7))
            .addDescription("Increases the lifetime of a spell")
            .setCustomModel(500004)
            .setSpellTiers(3,4,5,6,10)
            .setSpellTierWeights(0.5, 0.5, 0.5, 0.5, 0.1)
            .build());

    public static final Spell SPEED_UP = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(57, "speed_up")
            .addManaDrain(3)
            .addSpellModifier(new ValueSpellModifier().addVelocityMultiplier(2.5))
            .addDescription("Increases the rate at which a projectile flies through the air")
            .setCustomModel(500005)
            .setSpellTiers(1,2,3)
            .setSpellTierWeights(1, 0.5, 0.5)
            .build());

    public static final Spell EXPLOSIVE_PROJECTILE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(58, "explosive_projectile")
            .addManaDrain(30).addCastDelay(.67)
            .addSpellModifier(new ValueSpellModifier().addVelocityMultiplier(.75))
            .addSpellModifier(new ValueSpellModifier().addRadiusModifier(1.5))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollision(new ExplodeModule(false).destroysBlocks()))
            .addDescription("Makes a projectile more destructive to the environment")
            .setCustomModel(500006)
            .setSpellTiers(2,3,4)
            .setSpellTierWeights(1, 1, 1)
            .build());

    public static final Spell REDUCE_RECHARGE_TIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(59, "reduce_recharge_time")
            .addManaDrain(12).addCastDelay(-.17).addRechargeTime(-.33)
            .addDescription("Reduces the time between spellcasts")
            .setCustomModel(500007)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(1, 1, 1, 1, 1, 1)
            .build());

    public static final Spell HEAVY_SPREAD = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(510, "heavy_spread")
            .addManaDrain(2).addCastDelay(-.12).addRechargeTime(-.25)
            .addDescription("Gives a projectile a much lower cast delay, but no respect for your aim")
            .addSpellModifier(new ValueSpellModifier().addSpreadModifier(720))
            .setCustomModel(500008)
            .setSpellTiers(0,1,2,4,5,6)
            .setSpellTierWeights(0.8, 0.8, 0.8, 0.8, 0.8, 0.8)
            .build());

    public static final Spell FIRE_TRAIL = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(511, "fire_trail")
            .addManaDrain(10)
            .addDescription("Gives a projectile a trail of fiery particles")
            .addSpellModifier(new AddSpellModuleModifier()
                    .addOnTick(new TrailSpellModule(new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.FALLING_LAVA).setCount(5).setOffset(new Vector(.08, .08, .08)).build())
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.FLAME).setCount(2).setOffset(new Vector(.07, .07, .07)).build())
                            .build(), false).addDripBlock(Material.FIRE, 5)))
            .addSpellModifier(new AddSpellModuleModifier()
                    .addOnEntityCollision(new DamageModule(0, false).setsFire(60)))
            .setCustomModel(500009)
            .setSpellTiers(0,1,2,3,4)
            .setSpellTierWeights(0.3, 0.3, 0.3, 0.3, 0.3)
            .build());

    public static final Spell RAINBOW_TRAIL = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(512, "rainbow_trail")
            .addManaDrain(0)
            .addDescription("Gives a projectile a trail of rainbow")
            .addSpellModifier(new AddSpellModuleModifier()
                    .addOnTick(new TrailSpellModule(new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.DUST_COLOR_TRANSITION).setCount(3).setOffset(new Vector(.1, .1, .1)).setExtra(new Particle.DustTransition(Color.BLUE, Color.AQUA, 1)).build())
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.DUST_COLOR_TRANSITION).setCount(3).setOffset(new Vector(.1, .1, .1)).setExtra(new Particle.DustTransition(Color.AQUA, Color.GREEN, 1)).build())
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.DUST_COLOR_TRANSITION).setCount(3).setOffset(new Vector(.1, .1, .1)).setExtra(new Particle.DustTransition(Color.GREEN, Color.YELLOW, 1)).build())
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.DUST_COLOR_TRANSITION).setCount(3).setOffset(new Vector(.1, .1, .1)).setExtra(new Particle.DustTransition(Color.YELLOW, Color.ORANGE, 1)).build())
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.DUST_COLOR_TRANSITION).setCount(3).setOffset(new Vector(.1, .1, .1)).setExtra(new Particle.DustTransition(Color.ORANGE, Color.RED, 1)).build())
                            .build(), false)))
            .setCustomModel(500010)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .build());

    public static final Spell WATER_TRAIL = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(513, "water_trail")
            .addManaDrain(10)
            .addDescription("Gives a projectile a trail of fiery particles")
            .addSpellModifier(new AddSpellModuleModifier()
                    .addOnTick(new TrailSpellModule(new ParticleGroup.ParticleGroupBuilder()
                            .addStyle(new ParticleStylePoint.ParticleStylePointBuilder()
                                    .setParticle(Particle.FALLING_WATER).setCount(10).setOffset(new Vector(.1, .1, .1)).build())
                            .build(), false).addDripBlock(Material.WATER, 5)
                            .addBlockStateEditor(blockData -> {
                                if (blockData instanceof Levelled levelled) {
                                    levelled.setLevel(1);
                                    return levelled;
                                }
                                return blockData;
                            })))
            .setCustomModel(500011)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.3, 0.3, 0.3, 0.3)
            .build());

    public static final Spell BOUNCE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(514, "bounce")
            .addManaDrain(0)
            .addDescription("Makes a projectile bounce on impact")
            .addSpellModifier(new ValueSpellModifier().setIsBouncy(true, new Vector(.99, .99, .99), 10))
            .setCustomModel(500012)
            .setSpellTiers(2,3,4,5,6)
            .setSpellTierWeights(1, 1, 0.4, 0.2, 0.2)
            .build());

    public static final Spell REMOVE_BOUNCE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(515, "remove_bounce")
            .addManaDrain(0)
            .addDescription("A normally bouncy projectile stops doing so")
            .addSpellModifier(new ValueSpellModifier().setIsBouncy(false, new Vector()))
            .setCustomModel(500013)
            .setSpellTiers(2,3,4,5,6)
            .setSpellTierWeights(0.2, 0.2, 1, 1, 1)
            .build());

    // TODO: public static final Spell ROTATE_TOWARDS = register()

    // ----- MATERIAL SPELLS ----- ID: 6+XXX

    public static final Spell SEA_OF_WATER = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(60, "sea_of_water")
            .addManaDrain(140).consumeOnUse(3).setLifetime(.7).addCastDelay(.25)
            .addDescription("Summons a large body of water below the caster")
            .addExecuteOnCast(new BlockSpellModule(
                    new CubeGenerator(21, 8, 21, Material.WATER).setOffset(new Vector(-10, -10, -10))
                            .whitelistReplaceMaterials(Material.AIR, Material.CAVE_AIR), false)
            )
            .overrideSpellType(SpellType.MATERIAL)
            .setCustomModel(600000)
            .setSpellTiers(0,4,5,6)
            .setSpellTierWeights(0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell SPHERE_OF_WATER = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(61, "sphere_of_water")
            .addManaDrain(20).consumeOnUse(15).setLifetime(3).addCastDelay(.33)
            .addDescription("An expanding sphere of water")
            .addExecuteOnTick(new BlockSpellModule(
                    new SphereGenerator(2, Material.WATER)
                            .whitelistReplaceMaterials(Material.AIR, Material.CAVE_AIR)
                            .setExpandOnTick(.2)
                            .setBlockData(() -> {
                                BlockData data = Bukkit.createBlockData(Material.WATER);
                                if (data instanceof Levelled levelled) {
                                    levelled.setLevel(1);
                                    return levelled;
                                }
                                return data;
                            }), false)
            )
            .overrideSpellType(SpellType.MATERIAL)
            .setCustomModel(600001)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell SPHERE_OF_LAVA = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(62, "sphere_of_lava")
            .addManaDrain(20).consumeOnUse(15).setLifetime(2).addCastDelay(.33)
            .addDescription("An expanding sphere of lava")
            .addExecuteOnTick(new BlockSpellModule(
                    new SphereGenerator(2, Material.LAVA)
                            .whitelistReplaceMaterials(Material.AIR, Material.CAVE_AIR)
                            .setExpandOnTick(.2)
                            .setBlockData(() -> {
                                BlockData data = Bukkit.createBlockData(Material.LAVA);
                                if (data instanceof Levelled levelled) {
                                    levelled.setLevel(1);
                                    return levelled;
                                }
                                return data;
                            }), false)
            )
            .overrideSpellType(SpellType.MATERIAL)
            .setCustomModel(600002)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell CHUNK_OF_DIRT = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(63, "chunk_of_dirt")
            .addManaDrain(5).setLifetime(.05)
            .addDescription("Dirty spell")
            .addExecuteOnTick(new BlockSpellModule(
                    new SphereGenerator(3, Material.DIRT)
                            .whitelistReplaceMaterials(Material.AIR, Material.CAVE_AIR), false)
            )
            .overrideSpellType(SpellType.MATERIAL)
            .setCustomModel(600003)
            .setSpellTiers(1,2,3,5)
            .setSpellTierWeights(1, 1, 1, 1)
            .build());

    // ----- MULTICAST SPELLS ----- ID: 7+XXX

    public static final Spell TUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(70, "tuple_spell")
            .addManaDrain(1)
            .addDescription("Simultaneously casts 2 spells")
            .setNumberSpellsToCast(2)
            .setCustomModel(700000)
            .setSpellTiers(0,1,2,3,4,5,6)
            .setSpellTierWeights(0.8, 0.8, 0.8, 0.8, 0.8, 0.8, 0.8)
            .build());

    public static final Spell TRIPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(71, "triple_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 3 spells")
            .setNumberSpellsToCast(3)
            .setCustomModel(700001)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(0.7, 0.7, 0.7, 0.7, 0.7, 0.7)
            .build());

    public static final Spell QUADRUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(72, "quadruple_spell")
            .addManaDrain(5)
            .addDescription("Simultaneously casts 4 spells")
            .setNumberSpellsToCast(4)
            .setCustomModel(700002)
            .setSpellTiers(2,3,4,5,6)
            .build());

    public static final Spell OCTUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(73, "octuple_spell")
            .addManaDrain(30)
            .addDescription("Simultaneously casts 8 spells")
            .setNumberSpellsToCast(8)
            .setCustomModel(700003)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.6, 0.6, 0.6, 0.6, 0.6)
            .build());

    public static final Spell TUPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(74, "tuple_scatter_spell")
            .addManaDrain(1)
            .addDescription("Simultaneously casts 2 spells with low accuracy")
            .setNumberSpellsToCast(2)
            .setCustomModel(700004)
            .setIncreaseSpread(10)
            .setSpellTiers(0,1,2)
            .setSpellTierWeights(0.8, 0.8, 0.8)
            .build());

    public static final Spell TRIPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(75, "triple_scatter_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 3 spells with low accuracy")
            .setNumberSpellsToCast(3)
            .setCustomModel(700005)
            .setIncreaseSpread(20)
            .setSpellTiers(0,1,2,3)
            .setSpellTierWeights(0.7, 0.7, 0.7, 0.8)
            .build());

    public static final Spell QUADRUPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(76, "quadruple_scatter_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 4 spells with low accuracy")
            .setNumberSpellsToCast(4)
            .setCustomModel(700006)
            .setIncreaseSpread(40)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(0.6, 0.6, 0.7, 0.8, 0.8, 0.8)
            .build());

    public static final Spell BEHIND_YOUR_BACK = register(new MulticastSpell.MulticastSpellBuilder(77, "behind_your_back")
            .addManaDrain(1)
            .addDescription("Casts two spells: one ahead of and one behind the caster")
            .setFormation(MulticastSpell.Formation.BEHIND_BACK)
            .setCustomModel(700007)
            .setSpellTiers(1,2,3,4)
            .setSpellTierWeights(0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell ABOVE_AND_BELOW = register(new MulticastSpell.MulticastSpellBuilder(78, "above_and_below")
            .addManaDrain(3)
            .addDescription("Casts 3 spells - ahead, above and below the caster")
            .setFormation(MulticastSpell.Formation.ABOVE_AND_BELOW)
            .setCustomModel(700008)
            .setSpellTiers(1,2,3,4,5)
            .setSpellTierWeights(0.4, 0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell PENTAGON = register(new MulticastSpell.MulticastSpellBuilder(79, "pentagon")
            .addManaDrain(5)
            .addDescription("Casts 5 spells in a pentagonal pattern")
            .setFormation(MulticastSpell.Formation.PENTAGON)
            .setCustomModel(700009)
            .setSpellTiers(1,2,3,4,5)
            .setSpellTierWeights(0.4, 0.4, 0.3, 0.2, 0.1)
            .build());

    public static final Spell HEXAGON = register(new MulticastSpell.MulticastSpellBuilder(710, "hexagon")
            .addManaDrain(6)
            .addDescription("Casts 6 spells in a hexagonal pattern")
            .setFormation(MulticastSpell.Formation.HEXAGON)
            .setCustomModel(700010)
            .setSpellTiers(1,2,3,4,5,6)
            .setSpellTierWeights(0.1, 0.2, 0.3, 0.3, 0.3, 0.3)
            .build());

    public static final Spell BIFURCATED = register(new MulticastSpell.MulticastSpellBuilder(711, "bifurcated")
            .addManaDrain(2)
            .addDescription("Casts 2 spells in a bifurcated pattern")
            .setFormation(MulticastSpell.Formation.BIFURCATED)
            .setCustomModel(700011)
            .setSpellTiers(0,1,2,3,4)
            .setSpellTierWeights(0.8, 0.4, 0.4, 0.4, 0.4)
            .build());

    public static final Spell TRIFURCATED = register(new MulticastSpell.MulticastSpellBuilder(712, "trifurcated")
            .addManaDrain(3)
            .addDescription("Casts 3 spells in a trifurcated pattern")
            .setFormation(MulticastSpell.Formation.TRIFURCATED)
            .setCustomModel(700012)
            .setSpellTiers(2,3,4,5,6)
            .setSpellTierWeights(0.4, 0.3, 0.3, 0.3, 0.3)
            .build());

    // ----- OTHER SPELLS ----- ID: 8+XXX

    public static final Spell OCARINA_A = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(81, "ocarina_note_a")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.A), false))
            //.addExecuteOnCast(new SoundSpellModule(Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.PLAYERS, false).setPitch((float)Math.pow(2, 3f/12f)))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800000)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_B = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(82, "ocarina_note_b")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.B), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800001)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_C = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(83, "ocarina_note_c")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.C), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800002)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_D = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(84, "ocarina_note_d")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800003)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_E = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(85, "ocarina_note_e")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.E), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800004)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_F = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(86, "ocarina_note_f")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.F), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800005)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_G_SHARP = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(87, "ocarina_note_g_sharp")
            .overrideSpellName("Ocarina Note G#")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.sharp(0, Note.Tone.G), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800006)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell OCARINA_A_TWO = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(88, "ocarina_note_a2")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(1, Note.Tone.A), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800007)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell KANTELE_A = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(89, "kantele_note_a")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.A), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800008)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell KANTELE_D = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(810, "kantele_note_d")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800009)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell KANTELE_D_SHARP = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(811, "kantele_note_d_sharp")
            .overrideSpellName("Ocarina Note D#")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.sharp(1, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800010)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell KANTELE_E = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(812, "kantele_note_e")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.E), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800011)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell KANTELE_G = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(813, "kantele_note_g")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.G), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(800012)
            .setSpellTiers(10)
            .setSpellTierWeights(0)
            .addTag("note")
            .build());

    public static final Spell ALPHA = register(new InstantSpell.InstantSpellBuilder(814, "alpha", InstantSpell.CastMethod.ALPHA)
            .addManaDrain(30).addCastDelay(.25)
            .addDescription("Casts a copy of the first spell in your wand")
            .setCustomModel(800013)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell GAMMA = register(new InstantSpell.InstantSpellBuilder(815, "gamma", InstantSpell.CastMethod.GAMMA)
            .addManaDrain(30).addCastDelay(.25)
            .addDescription("Casts a copy of the last spell in your wand")
            .setCustomModel(800014)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell MU = register(new InstantSpell.InstantSpellBuilder(816, "mu", InstantSpell.CastMethod.MU)
            .addManaDrain(120).addCastDelay(.83)
            .addDescription("Every modifier-type spell in the current wand is applied to a projectile")
            .setCustomModel(800015)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell OMEGA = register(new InstantSpell.InstantSpellBuilder(817, "omega", InstantSpell.CastMethod.OMEGA)
            .addManaDrain(300).addCastDelay(.83)
            .addDescription("Casts copies of every spell in your wand")
            .setCustomModel(800016)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell PHI = register(new InstantSpell.InstantSpellBuilder(818, "phi", InstantSpell.CastMethod.PHI)
            .addManaDrain(120).addCastDelay(.83)
            .addDescription("Casts a copy of every projectile-type spell in the current wand")
            .setCustomModel(800017)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell SIGMA = register(new InstantSpell.InstantSpellBuilder(819, "sigma", InstantSpell.CastMethod.SIGMA)
            .addManaDrain(120).addCastDelay(.5)
            .addDescription("Copies every static projectile-type spell in the wand when cast")
            .setCustomModel(800018)
            .setSpellTiers(4,5,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell TAU = register(new InstantSpell.InstantSpellBuilder(820, "tau", InstantSpell.CastMethod.TAU)
            .addManaDrain(80).addCastDelay(.58)
            .addDescription("Copies the two following spells in the wand when cast")
            .setCustomModel(800019)
            .setSpellTiers(5,6,10)
            .setSpellTierWeights(0.1, 0.1, 1)
            .build());

    public static final Spell ZETA = register(new InstantSpell.InstantSpellBuilder(821, "zeta", InstantSpell.CastMethod.ZETA)
            .addManaDrain(10)
            .addDescription("Copies a random spell in another wand you're carrying")
            .setCustomModel(800020)
            .setSpellTiers(1,2,3,10)
            .setSpellTierWeights(0.2, 0.8, 0.6, 0.1)
            .build());

    public static final Spell RANDOM_SPELL = register(new InstantSpell.InstantSpellBuilder(822, "random_spell", InstantSpell.CastMethod.RANDOM_ANY)
            .addManaDrain(5)
            .addDescription("Casts a spell, any spell, at random!")
            .setCustomModel(800021)
            .setSpellTiers(3,4,5,6,10)
            .setSpellTierWeights(0.2, 0.3, 0.1, 0.1, 0.5)
            .build());

    // ----- CUSTOM SPELLS ----- ID: 9+XXX // TODO: Make a spell type and make it not appear in cgive list

    private static final Spell alchemist_dark_spell = register(new ProjectileSpell.ProjectileSpellBuilder(90, "alchemist_dark_spell")
            .addManaDrain(1).setRadius(.2).setSpread(2).setVelocity(24).setLifetime(1.3)
            .addExecuteOnEntityCollision(new DamageModule(4, true))
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.SOUL).setCount(10).setSpeed(.04).setDensity(3).build())
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.SOUL_FIRE_FLAME).setCount(2).setSpeed(0).setDensity(3).build())
                    .build())
            .addExecuteOnCast(new SoundSpellModule("minecraft:magic.zap", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:magic.hit", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addDescription("This spell is intended for use by the Alchemist boss, not for gameplay!")
            .build());

    private static final Spell alchemist_glowing_spell = register(new ProjectileSpell.ProjectileSpellBuilder(91, "alchemist_glowing_spell")
            .addManaDrain(1).setRadius(.2).setSpread(10).setVelocity(20).setLifetime(1.6)
            .addExecuteOnEntityCollision(new DamageModule(4, true))
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.END_ROD).setCount(3).setOffset(new Vector(.1, .1, .1)).setDensity(3).build())
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.WHITE, Color.YELLOW, 1)).setOffset(new Vector(.2, .2, .2)).setDensity(3).build())
                    .build())
            .addExecuteOnCast(new SoundSpellModule("minecraft:magic.zap", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnCast(new SoundSpellModule("minecraft:block.beacon.deactivate", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:magic.hit", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addDescription("This spell is intended for use by the Alchemist boss, not for gameplay!")
            .setCollidesWithBlocks(false)
            .build());

    private static final Spell alchemist_volatile_spell = register(new ProjectileSpell.ProjectileSpellBuilder(92, "alchemist_volatile_spell")
            .addManaDrain(1).setRadius(.4).setSpread(0).setVelocity(17).setLifetime(2)
            .addExecuteOnEntityCollision(new DamageModule(10, true))
            .addExecuteOnCollision(new ExplodeModule(true).overrideRadius(2))
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.LAVA).setCount(7).setOffset(new Vector(.25, .25, .25)).setDensity(3).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(.35).setDensity(10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(.25).setDensity(5).build())
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.SMOKE_LARGE).setCount(2).setOffset(new Vector(.05, .05, .05)).setDensity(3).build())
                    .build())
            .addExecuteOnCast(new SoundSpellModule("minecraft:magic.zap", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnCast(new SoundSpellModule("minecraft:item.firecharge.use", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:magic.hit", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addDescription("This spell is intended for use by the Alchemist boss, not for gameplay!")
            .build());

    private static final Spell alchemist_thunder_spell = register(new ProjectileSpell.ProjectileSpellBuilder(93, "alchemist_thunder_spell")
            .addManaDrain(1).setRadius(.4).setSpread(0).setVelocity(20).setLifetime(1.6)
            .addExecuteOnEntityCollision(new DamageModule(15, true))
            .addExecuteOnEntityCollision(new EntitySpellModule(EntityType.LIGHTNING, true))
            .addExecuteOnEntityCollision(new EffectModule(true).setPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 1, true, false)))
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.END_ROD).setCount(2).setOffset(new Vector(.1, .1, .1)).setDensity(3).build())
                    .addStyle(new ParticleStyleLine.ParticleStyleLineBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.BLUE, Color.SILVER, 1)).setCount(4).setOffset(new Vector(.2, .2, .2)).setDensity(3).build())
                    .build())
            .addExecuteOnCast(new SoundSpellModule("minecraft:magic.zap", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnCast(new SoundSpellModule("minecraft:block.beacon.deactivate", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:magic.hit", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:item.trident.thunder", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnCollision(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.ELECTRIC_SPARK).setCount(10).setOffset(new Vector(1, 1, 1)).build())
                    .build(), false))
            .addDescription("This spell is intended for use by the Alchemist boss, not for gameplay!")
            .build());

    public static final Spell ALCHEMIST_MAIN_SPELL = register(new ProjectileSpell.ProjectileSpellBuilder(94, "alchemist_main_spell")
            .addManaDrain(1).setRadius(.1).setSpread(2).setVelocity(8).setLifetime(6)
            .addExecuteOnEntityCollision(new DamageModule(12, true))
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.MAROON, 1)).setCount(10).setSpeed(.04).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.BLUE, Color.WHITE, 2)).setCount(2).setSpeed(0).build())
                    .build())
            .addExecuteOnCast(new SoundSpellModule("minecraft:magic.zap", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new SoundSpellModule("minecraft:magic.hit", SoundCategory.HOSTILE, false).setVolume(.5f))
            .addExecuteOnEntityCollision(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CRIT_MAGIC).setCount(10).setOffset(new Vector(.2, .2, .2)).build())
                    .build(), false))
            .addDescription("This spell is intended for use by the Alchemist boss, not for gameplay!")
            .addMovementModifier(new HomingMovementModifier().setRotateTowards(20))
            .build());

    public static final Spell FIRESPITTER = register(new ProjectileSpell.ProjectileSpellBuilder(95, "firespitter")
            .addManaDrain(1).setRadius(.7).setSpread(1).setVelocity(7.3).setLifetime(10).addCastDelay(.5).addGravity(0.4).consumeOnUse(25)

            .addExecuteOnCollision(new ReplaceBlocksModule(Material.FIRE, 2, true).onlySetTopBlocks())
            .addExecuteOnCollision(new ExplodeModule(true).setsFire().overrideRadius(2))
            .addExecuteOnCollision(new ParticleModule(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.LAVA).setInSphere().setDensity(70).setRadius(2).setSpeed(.5).build()
            ).build(), true))

            .addExecuteOnDeath(new ReplaceBlocksModule(Material.FIRE, 2, true).onlySetTopBlocks())
            .addExecuteOnDeath(new ExplodeModule(true).setsFire().overrideRadius(2))
            .addExecuteOnDeath(new ParticleModule(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.LAVA).setInSphere().setDensity(70).setRadius(2).setSpeed(.5).build()
            ).build(), true))

            .addDescription("A heavy, explosive sphere")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.FLAME).setCount(3).setOffset(new Vector(.1, .1, .1)).build()
            ).addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(1).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(5)
            .build());

    /**
     * Registers a spell.
     *
     * @param spell the spell to register
     * @param <T> the spell type.
     * @return the registered spell.
     */
    private static <T extends Spell> T register(final T spell){
        INBUILT_SPELLS.put(spell.getId(), spell);
        return spell;
    }

    // ----- SPELL STORING -----

    public void addActiveSpell(Spell spell, BukkitTask task, int hashCode){
        activeSpells.put(hashCode, spell);
        activeSpellTasks.put(hashCode, task);
    }
    public void removeActiveSpell(int hashCode){
        activeSpells.remove(hashCode);
        activeSpellTasks.remove(hashCode);
    }

    public void cancelSpell(int hashCode) {
        activeSpells.remove(hashCode);
        activeSpellTasks.get(hashCode).cancel();
        activeSpellTasks.remove(hashCode);
    }

    /**
     * Kills all active spells. Returns the number of spells killed
     *
     * @return number of spells killed
     */
    public int killAllSpells(){
        int nSpells = activeSpellTasks.size();
        for (BukkitTask s : activeSpellTasks.values()){
            s.cancel();
        }
        activeSpells.clear();
        activeSpellTasks.clear();
        return nSpells;
    }

    /**
     * Kills all active spells that are within the specified distance.
     *
     * @param location the center location from where to kill the spells from.
     * @param distance the max distance to the spells.
     * @return the number of spells killed.
     */
    public int killAllSpells(Location location, double distance) {
        int nSpells = 0;
        for (int hashCode : activeSpellTasks.keySet()) {
            ISpell spell = activeSpells.get(hashCode);
            if (spell.getLocation().distanceSquared(location) <= distance*distance) {
                cancelSpell(hashCode);
                nSpells++;
            }
        }
        return nSpells;
    }

    // ----- SPELL JSON PARSING -----

    private static void loadSpellsFromJson() {
        String[] spellFileNames = FileUtil.getResources(CobaltMagick.class, "spells");
        for (String s : spellFileNames) {
            File fileToDelete = FileUtil.getOrCreateFileFromResource(CobaltMagick.getInstance(), "spells/" + s);
            if (fileToDelete.exists()) fileToDelete.delete();

            loadSpell(FileUtil.getOrCreateFileFromResource(CobaltMagick.getInstance(), "spells/" + s));
        }
    }

    private static void loadSpell(File file) {
        CobaltMagick.getInstance().getLogger().info("Loading spell " + file.getAbsolutePath() + " from file...");

        if (!file.exists()) return;

        try (
                InputStream is = new FileInputStream(file.getPath());
                Reader reader = Files.newBufferedReader(Paths.get(file.getPath()))
        ) {
            // Create gson instance
            Gson gson = new Gson();

            // Convert JSON file to a map
            Map<?, ?> jsonMap = gson.fromJson(reader, Map.class);

            // Print map entries
            for (Map.Entry<?, ?> entry : jsonMap.entrySet()) {
                CobaltMagick.getInstance().getLogger().info(entry.getKey() + "=" + entry.getValue());
            }

            String jsonText = IOUtils.toString(is, "UTF-8");
            // Convert JSON file to JSONObject
            JsonObject rootObject = new Gson().fromJson(jsonText, JsonObject.class);

            // Load basic spell information // TODO: Move to abstract spell class

            int id = rootObject.get("id").getAsInt();
            String spellType = rootObject.get("spell_type").getAsString();
            String internalSpellName = rootObject.get("spell_name").getAsString();
            String description = rootObject.get("description").getAsString();
            int customModel = rootObject.get("custom_model").getAsInt();

            // Create the spell builder
            Spell.SpellBuilder<?, ?> builder;
            switch (spellType) {
                case "projectile" -> {
                    builder = new ProjectileSpell.ProjectileSpellBuilder(id, internalSpellName);

                    // Set projectile specific stats
                    double velocity = rootObject.get("velocity").getAsDouble();
                    double lifetime = rootObject.get("lifetime").getAsDouble();
                    double spread = rootObject.get("spread").getAsDouble();
                    ((ProjectileSpell.ProjectileSpellBuilder) builder)
                            .setVelocity(velocity)
                            .setLifetime(lifetime)
                            .setSpread(spread);
                }
                case "static_projectile" -> builder = new StaticProjectileSpell.StaticProjectileSpellBuilder(id, internalSpellName);
                case "projectile_modifier" -> {
                    builder = new ProjectileModifierSpell.ProjectileModifierSpellBuilder(id, internalSpellName);
                }
                default -> builder = new ProjectileSpell.ProjectileSpellBuilder(id, internalSpellName);
            }

            // Cosmetic settings
            if (description != null) builder.addDescription(description);
            builder.setCustomModel(customModel);

            // Generic spell settings
            int manaDrain = rootObject.get("mana_drain").getAsInt();
            double radius = rootObject.get("radius").getAsDouble();
            double castDelay = rootObject.get("cast_delay").getAsDouble();

            // Spell tiers
            JsonArray spellTiers = rootObject.get("spell_tiers").getAsJsonArray();
            int[] spellTiersArray = new int[spellTiers.size()];
            for (int i = 0; i < spellTiers.size(); i++) spellTiersArray[i] = spellTiers.get(i).getAsInt();
            builder.setSpellTiers(spellTiersArray);

            builder.addManaDrain(manaDrain);
            builder.setRadius(radius);
            builder.addCastDelay(castDelay);

            // Register spell
            register(builder.build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets a <code>RandomCollection</code> of <code>ISpell</code>'s with the given spell tier.
     *
     * @param spellTier the tier of the spells to get.
     * @return a <code>RandomCollection</code>.
     */
    public static RandomCollection<ISpell> getWeightedSpellCollection(int spellTier) {
        RandomCollection<ISpell> randomCollection = new RandomCollection<>();
        for (ISpell spell : INBUILT_SPELLS.values()) {
            for (int i = 0; i < spell.getSpellTiers().length; i++) {
                int currentTier = spell.getSpellTiers()[i];
                if (spell.getSpellTierWeights().length > i && currentTier == spellTier) {
                    double weight = spell.getSpellTierWeights()[i];

                    if (weight != 0) randomCollection.add(weight, spell);
                } else {
                    randomCollection.add(1, spell);
                }
            }
        }

        return randomCollection;
    }

    /**
     * Gets an array of all <code>ISpell</code>'s of the specified tier.
     *
     * @param spellTier the tier to get the <code>ISpell</code>'s of.
     * @return an array of <code>ISpell</code>'s.
     */
    public static ISpell[] getSpellsOfTier(int spellTier) {
        List<ISpell> spellsOfTier = new ArrayList<>();
        for (ISpell spell : INBUILT_SPELLS.values()) {
            for (int i : spell.getSpellTiers()) {
                if (i == spellTier) spellsOfTier.add(spell.clone());
            }
        }
        return spellsOfTier.toArray(new ISpell[0]);
    }

    /**
     * Gets an array of all <code>ISpell</code>'s of the specified type.
     *
     * @param type the type to get the <code>ISpell</code>'s of.
     * @return an array of <code>ISpell</code>'s.
     */
    public static ISpell[] getSpellsOfType(SpellType type) {
        List<ISpell> spellsOfType = new ArrayList<>();
        for (ISpell spell : INBUILT_SPELLS.values()) {
            if (spell.getSpellType() == type) spellsOfType.add(spell.clone());
        }
        return spellsOfType.toArray(new ISpell[0]);
    }

    /**
     * Gets a list of all spell type names.
     *
     * @return a list of all spell type names.
     */
    public static String[] getTypeNames() {
        SpellType[] types = SpellType.values();
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++){
            SpellType type = types[i];
            typeNames[i] = type.toString().toLowerCase();
        }
        return typeNames;
    }

    /**
     * Gets a list of all spell names.
     *
     * @return a list of spell names.
     */
    public static String[] getSpellNames() {
        String[] names = new String[INBUILT_SPELLS.size()];
        List<ISpell> spells = getAllSpells();
        for (int i = 0; i < INBUILT_SPELLS.size(); i++) {
            names[i] = spells.get(i).getInternalSpellName();
        }
        return names;
    }

    /**
     * Returns a list of all registered spells
     *
     * @return a list of spells
     */
    public static List<ISpell> getAllSpells(){
        return new ArrayList<>(INBUILT_SPELLS.values());
    }

    /**
     * Returns a new spell from the given name
     *
     * @param name the name of the spell to get
     * @return a new spell
     */
    public static ISpell getSpell(String name){
        List<ISpell> sps = new ArrayList<>(INBUILT_SPELLS.values());

        for (ISpell s : sps) {
            if (s.getInternalSpellName().equalsIgnoreCase(name)) return s.clone();
        }
        return null;
    }

    /**
     * Returns a spell from the given id
     * @param id id of the spell
     * @return the spell with the given id
     */
    public static ISpell getSpell(int id){
        ISpell spell = INBUILT_SPELLS.get(id);
        if (spell != null) return spell.clone();
        else return null;
    }

    /**
     * Returns a new spell from the given <code>ItemStack</code>
     * @param stack <code>ItemStack</code> to convert to a spell
     * @return a <code>ISpell</code> corresponding to the item
     */
    public static ISpell getSpell(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(spellKey, PersistentDataType.INTEGER)){
            int spellId = container.get(spellKey, PersistentDataType.INTEGER);
            ISpell spell = getSpell(spellId);
            if (spell != null) spell.setCount(stack.getAmount());
            return spell;
        } else {
            return null;
        }
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static SpellManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static SpellManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SpellManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    // ----- CONSTRUCTOR -----

    public SpellManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        // Load spells from json
        // TODO: loadSpellsFromJson();

        // Register all spell items as custom items
        for (ISpell spell : getAllSpells()) CustomItemManager.register(spell.getSpellCustomItem());
    }

    @Override
    public void disable() {

    }
}
