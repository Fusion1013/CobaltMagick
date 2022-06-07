package se.fusion1013.plugin.cobaltmagick.scene;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

public class SceneManager extends Manager implements CommandExecutor {

    public SceneManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- SCENES -----

    @CommandHandler(
            parameterNames = {"location"}
    )
    public static CommandResult anvil(Location location) {
        new CauldronEntryScene().play(location);
        return CommandResult.SUCCESS;
    }

    @Override
    public void reload() {
        CommandManager.getInstance().registerCommandModule("scene", this);
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static SceneManager INSTANCE = null;
    /**
     * Returns the object representing this <code>SceneManager</code>.
     *
     * @return The object of this class
     */
    public static SceneManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SceneManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
