package se.fusion1013.plugin.cobaltmagick.entity;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.entity.create.*;
import se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand.SentientWand;

public class EntityManager extends Manager {

    // ----- CONSTRUCTORS -----

    public EntityManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- REGISTER -----

    public static final ICustomEntity ORC = Orc.register();
    public static final ICustomEntity ORC_BRUTE = OrcBrute.register();
    public static final ICustomEntity ORC_ARCHER = OrcArcher.register();
    public static final ICustomEntity ORC_FLAMESPITTER = OrcFlamespitter.register();
    public static final ICustomEntity SENTIENT_WAND = SentientWand.register();
    public static final ICustomEntity HIGH_ALCHEMIST = HighAlchemist.register();
    public static final ICustomEntity APPRENTICE = Apprentice.register();


    @Override
    public void reload() {
    }

    @Override
    public void disable() {
    }

    // ----- INSTANCE -----

    private static EntityManager INSTANCE = null;
    /**
     * Returns the object representing this <code>EntityManager</code>.
     *
     * @return The object of this class
     */
    public static EntityManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new EntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
