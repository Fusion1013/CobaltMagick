package se.fusion1013.cobaltmagick.enchantments.extract_elements;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ElementalVeinManager;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.IElementalVein;
import se.fusion1013.cobaltmagick.alchemy.elemental_veins.ParametricSpline2D;

import java.util.Collection;
import java.util.Random;

public class ExtractElementsUtil {

    private static final Random RANDOM = new Random();
    private static final NamespacedKey EXTRACT_ELEMENTS_ENCHANTMENT_KEY = new NamespacedKey("fusion1013", "extract_elements");
    private static final int BASE_DISTANCE = 8;
    private static final int DISTANCE_PER_LEVEL = 4;
    private static final double DROP_CHANCE = 0.05;

    public static void tick() {
        Bukkit.getOnlinePlayers().forEach(ExtractElementsUtil::tickPlayer);
    }

    private static void tickPlayer(Player player) {
        EntityEquipment equipment = player.getEquipment();

        ItemStack itemInMainHand = equipment.getItemInMainHand();
        if (itemInMainHand.isEmpty()) return;

        ItemMeta itemMeta = itemInMainHand.getItemMeta();

        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(EXTRACT_ELEMENTS_ENCHANTMENT_KEY);

        if (enchantment == null) return;

        int extractElementsLevel = itemMeta.getEnchantLevel(enchantment);
        if (extractElementsLevel == 0) return;

        IElementalVein vein = getClosestVein(player.getLocation(), getMaxDistance(extractElementsLevel));
        String itemModelPath = getItemModelPath(vein);

        itemMeta.setItemModel(new NamespacedKey("thegreatwork", itemModelPath));
        itemMeta.setDisplayName(HexUtils.colorify(getItemName(vein)));

        itemInMainHand.setItemMeta(itemMeta);
    }

    private static @NotNull String getItemModelPath(IElementalVein vein) {
        String itemModelPath = "sword/elemental_dagger_empty";

        if (vein != null) {
            itemModelPath = switch (vein.getInternalName()) {
                case "earth" -> "sword/elemental_dagger_earth";
                case "air" -> "sword/elemental_dagger_air";
                case "fire" -> "sword/elemental_dagger_fire";
                case "water" -> "sword/elemental_dagger_water";
                case "aether" -> "sword/elemental_dagger_aether";
                default -> itemModelPath;
            };
        }
        return itemModelPath;
    }

    private static @NotNull String getItemName(IElementalVein vein) {
        String itemName = "<g:#afa273:#413b25>Elemental Dagger";

        if (vein != null) {
            itemName = switch (vein.getInternalName()) {
                case "earth" -> "<g:#3b3526:#28251c>Elemental Dagger";
                case "air" -> "<g:#bda51c:#92811f>Elemental Dagger";
                case "fire" -> "<g:#cc8327:#9b4c19>Elemental Dagger";
                case "water" -> "<g:#1f4076:#102344>Elemental Dagger";
                case "aether" -> "<g:#761bc0:#56158b>Elemental Dagger";
                default -> itemName;
            };
        }
        return itemName;
    }

    private static IElementalVein getClosestVein(@NotNull Location location, int maxDistance) {
        Collection<IElementalVein> elementalVeins = ElementalVeinManager.getElementalVeins();

        double closestDistance = Double.MAX_VALUE;
        IElementalVein closestVein = null;

        for (IElementalVein elementalVein : elementalVeins) {
            ParametricSpline2D.Result closestPoint = elementalVein.getClosestPoint(location.getX(), location.getZ());
            if (closestPoint.distance() > maxDistance) continue;
            if (closestPoint.distance() > closestDistance) continue;

            closestDistance = closestPoint.distance();
            closestVein = elementalVein;
        }

        return closestVein;
    }

    public static void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) return;

        DamageSource damageSource = event.getDamageSource();
        Entity causingEntity = damageSource.getCausingEntity();
        if (causingEntity == null) return;

        if (!(causingEntity instanceof LivingEntity livingEntity)) return;

        EntityEquipment equipment = livingEntity.getEquipment();
        if (equipment == null) return;

        ItemStack itemInMainHand = equipment.getItemInMainHand();
        if (itemInMainHand.isEmpty()) return;

        ItemMeta itemMeta = itemInMainHand.getItemMeta();

        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        Enchantment enchantment = registry.get(EXTRACT_ELEMENTS_ENCHANTMENT_KEY);

        if (enchantment == null) return;

        int extractElementsLevel = itemMeta.getEnchantLevel(enchantment);
        if (extractElementsLevel == 0) return;

        int lootingLevel = itemMeta.getEnchantLevel(Enchantment.LOOTING);

        Collection<IElementalVein> elementalVeins = ElementalVeinManager.getElementalVeins();
        for (IElementalVein vein : elementalVeins) {
            trySpawnEssence(event.getEntity(), vein, extractElementsLevel, lootingLevel);
        }
    }

    private static void trySpawnEssence(@NotNull LivingEntity killed, IElementalVein vein, int extractElementsLevel, int lootingLevel) {
        if (vein == null) return;

        Location location = killed.getLocation();
        ParametricSpline2D.Result closestPoint = vein.getClosestPoint(location.getX(), location.getZ());

        int maxDistance = getMaxDistance(extractElementsLevel);

        if (closestPoint.distance() > maxDistance) return;

        String itemName = vein.getEssenceItemName();
        ItemStack itemStack = CustomItemManager.getItemStack(itemName);
        if (itemStack == null) return;

        int maxNumberOfItems = 1 + lootingLevel + (int) (killed.getMaxHealth() / 10);
        double dropChance = DROP_CHANCE + 0.01 * lootingLevel;

        if (RANDOM.nextFloat() > dropChance) return;

        int numberOfItems = RANDOM.nextInt(1, maxNumberOfItems + 1);
        itemStack.setAmount(numberOfItems);

        World world = location.getWorld();
        world.dropItemNaturally(location, itemStack);


    }

    private static int getMaxDistance(int extractElementsLevel) {
        return BASE_DISTANCE + extractElementsLevel * DISTANCE_PER_LEVEL;
    }
}
