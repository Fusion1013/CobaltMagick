package se.fusion1013.cobaltmagick.spell;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.logger.RuleLogger;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltmagick.wand.cast.ShotState;

public interface ISpellTemplate extends IRegistryItem {

    ItemStack getItemStack();

    String getInternalName();

    int getDraws();

    int manaCost();

    void modify(ShotState shotState, RuleLogger logger);

}
