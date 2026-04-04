package se.fusion1013.cobaltmagick.spell;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.ActionVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.spell.projectile.IProjectileTemplate;
import se.fusion1013.cobaltmagick.wand.cast.ShotProperty;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static se.fusion1013.cobaltmagick.spell.SpellTrigger.*;

public class SpellTemplate extends AbstractSpellTemplate {

    private final StringVariable projectile = new StringVariable("projectile");
    private final IntVariable draws = new IntVariable("draws", 0);

    private final Map<ShotProperty, Object> properties = new HashMap<>();

    private final ActionVariable onHitActions = new ActionVariable(OnHit.getInternalName());
    private final ActionVariable onEntityHitActions = new ActionVariable(OnEntityHit.getInternalName());
    private final ActionVariable onBlockHitActions = new ActionVariable(OnBlockHit.getInternalName());
    private final ActionVariable onTickActions = new ActionVariable(OnTick.getInternalName());
    private final ActionVariable onDeathActions = new ActionVariable(OnDeath.getInternalName());

    public SpellTemplate(String internalName) {
        super(internalName);
    }

    @Override
    protected void loadParent(ConfigurationSection yaml) {
        if (!yaml.contains("properties")) return;
        ConfigurationSection propertiesYaml = yaml.getConfigurationSection("properties");
        if (propertiesYaml == null) return;

        for (ShotProperty<?> property : ShotProperty.values) {
            if (!propertiesYaml.contains(property.getInternalName())) continue;

            Object value = propertiesYaml.get(property.getInternalName());
            properties.put(property, value);
        }
    }

    @Override
    protected List<AbstractVariable> variables() {
        return List.of(draws, projectile, onHitActions, onEntityHitActions, onBlockHitActions, onTickActions, onDeathActions);
    }

    @Override
    public int getDraws() {
        return draws.getValue();
    }

    @Override
    public int manaCost() {
        return (int) properties.getOrDefault(ShotProperty.MANA_COST, 0);
    }

    @Override
    public void modify(ShotState shotState, RuleLogger logger) {
        for (Map.Entry<ShotProperty, Object> entry : properties.entrySet()) {
            ShotProperty property = entry.getKey();
            shotState.add(property, entry.getValue());
            logger.logMessage("Applied property " + property.getInternalName() + " with value " + entry.getValue());
        }

        if (projectile.getValue() != null) {
            IProjectileTemplate template = SpellManager.getProjectileTemplate(projectile.getValue());
            shotState.addProjectile(template);
            logger.logMessage("Added projectile template " + template.getInternalName());
        }

        shotState.addActions(OnHit, onHitActions.getValueList());
        shotState.addActions(OnEntityHit, onEntityHitActions.getValueList());
        shotState.addActions(OnBlockHit, onBlockHitActions.getValueList());
        shotState.addActions(OnTick, onTickActions.getValueList());
        shotState.addActions(OnDeath, onDeathActions.getValueList());
    }
}
