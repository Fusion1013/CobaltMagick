package se.fusion1013.plugin.cobaltmagick.world.structures.portal;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.advancement.MagickAdvancementManager;
import se.fusion1013.plugin.cobaltmagick.spells.ISpell;
import se.fusion1013.plugin.cobaltmagick.spells.SpellManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpellPortal extends AbstractMagickPortal implements Runnable {

    // ----- VARIABLES -----

    private BukkitTask task;
    private String activationSpell;

    private static final int tickDelay = 2;

    // ----- CONSTRUCTORS -----

    public SpellPortal(Location portalLocation, Location exitLocation, String spell) {
        super(portalLocation, exitLocation);
        this.activationSpell = spell;
        this.isActive = false;
    }

    public SpellPortal(Location portalLocation, Location exitLocation, String spell, UUID uuid) {
        super(portalLocation, exitLocation, uuid);
        this.activationSpell = spell;
        this.isActive = false;
    }

    // ----- PORTAL TICK -----

    @Override
    public void onTrigger(Object... args) {
        ISpell spell = (ISpell) args[0];
        Location location = spell.getCaster().getLocation();

        if (location.distanceSquared(portalLocation) > 10*10) return;
        if (SpellManager.getSpell(activationSpell).getId() == spell.getId()) super.isActive = true;

        MagickAdvancementManager advancementManager = CobaltCore.getInstance().getSafeManager(CobaltMagick.getInstance(), MagickAdvancementManager.class);
        if (advancementManager == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != portalLocation.getWorld()) continue;
            if (player.getLocation().distanceSquared(portalLocation) > 50 * 50) continue;

            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> advancementManager.grantAdvancement(player, "progression", "soul_egg"), 10);
        }
    }

    @Override
    public void run() {
        tickPortal();
    }

    // ----- LOADING / UNLOADING -----

    @Override
    public void onLoad() {
        super.onLoad();

        // TODO: Play portal emerge effects

        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltMagick.getInstance(), this, 20, tickDelay);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.task != null) this.task.cancel();
    }

    // ----- COMMAND INTEGRATION -----

    @Override
    public Argument<?>[] getCommandArguments() {
        List<Argument<?>> arguments = new ArrayList<>(List.of(super.getCommandArguments()));
        arguments.add(new StringArgument("spell").replaceSuggestions(ArgumentSuggestions.strings(SpellManager.getSpellNames())));
        return arguments.toArray(new Argument[0]);
    }

    // ----- JSON INTEGRATION -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = super.toJson();
        jo.addProperty("spell", activationSpell);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        super.fromJson(jsonObject);
        this.activationSpell = jsonObject.get("spell").getAsString();
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void setValue(String key, Object value) {
        super.setValue(key, value);

        switch (key) {
            case "spell" -> this.activationSpell = (String) value;
        }
    }

    @Override
    public String getObjectIdentifier() {
        return "spell_portal";
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public SpellPortal(SpellPortal target) {
        super(target);
        this.activationSpell = target.activationSpell;
        this.task = target.task;
        this.isActive = false;
    }

    @Override
    public SpellPortal clone() {
        return new SpellPortal(this);
    }
}
