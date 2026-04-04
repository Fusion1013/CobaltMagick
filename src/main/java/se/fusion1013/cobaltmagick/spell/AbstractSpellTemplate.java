package se.fusion1013.cobaltmagick.spell;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.loader.IObjectProperty;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.spell.properties.SpellVisualProperties;

import java.util.List;

public abstract class AbstractSpellTemplate implements ISpellTemplate {

    private final String internalName;
    private final NamespacedKey spellKey;
    private final NamespacedKey itemKey;

    protected final List<IObjectProperty<SpellCreationContext, AbstractSpellTemplate>> properties = List.of(
            new SpellVisualProperties()
    );

    public AbstractSpellTemplate(String internalName) {
        this.internalName = internalName;
        this.spellKey = new NamespacedKey(CobaltMagick.getInstance(), "spell." + this.internalName);
        this.itemKey = new NamespacedKey(CobaltCore.getInstance(), internalName);
    }

    @Override
    public ItemStack getItemStack() {
        SpellCreationContext context = new SpellCreationContext(internalName, spellKey, itemKey);

        for (IObjectProperty<SpellCreationContext, AbstractSpellTemplate> property : properties) {
            property.create(context);
        }

        return context.finalizeItem();
    }

    protected abstract void loadParent(ConfigurationSection yaml);

    protected void loadInternalData(YamlConfiguration yamlConfiguration) {
        for (AbstractVariable variable : variables()) {
            variable.load(yamlConfiguration);
        }

        for (IObjectProperty<SpellCreationContext, AbstractSpellTemplate> property : properties) {
            property.fromYaml(yamlConfiguration, this);
        }

        loadParent(yamlConfiguration);
    }

    public static ISpellTemplate load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        SpellTemplate spell = new SpellTemplate(internalName);
        spell.loadInternalData(yaml);
        return spell;
    }

    public static ISpellTemplate load(JsonObject json) {
        throw new NotImplementedException();
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    protected abstract List<AbstractVariable> variables();
}
