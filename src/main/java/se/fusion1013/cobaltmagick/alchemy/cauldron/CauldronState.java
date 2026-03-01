package se.fusion1013.cobaltmagick.alchemy.cauldron;

import se.fusion1013.cobaltmagick.alchemy.IAlchemyState;
import se.fusion1013.cobaltmagick.alchemy.properties.AlchemyBlockProperties;

public class CauldronState implements IAlchemyState {

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

    @Override
    public void setVariance(int variance) {
        this.variance = variance;
    }

    public int getPotency() {
        return potency;
    }

    @Override
    public void setPotency(int potency) {
        this.potency = potency;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getWild() {
        return wild;
    }

    @Override
    public void setWild(int wild) {
        this.wild = wild;
    }

    public int getDecay() {
        return decay;
    }

    @Override
    public void setDecay(int decay) {
        this.decay = decay;
    }

    @Override
    public String toString() {
        return "Variance: " + variance + ". Potency: " + potency + ". Duration: " + duration + ". Wild: " + wild + ". Decay: " + decay;
    }
}
