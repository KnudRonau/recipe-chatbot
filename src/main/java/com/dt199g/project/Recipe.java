package com.dt199g.project;

/**
 * Class representing a food recipe. All variables are final, and only getters are implemented.
 *
 * @author Knud Ronau Larsen
 */
public class Recipe {
    private final String name;
    private final int cookingTime;
    private final String ingredients;
    private final String guide;

    /**
     * Constructor for recipe
     * @param name The name of the recipe
     * @param cookingTime the time it takes to make it in minutes
     * @param ingredients The ingredients required
     * @param guide A guide on how to cook the recipe
     */
    public Recipe(String name, int cookingTime, String ingredients, String guide) {
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

}
