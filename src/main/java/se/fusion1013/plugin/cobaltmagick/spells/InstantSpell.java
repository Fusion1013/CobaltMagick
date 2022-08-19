package se.fusion1013.plugin.cobaltmagick.spells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.event.SpellCastEvent;
import se.fusion1013.plugin.cobaltmagick.wand.CastParser;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstantSpell extends Spell implements Cloneable {

    // ----- VARIABLES -----

    CastMethod method;
    List<ISpell> spellsToCast = new ArrayList<>();
    List<ProjectileModifierSpell> modifiers = new ArrayList<>();

    // ----- CONSTRUCTORS -----

    public InstantSpell(int id, String internalSpellName, String spellName) {
        super(id, internalSpellName, spellName, SpellType.OTHER);
    }

    // ----- CASTING -----

    @Override
    public void performPreCast(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        super.performPreCast(caster, wand, wandSpells, casts, spellPos);

        switch (method) {
            case ALPHA -> this.spellsToCast = executeAlpha(caster, wand, wandSpells, casts, spellPos);
            case GAMMA -> this.spellsToCast = executeGamma(caster, wand, wandSpells, casts, spellPos);
            case MU -> this.spellsToCast = executeMu(caster, wand, wandSpells, casts, spellPos);
            case OMEGA -> this.spellsToCast = executeOmega(caster, wand, wandSpells, casts, spellPos);
            case PHI -> this.spellsToCast = executePhi(caster, wand, wandSpells, casts, spellPos);
            case SIGMA -> this.spellsToCast = executeSigma(caster, wand, wandSpells, casts, spellPos);
            case TAU -> this.spellsToCast = executeTau(caster, wand, wandSpells, casts, spellPos);
            case ZETA -> this.spellsToCast = executeZeta(caster, wand, wandSpells, casts, spellPos);
            case RANDOM_ANY -> this.spellsToCast = executeRandomAny(caster, wand, wandSpells, casts, spellPos);
        }
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster) {
        for (ISpell s : spellsToCast){
            s.setCaster(caster);
            SpellCastEvent event = new SpellCastEvent(s);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                s.castSpell(wand, caster);
            }
        }
    }

    @Override
    public void castSpell(Wand wand, LivingEntity caster, Vector direction, Location location) {
        for (ISpell s : spellsToCast){
            s.setCaster(caster);
            SpellCastEvent event = new SpellCastEvent(s);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                s.castSpell(wand, caster, direction, location);
            }
        }
    }

    // ----- METHODS -----

    // Random Spell Methods

    /**
     * Casts a random spell from all existing spells.
     *
     * @param caster the caster.
     * @param wand the wand.
     * @param wandSpells the spells in the wand.
     * @param casts the number of casts.
     * @param spellPos the spell position.
     * @return the spells to cast.
     */
    public List<ISpell> executeRandomAny(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> spellsToCast = new ArrayList<>();

        // Pick a random spell
        Random r = new Random();
        List<ISpell> allSpells = SpellManager.getAllSpells();
        if (allSpells.isEmpty()) return spellsToCast;
        ISpell randomSpell = allSpells.get(r.nextInt(0, allSpells.size()));

        // Add the random spell to the spell list in the position of the old spell, perform cast, then reset the spell
        ISpell thisSpell = wandSpells.get(spellPos).clone();
        thisSpell.setHasCast(true);
        wandSpells.set(spellPos, randomSpell);
        spellsToCast = new CastParser(caster, wand.getId(), wandSpells, 1, spellPos).addModifiers(modifiers).prepareCast();
        wandSpells.set(spellPos, thisSpell);

        return spellsToCast;
    }

    // Greek Letter Methods

    // TODO: Add modifiers to all methods

    // TODO: Modifiers & Multicast support
    public List<ISpell> executeAlpha(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> spellsToCast = new ArrayList<>();

        if (wandSpells.isEmpty()) return spellsToCast;

        ISpell spellToCast = wandSpells.get(0).clone();

        // Do not copy itself
        if (spellToCast instanceof InstantSpell instantSpell) if (instantSpell.method == CastMethod.ALPHA) return spellsToCast;

        // Add spell to cast list
        spellsToCast.add(spellToCast);
        return spellsToCast;
    }

    // TODO: Modifiers & Multicast support
    public List<ISpell> executeGamma(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> spellsToCast = new ArrayList<>();

        if (wandSpells.isEmpty()) return spellsToCast;

        ISpell spellToCast = wandSpells.get(wandSpells.size()-1).clone();

        // Do not copy itself
        if (spellToCast instanceof InstantSpell instantSpell) if (instantSpell.method == CastMethod.GAMMA) return spellsToCast;

        // Add spell to cast list
        spellsToCast.add(spellToCast);
        return spellsToCast;
    }

    public List<ISpell> executeMu(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> spellsToCast = new ArrayList<>();
        if (wandSpells.size() < spellPos+1) return spellsToCast;

        List<ProjectileModifierSpell> modifiers = new ArrayList<>();
        for (ISpell spell : wandSpells) {
            if (spell instanceof ProjectileModifierSpell projectileModifierSpell) {
                modifiers.add(projectileModifierSpell.clone());
            }
        }
        spellsToCast = new CastParser(caster, wand.getId(), wandSpells, 1, spellPos+1).addModifiers(modifiers).addModifiers(this.modifiers).prepareCast();

        return spellsToCast;
    }

    public List<ISpell> executeOmega(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        // Make a copy list of spells
        List<ISpell> cloneSpellList = new ArrayList<>();

        for (int i = 0; i < wandSpells.size(); i++) {
            if (i == spellPos) continue; // Do not copy the current omega

            // Get the spell at the current position and set it as not cast
            ISpell currentSpell = wandSpells.get(i).clone();
            currentSpell.setHasCast(false);

            cloneSpellList.add(currentSpell);
        }

        // Create a new cast parser with the same cast amount as the spell list size
        return new CastParser(caster, wand.getId(), cloneSpellList, cloneSpellList.size(), 0).addModifiers(modifiers).prepareCast();
    }

    public List<ISpell> executePhi(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> toCast = new ArrayList<>();

        for (ISpell spell : wandSpells) {
            if (spell instanceof ProjectileSpell) {
                ISpell spellClone = spell.clone();
                for (ProjectileModifierSpell modifierSpell : modifiers) modifierSpell.modifySpell(spellClone);
                toCast.add(spellClone);
            }
        }

        return toCast;
    }

    public List<ISpell> executeSigma(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> toCast = new ArrayList<>();

        for (ISpell spell : wandSpells) {
            if (spell instanceof StaticProjectileSpell) {
                ISpell spellClone = spell.clone();
                for (ProjectileModifierSpell modifierSpell : modifiers) modifierSpell.modifySpell(spellClone);
                toCast.add(spellClone);
            }
        }

        return toCast;
    }

    /**
     * Copies the following two spells in the wand.
     *
     * @param caster the caster.
     * @param wand the wand.
     * @param wandSpells the spells in the wand.
     * @param casts the number of casts.
     * @param spellPos the position of the spell.
     * @return the spells to cast.
     */
    public List<ISpell> executeTau(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> toCast = new ArrayList<>();

        for (ISpell spell : wandSpells) {
            toCast.add(spell.clone());
        }

        toCast = new CastParser(caster, wand.getId(), toCast, 2, spellPos+1).addModifiers(modifiers).prepareCast();

        return toCast;
    }

    public List<ISpell> executeZeta(LivingEntity caster, Wand wand, List<ISpell> wandSpells, int casts, int spellPos) {
        List<ISpell> toCast = new ArrayList<>();

        if (caster instanceof Player player) {
            PlayerInventory inventory = player.getInventory();

            // Loop through the inventory and find all wands (except for the one that zeta was cast from)
            List<ISpell> inventorySpells = new ArrayList<>();
            for (ItemStack item : inventory) {
                if (item == null) continue;
                Wand inventoryWand = Wand.getWand(item);

                // If the item is not a wand or the wand is the used wand, skip this one;
                if (inventoryWand == null) continue;

                if (inventoryWand.getId() == wand.getId()) continue;

                // Store all wand spells
                inventorySpells.addAll(inventoryWand.getSpells());
                inventorySpells.addAll(inventoryWand.getAlwaysCast());
            }

            // If no spells were found, return empty
            if (inventorySpells.isEmpty()) return new ArrayList<>();

            // Select a random spell and cast it
            Random r = new Random();
            ISpell spellToCast = inventorySpells.get(r.nextInt(0, inventorySpells.size())).clone();

            // Apply modifiers
            for (ProjectileModifierSpell projectileModifierSpell : modifiers) projectileModifierSpell.modifySpell(spellToCast);

            spellToCast.performPreCast(caster, wand, wandSpells, 1, spellPos);
            toCast.add(spellToCast);
        }

        return toCast;
    }

    // ----- BUILDER -----

    public static class InstantSpellBuilder extends Spell.SpellBuilder<InstantSpell, InstantSpellBuilder> {

        CastMethod method;

        /**
         * Creates a new spell builder with an internalized spell name. Automatically generates the display name
         * of the spell. The internal name should follow the format: "spark_bolt".
         *
         * @param id                id of the spell
         * @param internalSpellName internal name of the spell
         */
        public InstantSpellBuilder(int id, String internalSpellName, CastMethod method) {
            super(id, internalSpellName);
            this.method = method;
        }

        @Override
        protected InstantSpell createObj() {
            return new InstantSpell(id, internalSpellName, spellName);
        }

        @Override
        public InstantSpell build() {
            obj.setMethod(method);

            return super.build();
        }

        @Override
        protected InstantSpellBuilder getThis() {
            return this;
        }
    }

    // ----- GETTERS / SETTERS -----

    public void addModifier(ProjectileModifierSpell modifier) {
        this.modifiers.add(modifier);
    }

    public void setMethod(CastMethod method) {
        this.method = method;
    }

    @Override
    public Location getLocation() {
        return caster.getLocation().clone();
    }

    @Override
    public void cancelTask() {}

    // ----- ENUM -----

    public enum CastMethod {
        ALPHA,
        GAMMA,
        MU,
        OMEGA,
        PHI,
        SIGMA,
        TAU,
        ZETA,
        RANDOM_ANY
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public InstantSpell(InstantSpell target) {
        super(target);

        this.method = target.method;
        this.spellsToCast = target.spellsToCast;
        this.modifiers = target.modifiers;
    }

    @Override
    public InstantSpell clone() {
        return new InstantSpell(this);
    }
}
