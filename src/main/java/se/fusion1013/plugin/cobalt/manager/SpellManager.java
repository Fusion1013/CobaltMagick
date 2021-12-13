package se.fusion1013.plugin.cobalt.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.particle.ParticleGroup;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobalt.particle.styles.ParticleStyleSphere;
import se.fusion1013.plugin.cobalt.spells.*;
import se.fusion1013.plugin.cobalt.spells.spellmodules.AreaEffectModule;
import se.fusion1013.plugin.cobalt.spells.spellmodules.DamageModule;
import se.fusion1013.plugin.cobalt.spells.spellmodules.ExplodeModule;
import se.fusion1013.plugin.cobalt.spells.spellmodules.ReplaceBlocksModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellManager extends Manager {

    public static final Map<Integer, Spell> INBUILT_SPELLS = new HashMap<>();
    static NamespacedKey spellKey = new NamespacedKey(Cobalt.getInstance(), "spell");

    private static SpellManager INSTANCE = null;
    /**
     * Returns the object representing this <code>CommandManager</code>.
     *
     * @return The object of this class
     */
    public static SpellManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SpellManager(Cobalt.getInstance());
        }
        return INSTANCE;
    }

    // ----- PROJECTILE SPELLS ----- ID: 1+XXX

    public static final Spell SPARK_BOLT = register(new ProjectileSpell.ProjectileSpellBuilder(10, "spark_bolt")
            .addManaDrain(5).setRadius(.2).setVelocity(16).setLifetime(80).addCastDelay(0.05).setSpread(-1)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A weak but enchanting sparkling projectile")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPELL_WITCH).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(2)
            .build());

    public static final Spell BUBBLE_SPARK = register(new ProjectileSpell.ProjectileSpellBuilder(11, "bubble_spark")
            .addManaDrain(5).setRadius(.4).setSpread(22.9).setVelocity(5).setLifetime(100).addCastDelay(-0.08)
            .addExecuteOnEntityCollision(new DamageModule(5, true))
            .addDescription("A bouncy, inaccurate spell")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).build()
            ).build())
            .setCustomModel(3)
            .build());

    public static final Spell BOUNCING_BURST = register(new ProjectileSpell.ProjectileSpellBuilder(12, "bouncing_burst")
            .addManaDrain(5).setRadius(.4).setSpread(0.6).setVelocity(14).setLifetime(750).addCastDelay(-0.03).addGravity(3)
            .addExecuteOnEntityCollision(new DamageModule(3, true))
            .addExecuteOnEntityCollision(new ExplodeModule(1, true))
            .addDescription("A very bouncy projectile")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPELL).build()
            ).build())
            .setCustomModel(4)
            .build());

    public static final Spell FIREBOLT = register(new ProjectileSpell.ProjectileSpellBuilder(13, "firebolt")
            .addManaDrain(50).setRadius(.7).setSpread(2.9).setVelocity(5.3).setLifetime(500).addCastDelay(.5).addGravity(2)
            .addExecuteOnEntityCollision(new ExplodeModule(2, true).setsFire().destroysBlocks())
            .addExecuteOnDeath(new ExplodeModule(2, true).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(2, true).setsFire().destroysBlocks().onlyIfVelocityExceeds(.75))
            .addDescription("A bouncy, explosive bolt")
            .setIsBouncy(true)
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.FLAME).setCount(3).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .setCustomModel(5)
            .build());

    public static final Spell BURST_OF_AIR = register(new ProjectileSpell.ProjectileSpellBuilder(14, "burst_of_air")
            .addManaDrain(5).setRadius(.4).setVelocity(8).setLifetime(40).addCastDelay(.05).setSpread(-2)
            .addExecuteOnEntityCollision(new DamageModule(6, true).setKnockback(3))
            .addDescription("A brittle burst of air capable of greatly pushing objects")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.CLOUD).setCount(2).build()
            ).build())
            .setCustomModel(6)
            .build());

    public static final Spell SPARK_BOLT_WITH_TRIGGER = register(new ProjectileSpell.ProjectileSpellBuilder(15, "spark_bolt_with_trigger")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(80).addCastDelay(0.05)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPELL_WITCH).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.COLLISION)
            .setCustomModel(8)
            .build());

    public static final Spell SPARK_BOLT_WITH_DOUBLE_TRIGGER = register(new ProjectileSpell.ProjectileSpellBuilder(16, "spark_bolt_with_double_trigger")
            .addManaDrain(15).setRadius(.2).setVelocity(14).setLifetime(80).addCastDelay(0.07)
            .addExecuteOnEntityCollision(new DamageModule(4, true).setCriticalChance(5))
            .addExecuteOnBlockCollision(new ExplodeModule(1, true))
            .addExecuteOnEntityCollision(new ExplodeModule(1, true))
            .addDescription("A spark bolt that casts two new spells upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPELL_WITCH).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.COLLISION)
            .addTrigger(Spell.TriggerType.COLLISION)
            .setCustomModel(22)
            .build());

    public static final Spell SPARK_BOLT_WITH_TIMER = register(new ProjectileSpell.ProjectileSpellBuilder(17, "spark_bolt_with_timer")
            .addManaDrain(10).setRadius(.2).setVelocity(16).setLifetime(80).addCastDelay(0.05)
            .addExecuteOnEntityCollision(new DamageModule(3, true).setCriticalChance(5))
            .addDescription("A spark bolt that casts another spell upon collision")
            .setParticle(new ParticleGroup.ParticleGroupBuilder().addStyle(
                    new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SPELL_WITCH).setCount(5).setOffset(new Vector(.1, .1, .1)).build()
            ).build())
            .addTrigger(Spell.TriggerType.TIMER)
            .setCustomModel(23)
            .build());

    public static final Spell BLACK_HOLE = register(new ProjectileSpell.ProjectileSpellBuilder(18, "black_hole")
            .addManaDrain(180).setRadius(4).setVelocity(4).setLifetime(120).addCastDelay(1.33)
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
            .addManaDrain(0).setRadius(1).setVelocity(.01).setLifetime(2).addCastDelay(0.02).addRechargeTime(-0.17)
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
            .addManaDrain(200).setRadius(1).setVelocity(20).setLifetime(200).setSpread(.6).addCastDelay(.33).addRechargeTime(10).addGravity(.9)
            .addExecuteOnEntityCollision(new DamageModule(75, true))
            .addExecuteOnEntityCollision(new ReplaceBlocksModule(Material.AIR, 10, true).setReplaceNonAir().setNoSound())
            .addExecuteOnBlockCollision(new ReplaceBlocksModule(Material.AIR, 10, true).setReplaceNonAir().setNoSound())
            .addExecuteOnEntityCollision(new ExplodeModule(20, true).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(20, true).setsFire().destroysBlocks())
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
            .addManaDrain(500).setRadius(1).setVelocity(20).setLifetime(200).setSpread(.6).addCastDelay(.83).addRechargeTime(13.33).addGravity(.9)
            .addExecuteOnEntityCollision(new DamageModule(250, true))
            .addExecuteOnEntityCollision(new ReplaceBlocksModule(Material.AIR, 20, true).setReplaceNonAir().setNoSound())
            .addExecuteOnBlockCollision(new ReplaceBlocksModule(Material.AIR, 20, true).setReplaceNonAir().setNoSound())
            .addExecuteOnEntityCollision(new ExplodeModule(30, true).setsFire().destroysBlocks())
            .addExecuteOnBlockCollision(new ExplodeModule(30, true).setsFire().destroysBlocks())
            .addDescription("What do you expect?")
            .setParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.LAVA).setCount(20).setOffset(new Vector(.5, .5, .5)).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.FLAME).setRadius(.9).setDensity(40).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SMOKE_NORMAL).setRadius(.7).setDensity(20).build())
                    .addStyle(new ParticleStylePoint.ParticleStylePointBuilder().setParticle(Particle.SMOKE_LARGE).setCount(4).setOffset(new Vector(.2, .2, .2)).build())
                    .build())
            .setCustomModel(29)
            .build());


    // ----- STATIC PROJECTILE SPELLS ----- ID: 2+XXX

    public static final Spell CIRCLE_OF_BUOYANCY = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(20, "circle_of_buoyancy")
            .addManaDrain(10).addCastDelay(.25).setRadius(5).setLifetime(500)
            .addDescription("A field of levitative magic")
            .addExecuteOnTick(new AreaEffectModule(new PotionEffect(PotionEffectType.LEVITATION, 2, 0, false, false), 5, false))
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(150).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.TOWN_AURA).setRadius(5).setDensity(20).setInSphere().build())
                    .build())
            .setCustomModel(7)
            .build());

    public static final Spell GIGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(21, "giga_black_hole")
            .addManaDrain(240).setRadius(8).setLifetime(500).addCastDelay(1.33)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 8, false).setDropItems().setReplaceNonAir().setSlowReplace())
            .addDescription("A growing orb of negative energy that destroys everything in its reach")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(8).setDensity(150).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(8).setDensity(150).build())
                    .build())
            .setCustomModel(26)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .build());

    public static final Spell OMEGA_BLACK_HOLE = register(new StaticProjectileSpell.StaticProjectileSpellBuilder(22, "omega_black_hole")
            .addManaDrain(500).setRadius(20).setLifetime(1000).addCastDelay(1).addRechargeTime(1.67)
            .addExecuteOnTick(new ReplaceBlocksModule(Material.AIR, 20, false).setReplaceNonAir().setSlowReplace())
            .addDescription("Even light dies eventually...")
            .addParticle(new ParticleGroup.ParticleGroupBuilder()
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.SQUID_INK).setRadius(20).setDensity(300).build())
                    .addStyle(new ParticleStyleSphere.ParticleStyleSphereBuilder().setParticle(Particle.CRIT_MAGIC).setRadius(20).setDensity(300).build())
                    .build())
            .setCustomModel(27)
            .setCollidesWithBlocks(false).setCollidesWithEntities(false)
            .build());

    // ----- PASSIVE SPELLS ----- ID: 3+XXX

    // ----- UTILITY SPELLS ----- ID: 4+XXX

    // ----- PROJECTILE MODIFIER SPELLS ----- ID: 5+XXX

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
            .setFormation(MulticastSpell.Formation.PENTAGON)
            .setCustomModel(20)
            .build());

    public static final Spell TRIFURCATED = register(new MulticastSpell.MulticastSpellBuilder(712, "trifurcated")
            .addManaDrain(3)
            .addDescription("Casts 3 spells in a trifurcated pattern")
            .setFormation(MulticastSpell.Formation.TRIFURCATED)
            .setCustomModel(21)
            .build());

    // ----- OTHER SPELLS -----

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
            return getSpell(spellId);
        } else {
            return null;
        }
    }

    public SpellManager(Cobalt cobalt) {
        super(cobalt);
        INSTANCE = this;
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {

    }
}
