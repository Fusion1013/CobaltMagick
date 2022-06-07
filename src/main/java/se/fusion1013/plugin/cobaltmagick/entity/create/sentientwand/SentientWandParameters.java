package se.fusion1013.plugin.cobaltmagick.entity.create.sentientwand;

import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltmagick.entity.modules.ability.CasterAbility;

public record SentientWandParameters(CasterAbility ability) implements ISpawnParameters {
}
