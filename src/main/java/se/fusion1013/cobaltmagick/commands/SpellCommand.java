package se.fusion1013.cobaltmagick.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.util.CommandUtil;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.ISpellTemplate;
import se.fusion1013.cobaltmagick.spell.SpellManager;
import se.fusion1013.cobaltmagick.spell.projectile.SpellProjectile;
import se.fusion1013.cobaltmagick.wand.cast.ShotProperty;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

public class SpellCommand {

    public static void register() {
        new CommandAPICommand("spell")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "get"))
                .withSubcommand(giveCommand())
                .withSubcommand(summonCommand())
                .register();
    }

    private static CommandAPICommand summonCommand() {
        return new CommandAPICommand("summon")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "spell.summon"))
                .withArguments(new LocationArgument("location"))
                .withArguments(new DoubleArgument("x"))
                .withArguments(new DoubleArgument("y"))
                .withArguments(new DoubleArgument("z"))
                .withArguments(new StringArgument("projectile_template").replaceSuggestions(ArgumentSuggestions.strings(k -> SpellManager.getProjectileTemplateNames())))
                .executesPlayer(SpellCommand::summonSpell);
    }

    private static void summonSpell(Player player, CommandArguments args) {
        Location location = (Location) args.get("location");
        double xVelocity = (double) args.get("x");
        double yVelocity = (double) args.get("y");
        double zVelocity = (double) args.get("z");
        String projectileTemplate = (String) args.get("projectile_template");

        ShotState shotState = new ShotState();
        shotState.setProperty(ShotProperty.IS_BOUNCY, true);
        shotState.setProperty(ShotProperty.HAS_GRAVITY, true);

        SpellProjectile projectile = new SpellProjectile(player, location, new Vector(xVelocity, yVelocity, zVelocity), SpellManager.getProjectileTemplate(projectileTemplate), shotState);
        SpellManager.getInstance().createSpell(projectile);
    }

    private static CommandAPICommand giveCommand() {
        return new CommandAPICommand("get")
                .withPermission(CommandUtil.getPermissionString(CobaltMagick.getInstance(), "spell.get"))
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(k -> SpellManager.getSpellNames())))
                .executesPlayer(SpellCommand::giveSpell);
    }

    private static void giveSpell(Player player, CommandArguments commandArguments) {
        String spellId = (String) commandArguments.get("id");
        ISpellTemplate template = SpellManager.getSpellTemplate(spellId);
        ItemStack itemStack = template.getItemStack();
        player.getInventory().addItem(itemStack);
    }

}
