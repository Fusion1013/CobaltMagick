package se.fusion1013.cobaltmagick.alchemy.properties;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.MaterialVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.Map;

public class AlchemyBlockProperties implements INameProvider {

    private final StringVariable internalName = new StringVariable("internal_name");
    private final MaterialVariable materials = new MaterialVariable("materials");
    private final IntVariable variance = new IntVariable("variance");
    private final IntVariable potency = new IntVariable("potency");
    private final IntVariable duration = new IntVariable("duration");
    private final IntVariable wild = new IntVariable("wild");
    private final IntVariable decay = new IntVariable("decay");

    public AlchemyBlockProperties(String internalName, Material[] materials, int variance, int potency, int duration, int wild, int decay) {
        this.internalName.setValue(internalName);
        this.materials.setValues(materials);
        this.variance.setValue(variance);
        this.potency.setValue(potency);
        this.duration.setValue(duration);
        this.wild.setValue(wild);
        this.decay.setValue(decay);
    }

    public AlchemyBlockProperties(YamlConfiguration yaml) {
        internalName.load(yaml);
        materials.load(yaml);
        variance.load(yaml);
        potency.load(yaml);
        duration.load(yaml);
        wild.load(yaml);
        decay.load(yaml);
    }

    public AlchemyBlockProperties(Map<?, ?> map) {
        internalName.load(map);
        materials.load(map);
        variance.load(map);
        potency.load(map);
        duration.load(map);
        wild.load(map);
        decay.load(map);
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

    @Override
    public String getInternalName() {
        return internalName.getValue();
    }

    public String getPropertyInfo() {
        return "VA:" + variance.getValue() + " PO:" + potency.getValue() + " DU:" + duration.getValue() + " WI:" + wild.getValue() + " DE:" + decay.getValue();
    }
}
