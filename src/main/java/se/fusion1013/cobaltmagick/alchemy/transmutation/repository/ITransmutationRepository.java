package se.fusion1013.cobaltmagick.alchemy.transmutation.repository;

import se.fusion1013.cobaltCore.database.system.IDao;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.Transmutation;

import java.util.List;

public interface ITransmutationRepository extends IDao {

    @Override
    default String getId() {
        return "transmutation";
    }

    List<Transmutation> getTransmutations();

    List<Transmutation> getTransmutations(String catalyst);

    List<Transmutation> getTransmutations(String inputItem, String catalyst);

    List<Transmutation> getTransmutationsFromInput(String inputItem);

    void addTransmutation(Transmutation transmutation);

}
