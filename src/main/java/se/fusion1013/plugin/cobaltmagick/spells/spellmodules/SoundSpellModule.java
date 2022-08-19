package se.fusion1013.plugin.cobaltmagick.spells.spellmodules;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.MovableSpell;
import se.fusion1013.plugin.cobaltmagick.wand.Wand;

public class SoundSpellModule extends AbstractSpellModule<SoundSpellModule> implements SpellModule {

    // TODO: Add support to only play sound for specific player
    boolean cancelsCast;

    // Optional Variables
    boolean playSound = false;
    Sound sound;
    String soundString;
    SoundCategory category;
    float volume = 1;
    float pitch = 1;

    boolean playInstrument = false;
    Instrument instrument;
    Note note;

    private static final int MAX_PLAYER_DISTANCE = 120;

    public SoundSpellModule(String soundString, SoundCategory category, boolean cancelsCast){
        this.soundString = soundString;
        this.category = category;
        this.cancelsCast = cancelsCast;
        this.playSound = true;
    }

    public SoundSpellModule(Sound sound, SoundCategory category, boolean cancelsCast){
        this.sound = sound;
        this.category = category;
        this.cancelsCast = cancelsCast;
        this.playSound = true;
    }

    public SoundSpellModule(Instrument instrument, Note note, boolean cancelsCast){
        this.instrument = instrument;
        this.note = note;
        this.cancelsCast = cancelsCast;
        this.playInstrument = true;
    }

    public SoundSpellModule(SoundSpellModule target){
        this.sound = target.sound;
        this.soundString = target.soundString;
        this.category = target.category;
        this.cancelsCast = target.cancelsCast;

        this.volume = target.volume;
        this.pitch = target.pitch;

        this.note = target.note;
        this.instrument = target.instrument;

        this.playSound = target.playSound;
        this.playInstrument = target.playInstrument;
    }

    public SoundSpellModule setVolume(float volume) {
        this.volume = volume;
        return getThis();
    }

    public SoundSpellModule setPitch(float pitch){
        this.pitch = pitch;
        return getThis();
    }

    @Override
    public void executeOnCast(Wand wand, LivingEntity caster, ISpell spell) {
        playSound(spell.getLocation());
    }

    @Override
    public void executeOnEntityHit(Wand wand, LivingEntity caster, MovableSpell spell, Entity entityHit) {
        super.executeOnEntityHit(wand, caster, spell, entityHit);
        if (!canRun) return;

        playSound(spell.getLocation());
    }

    @Override
    public void executeOnBlockHit(Wand wand, LivingEntity caster, MovableSpell spell, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(wand, caster, spell, blockHit, hitBlockFace);
        if (!canRun) return;

        playSound(spell.getLocation());
    }

    @Override
    public void executeOnTick(Wand wand, LivingEntity caster, ISpell spell) {
        if (!canRun) return;

        playSound(spell.getLocation());
    }

    @Override
    public void executeOnDeath(Wand wand, LivingEntity caster, ISpell spell) {
        playSound(spell.getLocation());
    }

    private void playSound(Location location) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() != location.getWorld()) continue; // If the player is not in the same dimension as the spell, skip

            if (p.getLocation().distanceSquared(location) > MAX_PLAYER_DISTANCE*MAX_PLAYER_DISTANCE) continue;

            if (playInstrument) p.playNote(location, instrument, note);
            else if (playSound && sound != null) p.playSound(location, sound, category, volume, pitch);
            else if (playSound) p.playSound(location, soundString, category, volume, pitch);
        }
    }

    @Override
    public boolean cancelsCast() {
        return cancelsCast;
    }

    @Override
    public AbstractSpellModule<SoundSpellModule> clone() {
        return new SoundSpellModule(this);
    }

    @Override
    protected SoundSpellModule getThis() {
        return this;
    }
}
