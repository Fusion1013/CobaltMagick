package se.fusion1013.cobaltmagick.alchemy.transmutation.repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import se.fusion1013.cobaltCore.database.system.DataStorageType;
import se.fusion1013.cobaltCore.database.system.implementations.SQLiteImplementation;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.alchemy.transmutation.entity.TransmutationEntity;
import se.fusion1013.cobaltmagick.alchemy.transmutation.mapper.TransmutationMapper;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.Transmutation;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TransmutationRepositoryImpl implements ITransmutationRepository {

    private static final Logger logger = CobaltMagick.getInstance().getLogger();
    private Dao<TransmutationEntity, Long> transmutationDao;

    @Override
    public void init() {
        try {
            ConnectionSource connectionSource = SQLiteImplementation.getConnectionSource();

            transmutationDao = DaoManager.createDao(connectionSource, TransmutationEntity.class);

            TableUtils.createTableIfNotExists(connectionSource, TransmutationEntity.class);

        } catch (SQLException e) {
            logger.severe("Error initializing Bounty DAO: " + e.getMessage());
        }
    }

    @Override
    public List<Transmutation> getTransmutations() {
        try {
            return TransmutationMapper.toModels(transmutationDao.queryForAll());
        } catch (SQLException e) {
            logger.severe("Error getting transmutations: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transmutation> getTransmutations(String catalyst) {
        try {
            return TransmutationMapper.toModels(transmutationDao.queryForEq("catalyst", catalyst));
        } catch (SQLException e) {
            logger.severe("Error getting transmutations: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transmutation> getTransmutations(String inputItem, String catalyst) {
        QueryBuilder<TransmutationEntity, Long> qb = transmutationDao.queryBuilder();
        try {
            qb.where()
                    .eq("input_item", inputItem)
                    .and()
                    .eq("catalyst", catalyst);
            return TransmutationMapper.toModels(qb.query());
        } catch (SQLException e) {
            logger.severe("Error getting transmutations: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transmutation> getTransmutationsFromInput(String inputItem) {
        try {
            return TransmutationMapper.toModels(transmutationDao.queryForEq("input_item", inputItem));
        } catch (SQLException e) {
            logger.severe("Error getting transmutations: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void addTransmutation(Transmutation transmutation) {
        try {
            transmutationDao.create(TransmutationMapper.toEntity(transmutation));
        } catch (SQLException e) {
            logger.severe("Error adding transmutation: " + e.getMessage());
        }
    }

    @Override
    public DataStorageType getDataStorageType() {
        return DataStorageType.SQLITE;
    }
}
