package se.fusion1013.plugin.cobalt.spells;

import se.fusion1013.plugin.cobalt.wand.Wand;

public interface ISpell {
    void castSpell(Wand wand);
    void tick();

    // Spell attributes
    int getUses(); // Number of times a spell can be cast before it is depleted
    int getManaDrain(); // The mana drain of the spell on cast
    double getDamage(); // The amount of damage dealt by the spell // TODO: Implement different damage types
    double getRadius(); // The radius of the circular area in which the damage of the spell is dealt
    double getSpread(); // The range of deviation of a spell from the aimed direction, measured in degrees (Lower number => More accurate)
    double getSpeed(); // The rate at which the projectile travels
    double getLifetime(); // The duration of which the projectile remains active
    double getCastDelay(); // A modifier to the wands base cast delay, determining the delay before the next spell in the wand's queue is cast
    double getRechargeTime(); // A modifier to the wands base recharge time, triggered after the last spell in the wands queue is cast. The wand reloads all spells in the queue, ready to cast again
    double getSpreadModifier(); // A modifier to the spread of the wand. A negative spread modifier reduces the positive spread of a wand when applied to the spell
    double getSpeedModifier(); // A modifier to the speed of the affected projectile. Speed modifiers over 1 will increase projectile speed
    double getLifetimeModifier(); // A modifier to the lifetime of the affected projectile
    double getCriticalChance(); // The chance that a spell will critically strike, dealing 5 times the usual damage, and dealing even further damage if critical chance is above 100%

    boolean getHasCast(); // True if the spell has been cast. Resets when the wand recharges
    int getAddCasts(); // Increases the number of spells the wand can cast
}
