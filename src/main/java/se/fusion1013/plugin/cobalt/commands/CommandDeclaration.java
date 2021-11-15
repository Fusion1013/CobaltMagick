package se.fusion1013.plugin.cobalt.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets properties for the annotated <code>CobaltCommand</code>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDeclaration {

    String commandName();
    String[] aliases() default{};
    String permission() default "";
    String usage() default "";
    String description() default "";
    int minArgs() default 0;
    int maxArgs() default Integer.MAX_VALUE;
    SenderType[] validSenders() default { SenderType.PLAYER, SenderType.CONSOLE, SenderType.BLOCK, SenderType.UNKNOWN };
    boolean parseCommandFlags() default false;
    boolean executeIfInvalidSubCommand() default false;
    String parentCommandName() default "";
}
