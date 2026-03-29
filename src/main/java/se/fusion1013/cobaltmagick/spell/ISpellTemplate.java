package se.fusion1013.cobaltmagick.spell;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

public interface ISpellTemplate extends IRegistryItem {

    ItemStack getItemStack();

    String getInternalName();

    // TODO: ISpell getSpell();

}
