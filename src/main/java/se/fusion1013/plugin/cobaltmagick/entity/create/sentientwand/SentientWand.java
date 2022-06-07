package se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityParticleModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityPotionEffectModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityTickMethodModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ITickExecutable;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStylePoint;
import se.fusion1013.plugin.cobaltmagick.entity.modules.EntityKillTimerModule;
import se.fusion1013.plugin.cobaltmagick.entity.modules.EntityStandPassengerModule;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;
import se.fusion1013.plugin.cobaltmagick.manager.SpellManager;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;

public class SentientWand {

    public static ICustomEntity register() {

        Wand wand = new Wand(false, 1, 0, 4, 1000, 1000, 9, 0, new ArrayList<>(), 0);

        // Particles
        ParticleGroup tickGroup = new ParticleGroup("sentient_wand");
        tickGroup.addParticleStyle(new ParticleStylePoint.ParticleStylePointBuilder("sentient_wand_point")
                .setParticle(Particle.DUST_COLOR_TRANSITION)
                .setExtra(new Particle.DustTransition(Color.YELLOW, Color.WHITE, 1))
                .setCount(1)
                .setOffset(new Vector(.1, .3, .1))
                .setSpeed(.5)
                .build());

        // Default Caster Ability
        CasterAbility casterAbility = new CasterAbility(1.5, 32, SpellManager.getSpell(10)).setOffset(new Vector());

        ICustomEntity sentientWand = new CustomEntity.CustomEntityBuilder("sentient_wand", EntityType.BAT)

                .addExecuteOnTickModule(new EntityKillTimerModule(20*6))
                .addExecuteOnTickModule(new EntityStandPassengerModule(wand.getWandItem(), new Vector(0, -2, 0))) // TODO: Use model of the wand that gets passed to the entity
                .addExecuteOnTickModule(new EntityPotionEffectModule(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 1)))
                .addExecuteOnTickModule(new EntityTickMethodModule(((customEntity, spawnParameters) -> {
                    if (spawnParameters != null) {
                        SentientWandParameters parameters = (SentientWandParameters) spawnParameters;
                        parameters.ability().attemptAbility(customEntity, spawnParameters);
                    } else {
                        casterAbility.attemptAbility(customEntity, null);
                    }
                })))
                .addExecuteOnTickModule(new EntityParticleModule(tickGroup))

                .build();

        return CustomEntityManager.register(sentientWand);

    }
}
