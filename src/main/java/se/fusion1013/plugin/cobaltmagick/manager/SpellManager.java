package se.fusion1013.plugin.cobaltmagick.manager;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltmagick.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobaltmagick.spells.*;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.AddSpellModuleModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodifiers.ValueSpellModifier;
import se.fusion1013.plugin.cobaltmagick.spells.spellmodules.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellManager extends Manager {

    public static final Map<Integer, Spell> INBUILT_SPELLS = new HashMap<>();
    static NamespacedKey spellKey = new NamespacedKey(CobaltMagick.getInstance(), "spell");

    private Map<Integer, Spell> activeSpells = new HashMap<>();
    private Map<Integer, BukkitTask> activeSpellTasks = new HashMap<>();
    public void addActiveSpell(Spell spell, BukkitTask task, int hashCode){
        activeSpells.put(hashCode, spell);
        activeSpellTasks.put(hashCode, task);
    }
    public void removeActiveSpell(int hashCode){
        activeSpells.remove(hashCode);
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


    private static SpellManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static SpellManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SpellManager(CobaltMagick.getInstance());
        }
        return INSTANCE;
    }

    // ----- PROJECTILE SPELLS ----- ID: 1+XXX

    public static final Spell SPARK_BOLT = register(new ProjectileSpell.ProjectileSpellBuilder(10, "spark_bolt")
            .addManaDrain(5).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setSpread(-1).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(2, true).setCriticalChance(5))
            .addDescription("A weak but enchanting sparkling projectile")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(2)
            .build());

    public static final Spell BUBBLE_SPARK = register(new ProjectileSpell.ProjectileSpellBuilder(11, "bubble_spark")
            .addManaDrain(5).setRadius(.4).setSpread(22.9).setVelocity(5).setLifetime(2).addCastDelay(-0.08)
            .addExecuteOnEntityCollision(new DamageModule(1, true))
            .addDescription("A bouncy, inaccurate spell")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).build()
            ).build())
            .setCustomModel(3)
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
            .setCustomModel(4)
            .build());

    public static final Spell FIREBOLT = register(new ProjectileSpell.ProjectileSpellBuilder(13, "firebolt")
            .addManaDrain(50).setRadius(.7).setSpread(2.9).setVelocity(5.3).setLifetime(10).addCastDelay(.5).addGravity(2).consumeOnUse(25)
            .addExecuteOnEntityCollision(new ExplodeModule(true).setsFire().destroysBlocks())
            .addExecuteOnDeath(new ExplodeModule(true).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(true).setsFire().destroysBlocks().onlyIfVelocityExceeds(.75))
            .addDescription("A bouncy, explosive bolt")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.FLAME).setCount(3).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(5)
            .build());

    public static final Spell BURST_OF_AIR = register(new ProjectileSpell.ProjectileSpellBuilder(14, "burst_of_air")
            .addManaDrain(5).setRadius(.4).setVelocity(8).setLifetime(0.8).addCastDelay(.05).setSpread(-2)
            .addExecuteOnEntityCollision(new DamageModule(6, true).setKnockback(3))
            .addDescription("A brittle burst of air capable of greatly pushing objects")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).setCount(2).build()
            ).build())
            .setCustomModel(6)
            .build());

    public static final Spell SPARK_BOLT_WITH_TRIGGER = register(new ProjectileSpell.ProjectileSpellBuilder(15, "spark_bolt_with_trigger")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.COLLISION)
            .setCustomModel(8)
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
            .setCustomModel(22)
            .build());

    public static final Spell SPARK_BOLT_WITH_TIMER = register(new ProjectileSpell.ProjectileSpellBuilder(17, "spark_bolt_with_timer")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(1.6).addCastDelay(0.05).setAffectedByAirResistance(false)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(245, 66, 239), 1)).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.TIMER)
            .setCustomModel(23)
            .build());

    public static final Spell BLACK_HOLE = register(new ProjectileSpell.ProjectileSpellBuilder(18, "black_hole")
            .addManaDrain(180).setRadius(4).setVelocity(4).setLifetime(2.4).addCastDelay(1.33).consumeOnUse(3)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 4, false).setDropItems().setSlowReplace().setReplaceNonAir())
            .addDescription("A slow orb of void that eats through all obstacles")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(4).setDensity(75).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(4).setDensity(75).build())
                    .build())
            .setCustomModel(24)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .build());

    public static final Spell DIGGING_BLAST = register(new ProjectileSpell.ProjectileSpellBuilder(19, "digging_blast")
            .addManaDrain(0).setRadius(1).setVelocity(.01).setLifetime(.01).addCastDelay(0.02).addRechargeTime(-0.17)
            .addExecuteOnEntityCollision(new DamageModule(3, true))
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 1, false).setReplaceNonAir().setDropItems())
            .addDescription("A weak but enchanting sparkling projectile")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(10).setOffset(new Vector(.1, .1, .1)).build())
                    .build())
            .setCustomModel(25)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
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
            .setCustomModel(28)
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
            .setCustomModel(29)
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
            .setCustomModel(35)
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
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.END_ROD).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(36)
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
            .setCustomModel(37)
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
            .setCustomModel(38)
            .build());

    // ----- STATIC PROJECTILE SPELLS ----- ID: 2+XXX

    public static final Spell SPHERE_OF_BUOYANCY = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(20, "sphere_of_buoyancy")
            .addManaDrain(10).addCastDelay(.25).setRadius(5).setLifetime(120).consumeOnUse(15)
            .addDescription("A field of levitative magic")
            .addExecuteOnTick(new EffectModule(5, false).setPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2, 0, false, false)).animateRadius(0, 10, 5))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .build())
            .setCustomModel(7)
            .build());

    public static final Spell GIGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(21, "giga_black_hole")
            .addManaDrain(240).setRadius(8).setLifetime(10).addCastDelay(1.33).consumeOnUse(6)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 8, false).setDropItems().setReplaceNonAir().setSlowReplace().animateRadius(0, 40, 8))
            .addDescription("A growing orb of negative energy that destroys everything in its reach")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(8).setDensity(150).animateRadius(0, 40).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(8).setDensity(150).animateRadius(0, 40).build())
                    .build())
            .setCustomModel(26)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .build());

    public static final Spell OMEGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(22, "omega_black_hole")
            .addManaDrain(500).setRadius(20).setLifetime(20).addCastDelay(1).addRechargeTime(1.67).consumeOnUse(6)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 20, false).setReplaceNonAir().setSlowReplace().animateRadius(0, 80, 20))
            .addDescription("Even light dies eventually...")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(20).setDensity(300).animateRadius(0, 80).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(20).setDensity(300).animateRadius(0, 80).build())
                    .build())
            .setCustomModel(27)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
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
            .setCustomModel(32)
            .build());

    public static final Spell SPHERE_OF_THUNDER = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(24, "sphere_of_thunder")
            .addManaDrain(60).addCastDelay(.25).setRadius(5).setLifetime(120).consumeOnUse(15)
            .addDescription("A field of electrifying magic")
            .addExecuteOnTick(new EntitySpellModule(EntityType.LIGHTNING, false).setCooldown(15, 15).setSummonInSphere(5))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().animateRadius(0, 10).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.ELECTRIC_SPARK).setRadius(5).setDensity(10).setInSphere().animateRadius(0, 10).setSpeed(.2).build())
                    .build())
            .setCustomModel(33)
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
            .setCustomModel(39)
            .build());

    // ----- PASSIVE SPELLS ----- ID: 3+XXX

    // ----- UTILITY SPELLS ----- ID: 4+XXX

    public static final Spell LONG_DISTANCE_CAST = register(new ProjectileSpell.ProjectileSpellBuilder(41, "long-distance_cast")
            .addManaDrain(0).setRadius(2).setVelocity(34).setLifetime(0.01).addCastDelay(-.08)
            .addTrigger(Spell.TriggerType.EXPIRATION).setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .addDescription("Casts a spell some distance away from the caster")
            .overrideSpellType(Spell.SpellType.UTILITY)
            .setCustomModel(42)
            .build());

    public static final Spell WARP_CAST = register(new ProjectileSpell.ProjectileSpellBuilder(42, "warp_cast")
            .addManaDrain(20).setRadius(2).setVelocity(44).setLifetime(0.01).addCastDelay(.17).setSpread(-6)
            .addTrigger(Spell.TriggerType.COLLISIONOREXPIRATION).setCollidesWithEntities(false) // TODO: Replace the trigger thing
            .addDescription("Makes a spell immediately jump a long distance, stopped by walls")
            .overrideSpellType(Spell.SpellType.UTILITY)
            .setCustomModel(59)
            .build());

    public static final Spell ALL_SEEING_EYE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(43, "all-seeing_eye")
            .addManaDrain(100).consumeOnUse(10).setLifetime(1)
            .addExecuteOnCast(new EffectModule(false).setPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*4*60, 0)))
            .addExecuteOnTick(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.DUST_COLOR_TRANSITION).setRadius(20).animateRadius(0, 20).setDensity(200).setInSphere().setExtra(new Particle.DustTransition(Color.PURPLE, Color.fromRGB(255,192,203), 1)).build())
                    .build(), false))
            .addDescription("See into the unexplored. But not everywhere...")
            .overrideSpellType(Spell.SpellType.UTILITY)
            .setCustomModel(60)
            .build());

    // ----- PROJECTILE MODIFIER SPELLS ----- ID: 5+XXX

    public static final Spell ADD_MANA = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(51, "add_mana")
            .addManaDrain(-30).addCastDelay(.17)
            .addDescription("Immediately adds 30 mana to the wand")
            .setCustomModel(30)
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
            .setCustomModel(31)
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
            .setCustomModel(34)
            .build());

    public static final Spell REDUCE_LIFETIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(55, "reduce_lifetime")
            .addManaDrain(10).addCastDelay(-.25)
            .addSpellModifier(new ValueSpellModifier().addLifetimeModifier(-1.8))
            .addDescription("Reduces the lifetime of a spell")
            .setCustomModel(40)
            .build());

    public static final Spell INCREASE_LIFETIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(56, "increase_lifetime")
            .addManaDrain(40).addCastDelay(.22)
            .addSpellModifier(new ValueSpellModifier().addLifetimeModifier(2.7))
            .addDescription("Increases the lifetime of a spell")
            .setCustomModel(41)
            .build());

    public static final Spell SPEED_UP = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(57, "speed_up")
            .addManaDrain(3)
            .addSpellModifier(new ValueSpellModifier().addVelocityMultiplier(2.5))
            .addDescription("Increases the rate at which a projectile flies through the air")
            .setCustomModel(43)
            .build());

    public static final Spell EXPLOSIVE_PROJECTILE = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(58, "explosive_projectile")
            .addManaDrain(30).addCastDelay(.67)
            .addSpellModifier(new ValueSpellModifier().addVelocityMultiplier(.75))
            .addSpellModifier(new ValueSpellModifier().addRadiusModifier(1.5))
            .addSpellModifier(new AddSpellModuleModifier().addOnCollision(new ExplodeModule(false).destroysBlocks()))
            .addDescription("Makes a projectile more destructive to the environment")
            .setCustomModel(44)
            .build());

    public static final Spell REDUCE_RECHARGE_TIME = register(new ProjectileModifierSpell.ProjectileModifierSpellBuilder(59, "reduce_recharge_time")
            .addManaDrain(12).addCastDelay(-.17).addRechargeTime(-.33)
            .addDescription("Reduces the time between spellcasts")
            .setCustomModel(45)
            .build());

    // ----- MATERIAL SPELLS ----- ID: 6+XXX

    // ----- MULTICAST SPELLS ----- ID: 7+XXX

    public static final Spell TUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(70, "tuple_spell")
            .addManaDrain(1)
            .addDescription("Simultaneously casts 2 spells")
            .setNumberSpellsToCast(2)
            .setCustomModel(9)
            .build());

    public static final Spell TRIPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(71, "triple_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 3 spells")
            .setNumberSpellsToCast(3)
            .setCustomModel(10)
            .build());

    public static final Spell QUADRUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(72, "quadruple_spell")
            .addManaDrain(5)
            .addDescription("Simultaneously casts 4 spells")
            .setNumberSpellsToCast(4)
            .setCustomModel(11)
            .build());

    public static final Spell OCTUPLE_SPELL = register(new MulticastSpell.MulticastSpellBuilder(73, "octuple_spell")
            .addManaDrain(30)
            .addDescription("Simultaneously casts 8 spells")
            .setNumberSpellsToCast(8)
            .setCustomModel(12)
            .build());

    public static final Spell TUPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(74, "tuple_scatter_spell")
            .addManaDrain(1)
            .addDescription("Simultaneously casts 2 spells with low accuracy")
            .setNumberSpellsToCast(2)
            .setCustomModel(13)
            .setIncreaseSpread(10)
            .build());

    public static final Spell TRIPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(75, "triple_scatter_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 3 spells with low accuracy")
            .setNumberSpellsToCast(3)
            .setCustomModel(14)
            .setIncreaseSpread(20)
            .build());

    public static final Spell QUADRUPLE_SCATTER_SPELL = register(new MulticastSpell.MulticastSpellBuilder(76, "quadruple_scatter_spell")
            .addManaDrain(2)
            .addDescription("Simultaneously casts 4 spells with low accuracy")
            .setNumberSpellsToCast(4)
            .setCustomModel(15)
            .setIncreaseSpread(40)
            .build());

    public static final Spell BEHIND_YOUR_BACK = register(new MulticastSpell.MulticastSpellBuilder(77, "behind_your_back")
            .addManaDrain(1)
            .addDescription("Casts two spells: one ahead of and one behind the caster")
            .setFormation(MulticastSpell.Formation.BEHIND_BACK)
            .setCustomModel(16)
            .build());

    public static final Spell ABOVE_AND_BELOW = register(new MulticastSpell.MulticastSpellBuilder(78, "above_and_below")
            .addManaDrain(3)
            .addDescription("Casts 3 spells - ahead, above and below the caster")
            .setFormation(MulticastSpell.Formation.ABOVE_AND_BELOW)
            .setCustomModel(17)
            .build());

    public static final Spell PENTAGON = register(new MulticastSpell.MulticastSpellBuilder(79, "pentagon")
            .addManaDrain(5)
            .addDescription("Casts 5 spells in a pentagonal pattern")
            .setFormation(MulticastSpell.Formation.PENTAGON)
            .setCustomModel(18)
            .build());

    public static final Spell HEXAGON = register(new MulticastSpell.MulticastSpellBuilder(710, "hexagon")
            .addManaDrain(6)
            .addDescription("Casts 6 spells in a hexagonal pattern")
            .setFormation(MulticastSpell.Formation.HEXAGON)
            .setCustomModel(19)
            .build());

    public static final Spell BIFURCATED = register(new MulticastSpell.MulticastSpellBuilder(711, "bifurcated")
            .addManaDrain(2)
            .addDescription("Casts 2 spells in a bifurcated pattern")
            .setFormation(MulticastSpell.Formation.BIFURCATED)
            .setCustomModel(20)
            .build());

    public static final Spell TRIFURCATED = register(new MulticastSpell.MulticastSpellBuilder(712, "trifurcated")
            .addManaDrain(3)
            .addDescription("Casts 3 spells in a trifurcated pattern")
            .setFormation(MulticastSpell.Formation.TRIFURCATED)
            .setCustomModel(21)
            .build());

    // ----- OTHER SPELLS ----- ID: 8+XXX

    public static final Spell OCARINA_A = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(81, "ocarina_note_a")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.A), false))
            //.addExecuteOnCast(new SoundSpellModule(Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.PLAYERS, false).setPitch((float)Math.pow(2, 3f/12f)))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(46)
            .build());

    public static final Spell OCARINA_B = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(82, "ocarina_note_b")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.B), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(47)
            .build());

    public static final Spell OCARINA_C = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(83, "ocarina_note_c")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.C), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(48)
            .build());

    public static final Spell OCARINA_D = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(84, "ocarina_note_d")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(49)
            .build());

    public static final Spell OCARINA_E = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(85, "ocarina_note_e")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.E), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(50)
            .build());

    public static final Spell OCARINA_F = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(86, "ocarina_note_f")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(0, Note.Tone.F), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(51)
            .build());

    public static final Spell OCARINA_G_SHARP = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(87, "ocarina_note_g#")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.sharp(0, Note.Tone.G), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(52)
            .build());

    public static final Spell OCARINA_A_TWO = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(88, "ocarina_note_a2")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.FLUTE, Note.flat(1, Note.Tone.A), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(53)
            .build());

    public static final Spell KANTELE_A = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(89, "kantele_note_a")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.A), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(54)
            .build());

    public static final Spell KANTELE_D = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(810, "kantele_note_d")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(55)
            .build());

    public static final Spell KANTELE_D_SHARP = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(811, "kantele_note_d#")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.sharp(1, Note.Tone.D), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(56)
            .build());

    public static final Spell KANTELE_E = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(812, "kantele_note_e")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.E), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(57)
            .build());

    public static final Spell KANTELE_G = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(813, "kantele_note_g")
            .addManaDrain(1).setLifetime(2).addCastDelay(.25)
            .overrideSpellType(Spell.SpellType.OTHER)
            .addExecuteOnCast(new SoundSpellModule(Instrument.GUITAR, Note.flat(1, Note.Tone.G), false))
            .addExecuteOnCast(new ParticleModule(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.NOTE).build())
                    .build(), false))
            .addDescription("Music for your ears!")
            .setCustomModel(58)
            .build());

    private static <T extends Spell> T register(final T spell){
        INBUILT_SPELLS.put(spell.getId(), spell);
        return spell;
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

        if (meta.getPersistentDataContainer().has(spellKey, PersistentDataType.INTEGER)){
            int spellId = meta.getPersistentDataContainer().get(spellKey, PersistentDataType.INTEGER);
            ISpell spell = getSpell(spellId);
            spell.setCount(stack.getAmount());

            return spell;
        } else {
            return null;
        }
    }

    public SpellManager(CobaltMagick cobaltMagick) {
        super(cobaltMagick);
        INSTANCE = this;
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }
}
