package main.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TagDTO {

    private String name;

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
