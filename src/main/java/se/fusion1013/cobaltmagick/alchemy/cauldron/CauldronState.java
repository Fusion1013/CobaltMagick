package se.fusion1013.cobaltmagick.alchemy.cauldron;

import se.fusion1013.cobaltmagick.alchemy.AlchemyBlockProperties;

public class CauldronState {

    private int variance;
    private int potency;
    private int duration;
    private int wild;
    private int decay;

    public void update(int variance, int potency, int duration, int wild, int decay) {
        this.variance += variance;
        this.potency += potency;
        this.duration += duration;
        this.wild += wild;
        this.decay += decay;
    }

    public void update(AlchemyBlockProperties properties) {
        update(properties.getVariance(), properties.getPotency(), properties.getDuration(), properties.getWild(), properties.getDecay());
    }

    public int getVariance() {
        return variance;
    }

    public int getPotency() {
        return potency;
    }

    public int getDuration() {
        return duration;
    }

    public int getWild() {
        return wild;
    }

    public int getDecay() {
        return decay;
    }
}
