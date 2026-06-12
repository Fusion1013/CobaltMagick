package se.fusion1013.cobaltmagick.database;

import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.transmutation.repository.ITransmutationRepository;
import se.fusion1013.cobaltmagick.alchemy.transmutation.repository.TransmutationRepositoryImpl;

public class MagickDataManager extends Manager<CobaltMagick> {

    public MagickDataManager(CobaltMagick plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.registerDao(new TransmutationRepositoryImpl(), ITransmutationRepository.class);
    }

    @Override
    public void disable() {

    }
}
