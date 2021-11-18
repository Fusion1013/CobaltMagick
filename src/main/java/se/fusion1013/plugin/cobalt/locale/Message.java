package se.fusion1013.plugin.cobalt.locale;

import org.bukkit.command.CommandSender;
import se.fusion1013.plugin.cobalt.Cobalt;
import se.fusion1013.plugin.cobalt.manager.LocaleManager;

@Deprecated
public enum Message {

    // Errors
    COMMAND_NOT_IMPLEMENTED("commands.error.not_implemented"),
    COMMAND_WRONG_SENDER_TYPE("commands.error.wrong_sender_type"),
    COMMAND_NO_PERMISSION("commands.error.no_permission"),
    COMMAND_INCORRECT_SYNTAX("commands.error.incorrect_syntax"),
    COMMAND_WARP_CREATE_ERROR_INVALID_NAME("commands.warp.create.error.invalid_name"),
    COMMAND_WARP_CREATE_ERROR_NAME_ALREADY_EXISTS("commands.warp.create.error.name_already_exists");

    private String code;

    Message(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public void send(CommandSender sender){
        LocaleManager manager = Cobalt.getInstance().getManager(LocaleManager.class);
        manager.sendMessage(sender, code);
    }
}
