package se.fusion1013.plugin.cobaltmagick.item.components;

import se.fusion1013.plugin.cobaltcore.item.components.IComponentFactory;
import se.fusion1013.plugin.cobaltcore.item.components.IComponentType;
import se.fusion1013.plugin.cobaltcore.item.components.IItemComponent;
import se.fusion1013.plugin.cobaltmagick.item.components.spell.ProjectileSpellComponent;

import java.util.Map;

public class MagickComponentFactory implements IComponentFactory {

    @Override
    public IItemComponent createComponent(IComponentType componentType, Map<?, ?> data, String owningItemName) {
        if (componentType == MagickComponentType.PROJECTILE_SPELL) return new ProjectileSpellComponent(owningItemName, data);
        if (componentType == MagickComponentType.ITEM_FRAME) return new ItemFrameComponent(owningItemName, data);

        return null;
    }

    @Override
    public IItemComponent createComponent(String componentType, Map<?, ?> data, String owningItemName) {
        if (componentType.equalsIgnoreCase("projectile_spell_component")) return new ProjectileSpellComponent(owningItemName, data);
        if (componentType.equalsIgnoreCase("item_frame_component")) return new ItemFrameComponent(owningItemName, data);

        return null;
    }
}
