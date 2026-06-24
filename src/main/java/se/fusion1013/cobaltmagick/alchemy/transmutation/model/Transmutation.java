package se.fusion1013.cobaltmagick.alchemy.transmutation.model;

import se.fusion1013.cobaltmagick.alchemy.Element;

public final class Transmutation {

    private Long id;
    private String inputItem;
    private String outputItem;
    private Element element;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
