package se.fusion1013.cobaltmagick.components.actions;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.fusion1013.cobaltCore.components.actions.AbstractAction;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;

import java.util.List;
import java.util.Map;

public class ElementalVeinInfoAction extends AbstractAction {

    private final StringVariable veinName = new StringVariable("vein");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(veinName);
    }

    @Override
    public void execute(Map<String, Object> context) {
        LivingEntity entity = getTargetLivingEntity(context);

        IElementalVein vein = ElementalVeinManager.getElementalVein(veinName.getValue());
        if (vein == null) return;

        if (entity instanceof Player player) {
            ItemStack itemStack = (ItemStack) context.get("default_itemstack");
            ItemMeta meta = itemStack.getItemMeta();

            ParametricSpline2D.Result result = vein.getClosestPoint(player.getX(), player.getZ());
            double distance = result.distance();

            if (distance > 150) {
                meta.setItemModel(new NamespacedKey(Key.MINECRAFT_NAMESPACE, "red_wool"));
            } else if (distance > 100) {
                meta.setItemModel(new NamespacedKey(Key.MINECRAFT_NAMESPACE, "orange_wool"));
            } else if (distance > 50) {
                meta.setItemModel(new NamespacedKey(Key.MINECRAFT_NAMESPACE, "yellow_wool"));
            } else if (distance > 25) {
                meta.setItemModel(new NamespacedKey(Key.MINECRAFT_NAMESPACE, "lime_wool"));
            } else {
                meta.setItemModel(new NamespacedKey(Key.MINECRAFT_NAMESPACE, "green_wool"));
            }

            itemStack.setItemMeta(meta);

            // player.sendMessage("Distance: " + result.distance());
        }
    }

    @Override
    public String getId() {
        return "elemental_vein_info";
    }
}
