package main.dto;

public class TagDTO {

    private String name;

//    @JsonProperty(value = "weight", defaultValue = "x")
    private double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
