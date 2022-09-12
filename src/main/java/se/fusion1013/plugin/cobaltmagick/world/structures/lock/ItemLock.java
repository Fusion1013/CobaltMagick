package se.fusion1013.plugin.cobaltmagick.world.structures.lock;

import com.google.gson.JsonObject;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItem;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltmagick.item.ItemManager;
import se.fusion1013.plugin.cobaltmagick.world.structures.system.IActivatable;

import java.util.*;

public class ItemLock extends AbstractLock {

    // ----- VARIABLES -----

    String item;

    // ----- CONSTRUCTORS -----

    public ItemLock() {}

    public ItemLock(Location location, String item, UUID[] activatables) {
        super(location, activatables);
        this.item = item;
    }

    public ItemLock(UUID uuid, Location location, String item, UUID[] activatables) {
        super(location, uuid, activatables);
        this.item = item;
    }

    // ----- LOGIC -----

    @Override
    public void onTrigger(Object... args) {
        Location location = (Location) args[0];
        Player player = (Player) args[1];
        if (!location.toBlockLocation().equals(super.location.toBlockLocation())) return;
        onClick(player);
    }

    public boolean onClick(Player p) {
        ItemStack pItem = p.getInventory().getItemInMainHand();
        String itemName = CustomItemManager.getItemName(pItem);

        if (itemName.equalsIgnoreCase(item)) {
            super.unlock();
            if (p.getGameMode() != GameMode.CREATIVE) p.getInventory().getItemInMainHand().setAmount(pItem.getAmount()-1);
        }
        return false;
    }

    // ----- JSON INTEGRATION METHODS -----

    @Override
    public JsonObject toJson() {
        JsonObject jo = super.toJson();
        jo.addProperty("item", item);
        return jo;
    }

    @Override
    public void fromJson(JsonObject jsonObject) {
        super.fromJson(jsonObject);
        item = jsonObject.get("item").getAsString();
    }

    // ----- COMMAND INTEGRATION METHODS -----

    @Override
    public void fromCommandArguments(Object[] objects) {
        super.fromCommandArguments(objects);
        this.item = (String) objects[1];
    }

    @Override
    public Argument<?>[] getCommandArguments() {
        List<Argument<?>> args = new ArrayList<>(List.of(super.getCommandArguments()));
        args.add(new StringArgument("item").replaceSuggestions(ArgumentSuggestions.strings(CustomItemManager.getItemNames())));
        return args.toArray(new Argument[0]);
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getObjectIdentifier() {
        return "item_lock";
    }

    @Override
    public void setValue(String key, Object value) {
        super.setValue(key, value);

        switch (key) {
            case "item" -> this.item = (String) value;
        }
    }

    @Override
    public List<String> getInfoStrings() {
        List<String> info = super.getInfoStrings();
        info.add("Item: " + item);
        return info;
    }

    public String getItem() {
        return item;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public ItemLock(ItemLock target) {
        super(target);

        this.item = target.item;
    }

    @Override
    public ItemLock clone() {
        return new ItemLock(this);
    }
}
