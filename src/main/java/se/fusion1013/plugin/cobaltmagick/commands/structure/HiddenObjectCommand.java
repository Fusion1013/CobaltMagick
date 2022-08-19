package se.fusion1013.plugin.cobaltmagick.commands.structure;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;
import se.fusion1013.plugin.cobaltmagick.world.WorldManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.hidden.RevealMethod;

import java.util.UUID;

public class HiddenObjectCommand {

    public static CommandAPICommand createHiddenObjectCommand() {
        return new CommandAPICommand("hidden_object")
                .withPermission("cobalt.magick.commands.structure.hidden_object")
                .withSubcommand(createPlaceCommand())
                .withSubcommand(createRemoveCommand())
                .withSubcommand(createSetCommand());
    }

    // ----- SET COMMAND -----

    private static CommandAPICommand createSetCommand() {
        return new CommandAPICommand("set")
                .withPermission("cobalt.magick.commands.structure.hidden_object.set")
                .withSubcommand(createSetParticleGroupCommand())
                .withSubcommand(createSetItemDropCommand())
                .withSubcommand(createSetWandDrop())
                .withSubcommand(createSetDeleteOnActivation());
    }

    // ----- SET DELETE ON ACTIVATION -----

    private static CommandAPICommand createSetDeleteOnActivation() {
        return new CommandAPICommand("delete_on_activation")
                .withPermission("cobalt.magick.commands.structure.hidden_object.set.delete_on_activation")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getHiddenObjectIdentifiers())))
                .withArguments(new BooleanArgument("delete"))
                .executes(HiddenObjectCommand::setDeleteOnActivation);
    }

    private static void setDeleteOnActivation(CommandSender sender, Object[] args) {
        UUID uuid = UUID.fromString((String) args[0]);
        boolean delete = (boolean) args[1];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", uuid.toString())
                .addPlaceholder("property", "Delete on Activaton")
                .build();

        WorldManager.setHiddenObjectDeleteOnActivation(uuid, delete);

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.set", placeholders);
        }
    }

    // ----- SET WAND DROP -----

    private static CommandAPICommand createSetWandDrop() {
        return new CommandAPICommand("wand_drop")
                .withPermission("cobalt.magick.commands.structure.hidden_object.set.wand_drop")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getHiddenObjectIdentifiers())))
                .withArguments(new IntegerArgument("wand_tier", 0))
                .executes(HiddenObjectCommand::setWandDrop);
    }

    private static void setWandDrop(CommandSender sender, Object[] args) {
        UUID uuid = UUID.fromString((String) args[0]);
        int wandTier = (int) args[1];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", uuid.toString())
                .addPlaceholder("property", "Wand Drop")
                .build();

        WorldManager.setHiddenObjectWandSpawn(uuid, wandTier);

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.set", placeholders);
        }
    }

    // ----- SET ITEM DROP -----

    private static CommandAPICommand createSetItemDropCommand() {
        return new CommandAPICommand("item_drop")
                .withPermission("cobalt.magick.commands.structure.hidden_object.set.item_drop")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getHiddenObjectIdentifiers())))
                .withArguments(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(info -> CustomItemManager.getItemNames())))
                .executes(HiddenObjectCommand::setItemDrop);
    }

    private static void setItemDrop(CommandSender sender, Object[] args) {
        UUID uuid = UUID.fromString((String) args[0]);
        String itemName = (String) args[1];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", uuid.toString())
                .addPlaceholder("property", "Item Drop")
                .build();

        WorldManager.setHiddenObjectItemSpawn(uuid, itemName);

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.set", placeholders);
        }
    }

    // ----- SET PARTICLE GROUP -----

    private static CommandAPICommand createSetParticleGroupCommand() {
        return new CommandAPICommand("particle_group")
                .withPermission("cobalt.magick.commands.structure.hidden_object.set.particle_group")
                .withArguments(new StringArgument("uuid").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getHiddenObjectIdentifiers())))
                .withArguments(new StringArgument("group_name").replaceSuggestions(ArgumentSuggestions.strings(info -> ParticleGroupManager.getParticleGroupNames())))
                .executes(HiddenObjectCommand::setParticleGroup);
    }

    private static void setParticleGroup(CommandSender sender, Object[] args) {
        UUID uuid = UUID.fromString((String) args[0]);
        String groupName = (String) args[1];
        ParticleGroup group = ParticleGroupManager.getParticleGroup(groupName);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("uuid", uuid.toString())
                .addPlaceholder("property", "Particle Group")
                .build();

        if (group == null) {
            // TODO: Locale
            return;
        }

        WorldManager.setHiddenObjectParticleGroup(uuid, group);

        if (sender instanceof Player player) {
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.set", placeholders);
        }
    }

    // ----- SET ITEM -----

    // ----- SET WAND -----

    // ----- SET DELETE ON ACTIVATION -----

    // ----- REMOVE COMMAND -----

    private static CommandAPICommand createRemoveCommand() {
        return new CommandAPICommand("remove")
                .withPermission("cobalt.magick.commands.structure.hidden_object.remove")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getHiddenObjectIdentifiers())))
                .executes(HiddenObjectCommand::removeHiddenObject);
    }

    private static void removeHiddenObject(CommandSender sender, Object[] args) {
        UUID id = UUID.fromString((String) args[0]);
        WorldManager.removeHiddenObject(id);

        if (sender instanceof Player player) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("uuid", id).build();
            LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.remove", placeholders);
        }
    }

    // ----- PLACE COMMAND ----

    private static CommandAPICommand createPlaceCommand() {
        return new CommandAPICommand("place")
                .withPermission("cobalt.magick.commands.structure.hidden_object.place")
                .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                .withArguments(new StringArgument("reveal_method").replaceSuggestions(ArgumentSuggestions.strings(info -> WorldManager.getRevealMethods())))
                .executes(HiddenObjectCommand::placeHiddenObject);
    }

    private static void placeHiddenObject(CommandSender sender, Object[] args) {
        Location location = (Location) args[0];
        String revealMethodName = (String) args[1];

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("location", location)
                .addPlaceholder("reveal_method", revealMethodName).build();

        RevealMethod revealMethod = EnumUtils.findEnumInsensitiveCase(RevealMethod.class, revealMethodName);

        if (revealMethod == null) {
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.reveal_method_not_found", placeholders);
            return;
        }

        UUID id = WorldManager.createHiddenObject(location, revealMethod);
        placeholders.addPlaceholder("uuid", id);

        if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltMagick.getInstance(), player, "commands.magick.structure.hidden_object.place", placeholders);
    }

}
