package se.fusion1013.cobaltmagick.foundry;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltmagick.CobaltMagick;
import se.fusion1013.cobaltmagick.pedestal.PedestalManager;
import se.fusion1013.cobaltmagick.pedestal.PedestalStyle;
import se.fusion1013.cobaltmagick.util.AdvancementUtil;

import java.util.*;

public class FoundryRecipe implements IFoundryRecipe {

    private static final Random random = new Random();

    private final StringVariable internalName = new StringVariable("internal_name");
    private final StringVariable displayName = new StringVariable("display_name");
    private final StringVariable mold = new StringVariable("mold");
    private final StringVariable pattern = new StringVariable("pattern");
    private final List<FoundryPillar> pillars = new ArrayList<>();
    private final Map<String, String> items = new HashMap<>();
    private final Map<String, String> pedestals = new HashMap<>();
    private final StringVariable result = new StringVariable("result");

    public static IFoundryRecipe create(ConfigurationSection yaml) {
        return new FoundryRecipe(yaml);
    }

    public static IFoundryRecipe create(JsonObject json) {
        return null;
    }

    private FoundryRecipe(ConfigurationSection yaml) {
        internalName.load(yaml);
        displayName.load(yaml);
        mold.load(yaml);
        pattern.load(yaml);
        result.load(yaml);
        loadItems(yaml);
        loadBlocks(yaml);

        loadPillars();
    }

    private void loadItems(ConfigurationSection yaml) {
        ConfigurationSection itemsConfig = yaml.getConfigurationSection("items");
        if (itemsConfig == null) return;

        for (String key : itemsConfig.getKeys(false)) {
            String value = itemsConfig.getString(key);
            items.put(key, value);
        }
    }

    private void loadBlocks(ConfigurationSection yaml) {
        ConfigurationSection pedestalsConfig = yaml.getConfigurationSection("pedestals");
        if (pedestalsConfig == null) return;

        for (String key : pedestalsConfig.getKeys(false)) {
            String value = pedestalsConfig.getString(key);
            pedestals.put(key, value);
        }
    }

    private void loadPillars() {
        List<String> lines = pattern.getValueList();
        int zOffset = -(lines.size() - 1) / 2;

        for (int z = 0; z < lines.size(); z++) {
            String line = lines.get(z);
            int xOffset = -(line.length() - 1) / 2;
            int zPos = z + zOffset;

            for (int x = 0; x < line.length(); x++) {
                int xPos = x + xOffset;
                char c = line.charAt(x);
                if (c == '-') continue;

                String requiredPedestal = pedestals.get("" + c);
                String requiredItem = items.get("" + c);

                Vector offset = new Vector(xPos, 0, zPos);
                FoundryPillar pillar = new FoundryPillar(offset, requiredPedestal, requiredItem);
                pillars.add(pillar);
            }
        }
    }

    @Override
    public boolean validate(Location anvilLocation) {
        for (FoundryPillar pillar : pillars) {
            Location currentLocation = anvilLocation.clone().add(pillar.offset());
            if (!validate(currentLocation, pillar.pedestalType(), pillar.getItem())) return false;
        }

        return true;
    }

    private boolean validate(Location location, String pedestalType, ItemStack requiredItem) {
        PedestalStyle pedestalStyle = PedestalManager.getInstance().getPedestalStyle(location);
        if (pedestalStyle == null) return false;
        if (!pedestalStyle.getName().equalsIgnoreCase(pedestalType)) return false;

        Collection<Item> nearbyItems = location.clone().add(0, 1, 0).toCenterLocation().getNearbyEntitiesByType(Item.class, 0.5f);
        if (nearbyItems.isEmpty()) return false;
        if (nearbyItems.size() > 1) return false;

        Item itemEntity = nearbyItems.iterator().next();
        ItemStack foundItemStack = itemEntity.getItemStack();
        if (foundItemStack.getAmount() > 1) return false;

        return CustomItemManager.compare(foundItemStack, requiredItem);
    }

    @Override
    public void execute(Location location) {
        World world = location.getWorld();
        Location resultLocation = location.clone().add(0, 5, 0).toCenterLocation();

        // Make all items unable to pick up
        for (int i = 0; i < pillars.size(); i++) {
            FoundryPillar pillar = pillars.get(i);
            Location pillarLocation = location.clone().add(pillar.offset()).toCenterLocation();
            Location itemLocation = pillarLocation.clone().add(0, 1, 0).toCenterLocation();
            Collection<Item> nearbyItems = itemLocation.getNearbyEntitiesByType(Item.class, 0.5f);
            for (Item item : nearbyItems) {
                item.setCanPlayerPickup(false);
                item.setCanMobPickup(false);
                item.setGlowing(true);
                item.getItemStack().editPersistentDataContainer(ps -> ps.set(PedestalManager.NO_PICK_UP, PersistentDataType.INTEGER, 1));

                Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
                    item.remove();
                    world.spawnParticle(Particle.END_ROD, resultLocation, 50, .2, .2, .2, .02);
                    world.spawnParticle(Particle.END_ROD, itemLocation, 10, .2, .2, .2, .01);
                    world.playSound(itemLocation, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, SoundCategory.BLOCKS, random.nextFloat(0.9f, 1.1f), random.nextFloat(0.9f, 1.1f));
                }, i * 20L);
            }
        }

        Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(), () -> {
            world.spawn(resultLocation, Item.class, item -> {
                item.setItemStack(getResult());
                item.setVelocity(new Vector(0, 0, 0));
                item.setGlowing(true);
            });
            world.spawnParticle(Particle.END_ROD, resultLocation, 50, .2, .2, .2, .5);
            world.playSound(resultLocation, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.BLOCKS, random.nextFloat(0.9f, 1.1f), random.nextFloat(0.9f, 1.1f));

            Advancement metallurgyAdvancement = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "metallurgy/root"));
            Advancement toolAdvancement = Bukkit.getAdvancement(new NamespacedKey("fusion1013", "metallurgy/" + internalName.getValue()));
            if (metallurgyAdvancement != null) AdvancementUtil.grantInRange(location, metallurgyAdvancement, 16);
            if (toolAdvancement != null) AdvancementUtil.grantInRange(location, toolAdvancement, 16);
        }, pillars.size() * 20L);
    }

    @Override
    public void placeTemplate(Location center, boolean cinematic) {
        center.getBlock().setBlockData(Material.ANVIL.createBlockData());

        for (int i = 0; i < pillars.size(); i++) {
            FoundryPillar pillar = pillars.get(i);
            Location pillarLocation = center.clone().add(pillar.offset());
            Bukkit.getScheduler().runTaskLater(CobaltMagick.getInstance(),
                    () -> PedestalManager.getInstance().create(pillarLocation, pillar.pedestalType()),
                    random.nextInt(0, pillars.size() * 3));
        }
    }

    @Override
    public String getDisplayName() {
        return displayName.getValue();
    }

    @Override
    public ItemStack getResult() {
        String resultValue = result.getValue();
        return CustomItemManager.getItemStack(resultValue);
    }

    @Override
    public ItemStack getMold() {
        String resultValue = mold.getValue();
        return CustomItemManager.getItemStack(resultValue);
    }

    @Override
    public boolean isCorrectMold(ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemStack mold = getMold();
        if (mold == null) return false;
        return CustomItemManager.compare(mold, itemStack);
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }
}
