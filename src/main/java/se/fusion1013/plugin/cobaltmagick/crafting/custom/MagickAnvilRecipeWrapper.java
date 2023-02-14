package se.fusion1013.plugin.cobaltmagick.crafting.custom;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.crafting.IRecipeWrapper;
import se.fusion1013.plugin.cobaltcore.item.crafting.RecipeManager;
import se.fusion1013.plugin.cobaltmagick.CobaltMagick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MagickAnvilRecipeWrapper implements IRecipeWrapper {

    //region FIELDS

    private final NamespacedKey key;
    private final String[] input;
    private final String[] output;

    //endregion

    //region CONSTRUCTORS

    public MagickAnvilRecipeWrapper(NamespacedKey key, String[] input, String[] output) {
        this.key = key;
        this.input = input;
        this.output = output;
    }

    //endregion

    //region REGISTER

    @Override
    public boolean register() {
        List<ItemStack> inputItems = new ArrayList<>();
        List<ItemStack> outputItems = new ArrayList<>();

        // Add input items
        for (String s : input) {
            ItemStack item = CustomItemManager.getCustomItemStack(s);
            if (item != null) inputItems.add(item);
        }

        // Add output items
        for (String s : output) {
            ItemStack item = CustomItemManager.getCustomItemStack(s);
            if (item != null) outputItems.add(item);
        }

        MagickAnvilRecipe recipe = new MagickAnvilRecipe(key, inputItems.toArray(new ItemStack[0]), outputItems.toArray(new ItemStack[0]));
        RecipeManager.registerCobaltRecipe(recipe);

        return true;
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public String getRecipeType() {
        return "magick_anvil";
    }

    @Override
    public String getItemName() {
        StringBuilder builder = new StringBuilder();
        for (String s : output) {
            builder.append(s);
            builder.append(", ");
        }
        String out = builder.toString();
        return out.substring(out.length()-2);
    }

    //endregion

    //region LOADING

    @Override
    public List<IRecipeWrapper> loadFromFile(YamlConfiguration yaml, String itemName) {
        List<IRecipeWrapper> wrappers = new ArrayList<>();

        if (yaml.contains("magick_anvil_crafting")) {
            List<Map<?, ?>> mapList = yaml.getMapList("magick_anvil_crafting");

            for (Map<?, ?> map : mapList) {
                map.keySet().forEach(k -> {
                    Map<?, ?> values = (Map<?, ?>) map.get(k);
                    NamespacedKey key = new NamespacedKey(CobaltMagick.getInstance(), itemName + "_" + k);
                    List<String> inputs = (List<String>) values.get("ingredients");
                    List<String> outputs = new ArrayList<>();
                    if (values.containsKey("outputs")) outputs.addAll((List<String>) values.get("outputs"));
                    outputs.add(itemName);

                    MagickAnvilRecipeWrapper wrapper = new MagickAnvilRecipeWrapper(key, inputs.toArray(new String[0]), outputs.toArray(new String[0]));

                    wrappers.add(wrapper);
                });
            }
        }

        return wrappers;
    }

    //endregion
}
