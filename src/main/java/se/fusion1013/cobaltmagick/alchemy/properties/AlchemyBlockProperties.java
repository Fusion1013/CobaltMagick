package se.fusion1013.cobaltmagick.alchemy.properties;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;
import se.fusion1013.cobaltCore.variable.MaterialVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.Map;

public class AlchemyBlockProperties implements IRegistryItem {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final MaterialVariable materials = new MaterialVariable("materials");
    private final IntVariable variance = new IntVariable("variance");
    private final IntVariable potency = new IntVariable("potency");
    private final IntVariable duration = new IntVariable("duration");
    private final IntVariable wild = new IntVariable("wild");
    private final IntVariable decay = new IntVariable("decay");
    private final IntVariable failure = new IntVariable("failure");
    private final LiteralVariable type = new LiteralVariable("type", "external", new String[]{"internal", "external"});

    public AlchemyBlockProperties(String internalName, Material[] materials, int variance, int potency, int duration, int wild, int decay, int failure) {
        this.internalName.setValue(internalName);
        this.materials.setValues(materials);
        this.variance.setValue(variance);
        this.potency.setValue(potency);
        this.duration.setValue(duration);
        this.wild.setValue(wild);
        this.decay.setValue(decay);
        this.failure.setValue(failure);
    }

    public AlchemyBlockProperties(YamlConfiguration yaml) {
        internalName.load(yaml);
        materials.load(yaml);
        variance.load(yaml);
        potency.load(yaml);
        duration.load(yaml);
        wild.load(yaml);
        decay.load(yaml);
        failure.load(yaml);
    }

    public AlchemyBlockProperties(Map<?, ?> map) {
        internalName.load(map);
        materials.load(map);
        variance.load(map);
        potency.load(map);
        duration.load(map);
        wild.load(map);
        decay.load(map);
        failure.load(map);
    }

    public AlchemyBlockProperties(JsonObject json) {
        throw new NotImplementedException();
    }

    public Material[] getMaterials() {
        return materials.getMaterials();
    }

    public int getVariance() {
        return variance.getValue();
    }

    public int getPotency() {
        return potency.getValue();
    }

    public int getDuration() {
        return duration.getValue();
    }

    public int getWild() {
        return wild.getValue();
    }

    public int getDecay() {
        return decay.getValue();
    }

    public int getFailure() {
        return failure.getValue();
    }

    public boolean isInternal() {
        return type.getValue().equalsIgnoreCase("internal");
    }

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    public String getPropertyInfo() {
        return "VA:" + variance.getValue() + " PO:" + potency.getValue() + " DU:" + duration.getValue() + " WI:" + wild.getValue() + " DE:" + decay.getValue() + " FA:" + failure.getValue();
    }
}
