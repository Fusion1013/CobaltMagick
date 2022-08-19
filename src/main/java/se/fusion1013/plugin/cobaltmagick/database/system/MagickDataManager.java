package se.fusion1013.plugin.cobaltmagick.database.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltmagick.database.door.DoorDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.door.IDoorDao;
import se.fusion1013.plugin.cobaltmagick.database.hidden.HiddenObjectDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.hidden.IHiddenObjectDao;
import se.fusion1013.plugin.cobaltmagick.database.itemlock.IItemLockDao;
import se.fusion1013.plugin.cobaltmagick.database.itemlock.ItemLockDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.laser.ILaserDao;
import se.fusion1013.plugin.cobaltmagick.database.laser.LaserDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.IMusicBoxDao;
import se.fusion1013.plugin.cobaltmagick.database.musicbox.MusicBoxDaoSQLite;
import se.fusion1013.plugin.cobaltmagick.database.runelock.IRuneLockDao;
import se.fusion1013.plugin.cobaltmagick.database.runelock.RuneLockDaoSQLite;
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
        core.getManager(core, DataManager.class).registerDao(new DoorDaoSQLite(), IDoorDao.class);
        core.getManager(core, DataManager.class).registerDao(new ItemLockDaoSQLite(), IItemLockDao.class);
        core.getManager(core, DataManager.class).registerDao(new MusicBoxDaoSQLite(), IMusicBoxDao.class);
        core.getManager(core, DataManager.class).registerDao(new WandDaoSQLite(), IWandDao.class);
        core.getManager(core, DataManager.class).registerDao(new RuneLockDaoSQLite(), IRuneLockDao.class);
        core.getManager(core, DataManager.class).registerDao(new LaserDaoSQLite(), ILaserDao.class);
        core.getManager(core, DataManager.class).registerDao(new HiddenObjectDaoSQLite(), IHiddenObjectDao.class);
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
