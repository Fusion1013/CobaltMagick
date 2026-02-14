package se.fusion1013.cobaltmagick.spell;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.util.INameProvider;

public interface ISpellTemplate extends INameProvider {

    ItemStack getItemStack();

    String getInternalName();

    // TODO: ISpell getSpell();

}
