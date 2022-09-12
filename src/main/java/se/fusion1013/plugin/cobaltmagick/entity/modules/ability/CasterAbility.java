package se.fusion1013.plugin.cobaltmagick.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.AbilityModule;
import se.fusion1013.plugin.cobaltcore.entity.modules.ability.IAbilityModule;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.util.AIUtil;
import se.fusion1013.plugin.cobaltmagick.wand.CastParser;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CasterAbility extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    ISpell[] spells;
    private final double maxTargetDistance;
    private Vector offset = new Vector(0, 2, 0);

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>CasterAbility</code>.
     *
     * @param cooldown the cooldown in seconds between each spellcast.
     * @param maxTargetDistance the max distance a target can be from the casting entity.
     * @param spells the spells to cast on each spellcast.
     */
    public CasterAbility(double cooldown, double maxTargetDistance, ISpell... spells) {
        super(cooldown); // Set initial cooldown of the spellcast

        this.spells = spells;
        this.maxTargetDistance = maxTargetDistance;
    }

    // ----- SETTERS -----

    public CasterAbility setOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    // ----- EXECUTE METHODS -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        // Get a target entity
        Entity summonedEntity = customEntity.getSummonedEntity();
        LivingEntity targetEntity = AIUtil.findNearbyPlayerHealthWeighted(summonedEntity, maxTargetDistance);
        Location castLocation = summonedEntity.getLocation().clone().add(offset);

        // If the target is not null, calculate the direction towards it and cast the spells
        if (targetEntity != null) {
            Location target = targetEntity.getLocation();
            Vector delta = new Vector(target.getX() - castLocation.getX(), target.getY() - castLocation.getY() + 1, target.getZ() - castLocation.getZ()).normalize();
            if (summonedEntity instanceof LivingEntity living) {
                // Cast Spells
                CastParser parser = new CastParser(living, -1, Arrays.asList(spells), 1);
                List<ISpell> spellsToCast = parser.prepareCast();
                for (ISpell spell : spellsToCast) {
                    spell.clone().castSpell(null, living, delta, castLocation); // TODO: Make sure that setting wand to null does not cause problems
                }
                for (ISpell spell : spells) spell.setHasCast(false);
            }
        }
    }

    @Override
    public boolean attemptAbility(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        if (!super.attemptAbility(customEntity, spawnParameters)) return false;
        execute(customEntity, spawnParameters);
        return true;
    }

    @Override
    public String getAbilityName() {
        return "Caster";
    }

    @Override
    public String getAbilityDescription() {
        return "Allows the entity to cast spells";
    }

    // ----- CLONE -----

    public CasterAbility(CasterAbility target) {
        super(target);
        this.spells = target.spells;
        this.offset = target.offset;
        this.maxTargetDistance = target.maxTargetDistance;
    }

    @Override
    public CasterAbility clone() {
        return new CasterAbility(this);
    }
}
