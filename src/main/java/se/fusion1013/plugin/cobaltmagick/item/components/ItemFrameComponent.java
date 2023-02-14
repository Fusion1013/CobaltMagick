package se.fusion1013.plugin.cobaltmagick.item.components;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.hanging.HangingPlaceEvent;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;
import se.fusion1013.plugin.cobaltcore.item.components.AbstractItemComponent;

import java.util.Map;

public class ItemFrameComponent extends AbstractItemComponent {

    //region VARIABLES

    private boolean isInvisible;
    private boolean isGlowing;

    //endregion

    //region CONSTRUCTORS

    public ItemFrameComponent(String owningItem) {
        super(owningItem);
    }

    protected ItemFrameComponent(String owningItem, Map<?, ?> data) {
        super(owningItem, data);

        if (data.containsKey("is_invisible")) isInvisible = (boolean) data.get("is_invisible");
        if (data.containsKey("is_glowing")) isGlowing = (boolean) data.get("is_glowing");
    }

    //endregion

    //region EVENTS

    @Override
    public Map<ItemActivator, IItemActivatorExecutor> registerEvents() {
        Map<ItemActivator, IItemActivatorExecutor> events = super.registerEvents();
        events.put(ItemActivator.HANGING_PLACE, ((iCustomItem, event, equipmentSlot) -> {
            HangingPlaceEvent hangingPlaceEvent = (HangingPlaceEvent) event;
            ItemFrame itemFrame = (ItemFrame) hangingPlaceEvent.getEntity();
            itemFrame.setVisible(!isInvisible);
            itemFrame.setGlowing(isGlowing);
        }));
        return events;
    }

    //endregion

    //region GETTERS / SETTERS

    @Override
    public String getInternalName() {
        return "item_frame_components";
    }

    //endregion
}
