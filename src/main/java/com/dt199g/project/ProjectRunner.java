package com.dt199g.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ProjectRunner {
    private final HashMap userInputs;
    private final HashMap botAnswers;
    private final List<Recipe> recipeList;


    public ProjectRunner() {
        this.userInputs = createInputList();
        this.botAnswers = createResponses();
        recipeList = loadRecipes();
    }

    public void runProject() {
        Scanner scanner = new Scanner(System.in);
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                String line = scanner.nextLine();
                emitter.onNext(line);
            }
        })
                .observeOn(Schedulers.io())
                //.map()
                //.map(s -> s.toString())
                .subscribe(this::getByUserInput);
    }

    private List<Recipe> filterByIngredient(String s) {

    }

    private List<Recipe> loadRecipes() {
        String jsonRecipes = "";
        try(InputStream inputStream = getClass().getResourceAsStream("/recipes.json")) {
            assert inputStream != null;
            jsonRecipes = new String(inputStream.readAllBytes());
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Recipe> recipes = gson.fromJson(jsonRecipes, new TypeToken<List<Recipe>>(){}.getType());

        for (Recipe r : recipes) {
            r.printData();
        }
        return recipes;
    }

    private void getByUserInput(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i < recipeList.size()) {
                expressRecipe(recipeList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void expressRecipe(Recipe recipe) {
        System.out.printf("Here's a recipe one how to make %s! I hope it's what you're looking for.\n" +
                "It takes %d minutes to make and you need the following ingredients:\n%s\nThis is how you do it:\n%s"
                ,recipe.getName(), recipe.getCookingTime(), recipe.getIngredients(), recipe.getGuide());
    }

    private HashMap<String, String[]> createInputList() {
        HashMap<String, String[]> tempHashMap = new HashMap<>();
        tempHashMap.put("Greetings", new String[]{"Hi", "Hello", "Hey", "Heyo", "Good Evening","Good Morning",
            "Good Afternoon","Howdy", "Yo", "What's up", "Hola", "Bonjour", "Hejsa", "Hej", "Tjena"});
        tempHashMap.put("Containing", new String[]{"With", "That contains", "in it"});
        tempHashMap.put("Time", new String[]{"In less than", "Only have"});
        tempHashMap.put("Quick",new String[]{"Quick", "Simple", "Fast", "Easy"});

        return tempHashMap;
    }

    private  HashMap<String, String> createResponses() {
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("Greeting", "Hello there User! Let me know what kind of recipe, you're looking for");

        return tempHashMap;
    }


}
