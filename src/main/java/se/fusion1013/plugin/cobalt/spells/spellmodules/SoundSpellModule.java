package se.fusion1013.plugin.cobalt.spells.spellmodules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SoundSpellModule extends AbstractSpellModule<SoundSpellModule> implements SpellModule {

    // TODO: Add support to only play sound for specific player
    Sound sound;
    SoundCategory category;
    boolean cancelsCast;

    // Optional Variables
    float volume = 1;
    float pitch = 1;

    public SoundSpellModule(Sound sound, SoundCategory category, boolean cancelsCast){
        this.sound = sound;
        this.category = category;
        this.cancelsCast = cancelsCast;
    }

    public SoundSpellModule(SoundSpellModule target){
        this.sound = target.sound;
        this.category = target.category;
        this.cancelsCast = target.cancelsCast;

        this.volume = target.volume;
        this.pitch = target.pitch;
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
    public void executeOnCast(Location location, Vector velocityVector) {
        playSound(location);
    }

    @Override
    public void executeOnEntityHit(Location location, Vector velocityVector, Entity entityHit) {
        super.executeOnEntityHit(location, velocityVector, entityHit);
        if (!canRun) return;

        playSound(location);
    }

    @Override
    public void executeOnBlockHit(Location location, Vector velocityVector, Block blockHit, BlockFace hitBlockFace) {
        super.executeOnBlockHit(location, velocityVector, blockHit, hitBlockFace);
        if (!canRun) return;

        playSound(location);
    }

    @Override
    public void executeOnTick(Location location, Vector velocityVector) {
        if (!canRun) return;

        playSound(location);
    }

    @Override
    public void executeOnDeath(Location location, Vector velocityVector) {
        playSound(location);
    }

    private void playSound(Location location){
        for (Player p : Bukkit.getOnlinePlayers()){
            p.playSound(location, sound, category, volume, pitch);
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
