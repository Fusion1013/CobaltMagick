package se.fusion1013.cobaltmagick.spell;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.loader.IObjectProperty;
import se.fusion1013.cobaltmagick.CobaltMagick;

import java.util.List;

public abstract class AbstractSpellTemplate implements ISpellTemplate {

    private final String internalName;
    private final NamespacedKey spellKey;
    private final NamespacedKey itemKey;

    protected final List<IObjectProperty<SpellCreationContext, AbstractSpellTemplate>> properties = List.of(

    );

    public AbstractSpellTemplate(String internalName) {
        this.internalName = internalName;
        this.spellKey = new NamespacedKey(CobaltMagick.getInstance(), "spell." + this.internalName);
        this.itemKey = new NamespacedKey(CobaltCore.getInstance(), internalName);
    }

    @Override
    public ItemStack getItemStack() {
        SpellCreationContext context = new SpellCreationContext(spellKey, itemKey);

        for (IObjectProperty<SpellCreationContext, AbstractSpellTemplate> property : properties) {
            property.create(context);
        }

        return context.finalizeItem();
    }

    protected void loadInternalData(YamlConfiguration yamlConfiguration) {
        for (IObjectProperty<SpellCreationContext, AbstractSpellTemplate> property : properties) {
            property.fromYaml(yamlConfiguration, this);
        }
    }

    public static ISpellTemplate load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        SpellTemplate spell = new SpellTemplate(internalName);
        spell.loadInternalData(yaml);
        return spell;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }
}
