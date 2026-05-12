package se.fusion1013.cobaltmagick.alchemy.transmutation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltmagick.alchemy.cauldron.AbstractCauldronRecipe;
import se.fusion1013.cobaltmagick.alchemy.cauldron.CauldronState;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalAffinity;

import java.util.List;

public class TransmutationRecipe extends AbstractCauldronRecipe implements ITransmutationRecipe, IElementalAffinity {

    public TransmutationRecipe(YamlConfiguration yaml) {
        super(yaml);
    }

    public TransmutationRecipe(JsonObject json) {
        super(json);
        throw new NotImplementedException();
    }

    @Override
    public boolean validateFinalItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public String getGlyph() {
        return "";
    }

    @Override
    public boolean validateConditions(Location location) {
        return false;
    }

    @Override
    public boolean validateItems(List<ItemStack> items) {
        return false;
    }

    @Override
    public void execute(Location location, CauldronState state, int count, RuleLogger ruleLogger) {

    }

    @Override
    public boolean allowExternalBlocks() {
        return false;
    }

}
