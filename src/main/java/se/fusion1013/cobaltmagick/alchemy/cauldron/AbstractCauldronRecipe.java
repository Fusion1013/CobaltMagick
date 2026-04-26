package se.fusion1013.cobaltmagick.alchemy.cauldron;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.variable.BooleanVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;

import java.util.Random;

public abstract class AbstractCauldronRecipe implements ICauldronRecipe, IElementalAffinity {

    protected static final Random random = new Random();

    protected final StringVariable internalName = new StringVariable("internal_name");
    protected final StringVariable elementalAffinities = new StringVariable("elemental_affinity");
    protected final BooleanVariable hasSpecificGlyph = new BooleanVariable("has_specific_glyph", true);

    public AbstractCauldronRecipe(YamlConfiguration yaml) {
        internalName.load(yaml);
        elementalAffinities.load(yaml);
        hasSpecificGlyph.load(yaml);
    }

    public AbstractCauldronRecipe(JsonObject json) {
        throw new NotImplementedException();
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    @Override
    public String[] getElementalAffinities() {
        return elementalAffinities.getValueList().toArray(new String[0]);
    }

    @Override
    public boolean hasSpecificGlyph() {
        return hasSpecificGlyph.getValue();
    }

    @Override
    public IElementalAffinity getElementalAffinity() {
        return this;
    }
}
