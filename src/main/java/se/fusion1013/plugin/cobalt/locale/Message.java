package se.fusion1013.plugin.cobalt.locale;

public enum Message {

    // Errors
    COMMAND_NOT_IMPLEMENTED("commands.error.not_implemented"),
    COMMAND_WRONG_SENDER_TYPE("commands.error.wrong_sender_type"),
    COMMAND_NO_PERMISSION("commands.error.no_permission"),
    COMMAND_INCORRECT_SYNTAX("commands.error.incorrect_syntax");

    private String code;

    Message(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
