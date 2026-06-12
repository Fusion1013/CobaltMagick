package se.fusion1013.cobaltmagick.alchemy.transmutation.mapper;

import se.fusion1013.cobaltmagick.alchemy.transmutation.entity.TransmutationEntity;
import se.fusion1013.cobaltmagick.alchemy.transmutation.model.Transmutation;

import java.util.List;

public class TransmutationMapper {

    public static Transmutation toModel(TransmutationEntity entity) {
        Transmutation model = new Transmutation();

        model.setId(entity.getId());
        model.setInputItem(entity.getInputItem());
        model.setOutputItem(entity.getOutputItem());
        model.setCatalyst(entity.getCatalyst());
        model.setCost(entity.getCost());

        return model;
    }

    public static TransmutationEntity toEntity(Transmutation model) {
        TransmutationEntity entity = new TransmutationEntity();

        entity.setInputItem(model.getInputItem());
        entity.setOutputItem(model.getOutputItem());
        entity.setCatalyst(model.getCatalyst());
        entity.setCost(model.getCost());

        return entity;
    }

    public static List<Transmutation> toModels(List<TransmutationEntity> entities) {
        return entities.stream().map(TransmutationMapper::toModel).toList();
    }

}
