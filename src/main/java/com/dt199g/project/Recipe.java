package com.dt199g.project;

import java.util.List;

public class Recipe {
    private final String name;
    private final int cookingTime;
    private final List<String> ingredients;
    private final String guide;

    public Recipe(String name, int cookingTime, List<String> ingredients, String guide) {

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

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getGuide() {
        return guide;
    }

    public void printData() {
        System.out.printf("\n\nThis is a recipe for %s, that takes %d minutes to cook." +
                "\nIt has %s in it and is made in the following way:\n%s", name, cookingTime, ingredients.get(0), guide);
    }
}
