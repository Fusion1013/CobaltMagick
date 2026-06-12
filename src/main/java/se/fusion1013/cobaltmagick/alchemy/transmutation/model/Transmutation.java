package se.fusion1013.cobaltmagick.alchemy.transmutation.model;

import java.util.Objects;

public final class Transmutation {

    private Long id;
    private String inputItem;
    private String outputItem;
    private String catalyst;
    private int cost;

    public Transmutation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInputItem() {
        return inputItem;
    }

    public void setInputItem(String inputItem) {
        this.inputItem = inputItem;
    }

    public String getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(String outputItem) {
        this.outputItem = outputItem;
    }

    public String getCatalyst() {
        return catalyst;
    }

    public void setCatalyst(String catalyst) {
        this.catalyst = catalyst;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transmutation) obj;
        return Objects.equals(this.inputItem, that.inputItem) &&
                Objects.equals(this.outputItem, that.outputItem) &&
                Objects.equals(this.catalyst, that.catalyst) &&
                this.cost == that.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputItem, outputItem, catalyst, cost);
    }

    @Override
    public String toString() {
        return "Transmutation[" +
                "inputItem=" + inputItem + ", " +
                "outputItem=" + outputItem + ", " +
                "catalyst=" + catalyst + ", " +
                "cost=" + cost + ']';
    }


}
