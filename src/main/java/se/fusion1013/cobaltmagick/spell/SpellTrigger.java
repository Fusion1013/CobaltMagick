package se.fusion1013.cobaltmagick.spell;

public enum SpellTrigger {

    OnHit("on_hit"),
    OnEntityHit("on_entity_hit"),
    OnBlockHit("on_block_hit"),
    OnTick("on_tick"),
    OnDeath("on_death");

    private final String internalName;

    SpellTrigger(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName;
    }
}
