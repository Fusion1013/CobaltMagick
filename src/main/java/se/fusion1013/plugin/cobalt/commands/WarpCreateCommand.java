package se.fusion1013.plugin.cobalt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;
import se.fusion1013.plugin.cobalt.util.StringPlaceholders;
import se.fusion1013.plugin.cobalt.util.StringUtil;
import se.fusion1013.plugin.cobalt.util.Warp;

import java.util.List;

@CommandDeclaration(
        commandName = "create",
        aliases = {"set","add"},
        permission = "cobalt.warp.create",
        usage = "/warp create <name>",
        description = "Creates a new warp",
        minArgs = 1,
        maxArgs = 1,
        validSenders = SenderType.PLAYER,
        parentCommandName = "warp"
)
public class WarpCreateCommand extends SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args, CommandFlags flags) {
        LocaleManager localeManager = Cobalt.getInstance().getManager(LocaleManager.class);
        Player player = (Player)sender;
        List<Warp> currentWarps = Cobalt.getInstance().getRDatabase().getWarps();

        String name = args[0];
        StringPlaceholders namePlaceholder = new StringPlaceholders().builder()
                .addPlaceholder("name", name).build();

        // Check if the name is a alphanumerical word
        if (!StringUtil.isWord(name)){
            localeManager.sendMessage(sender, "commands.warp.create.error.invalid_name", namePlaceholder);
            return false;
        }

        // Check if warp with the same name already exists
        for (Warp warp : currentWarps){
            if (warp.getName().equalsIgnoreCase(name)){
                localeManager.sendMessage(sender, "commands.warp.create.error.name_already_exists", namePlaceholder);
                return false;
            }
        }

        // Create the warp and store it in the database
        Warp warp = new Warp(name, player.getUniqueId(), player.getLocation());
        Cobalt.getInstance().getRDatabase().insertWarp(warp);

        localeManager.sendMessage(sender, "commands.warp.create.info.created_warp", namePlaceholder);
        return true;
    }
}
