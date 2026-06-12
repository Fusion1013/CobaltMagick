package se.fusion1013.cobaltmagick.alchemy.transmutation.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transmutation")
public class TransmutationEntity {

    @DatabaseField(generatedId = true, columnName = "id")
    private Long id;

    @DatabaseField(columnName = "input_item")
    private String inputItem;

    @DatabaseField(columnName = "output_item")
    private String outputItem;

    @DatabaseField(columnName = "catalyst")
    private String catalyst;

    @DatabaseField(columnName = "cost")
    private int cost;

    public TransmutationEntity() {
    }

    public Long getId() {
        return id;
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
}
