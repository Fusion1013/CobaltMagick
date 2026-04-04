package se.fusion1013.cobaltmagick.wand.cast;

import se.fusion1013.cobaltCore.components.actions.IAction;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.spell.SpellTrigger;
import se.fusion1013.cobaltmagick.spell.projectile.IProjectileTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShotState {

    private final List<IProjectileTemplate> projectileTemplates = new ArrayList<>();
    private final Map<ShotProperty<?>, Object> properties = new HashMap<>();
    private final Map<SpellTrigger, List<IAction>> actions = new HashMap<>();

    public void addActions(SpellTrigger trigger, List<IAction> actions) {
        List<IAction> current = this.actions.computeIfAbsent(trigger, (k) -> new ArrayList<>());
        current.addAll(actions);
    }

    public List<IAction> getActions(SpellTrigger trigger) {
        return this.actions.getOrDefault(trigger, new ArrayList<>());
    }

    public boolean hasProperty(ShotProperty property) {
        return properties.containsKey(property);
    }

    public <T> void setProperty(ShotProperty<T> property, T value) {
        properties.put(property, value);
    }

    public <T> T getProperty(ShotProperty<T> property) {
        //noinspection unchecked
        return (T) properties.get(property);
    }

    public <T> T getPropertyOrDefault(ShotProperty<T> property, T defaultValue) {
        return properties.containsKey(property) ? getProperty(property) : defaultValue;
    }

    public <T> void add(ShotProperty<T> property, T value) {
        T currentValue = getProperty(property);
        T newValue = property.add(currentValue, value);
        setProperty(property, newValue);
    }

    public void addProjectile(IProjectileTemplate template) {
        projectileTemplates.add(template);
    }

    public List<IProjectileTemplate> getProjectileTemplates() {
        return projectileTemplates;
    }

    public void log(RuleLogger ruleLogger) {
        ruleLogger.evaluate("ShotState", () -> {
            for (ShotProperty<?> property : properties.keySet()) {
                Object value = properties.get(property);
                ruleLogger.logMessage(property.getDisplayName() + ": " + value);
            }
        });
    }

    public ShotState() {
    }

    public ShotState(ShotState clone) {
        projectileTemplates.addAll(clone.projectileTemplates);
        properties.putAll(clone.properties);
        actions.putAll(clone.actions);
    }

}
