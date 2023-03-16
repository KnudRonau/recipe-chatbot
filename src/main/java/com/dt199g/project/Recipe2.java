package com.dt199g.project;

import java.util.List;

public class Recipe2 {
    private final String name;
    private final int cookingTime;
    private final String ingredients;
    private final String guide;

    public Recipe2(String name, int cookingTime, String ingredients, String guide) {

        this.name = name;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.guide = guide;
    }

    public String getName() {
        return name;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getGuide() {
        return guide;
    }

    public void printData() {
        System.out.printf("\n\nThis is a recipe for %s, that takes %d minutes to cook." +
                "\nIt has the following ingredients:\n%s \nIt is made in the following way:\n%s\n", name, cookingTime, ingredients, guide);
        System.out.println("\nThis disk too " + cookingTime + " to finish\n");
    }
}
