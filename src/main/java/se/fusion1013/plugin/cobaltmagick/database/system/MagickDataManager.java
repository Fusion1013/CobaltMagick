package se.fusion1013.plugin.cobaltmagick.database.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.database.door.DoorDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.door.IDoorDao;
import se.fusion1013.plugin.cobaltmagick.database.lock.ILockDao;
import se.fusion1013.plugin.cobaltmagick.database.lock.LockDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.MusicBoxDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.wand.IWandDao;
import se.fusion1013.plugin.cobaltmagick.database.wand.WandDaoSQLite;

public class MagickDataManager extends Manager {

    // ----- CONSTRUCTORS -----

    public MagickDataManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        initDao();
    }

    @Override
    public void disable() {

    }

    private void initDao() {
        // SQLITE
        DataManager.getInstance().registerDao(new DoorDaoSQLite(), IDoorDao.class);
        DataManager.getInstance().registerDao(new LockDaoSQLite(), ILockDao.class);
        DataManager.getInstance().registerDao(new MusicBoxDaoSQLite(), IMusicBoxDao.class);
        DataManager.getInstance().registerDao(new WandDaoSQLite(), IWandDao.class);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static MagickDataManager INSTANCE = null;
    /**
     * Returns the object representing this <code>MagickDataManager</code>.
     *
     * @return The object of this class
     */
    public static MagickDataManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new MagickDataManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
