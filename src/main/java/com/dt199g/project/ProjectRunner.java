package com.dt199g.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


public class ProjectRunner {
    private final HashMap userInputs;
    private final HashMap botAnswers;
    private final List<Recipe2> recipeList;


    public ProjectRunner() {
        this.userInputs = createInputList();
        this.botAnswers = createResponses();
        recipeList = loadRecipes();
        //exportNewRecipe(recipeList);
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
                //.map(this::)
                .map(s -> s.toString())
                .subscribe(this::getByUserInput);
    }

    private List<>

    private List<Recipe2> loadRecipes() {
        String jsonRecipes = "";
        try(InputStream inputStream = getClass().getResourceAsStream("/recipes3.json")) {
            assert inputStream != null;
            jsonRecipes = new String(inputStream.readAllBytes());
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        List<Recipe2> recipes = gson.fromJson(jsonRecipes, new TypeToken<List<Recipe2>>(){}.getType());

        for (Recipe2 r : recipes) {
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



    private void expressRecipe(Recipe2 recipe) {
        System.out.printf("Here's a recipe one how to make %s! I hope it's what you're looking for.\n" +
                "It takes %d minutes to make and you need the following ingredients:\n%s\nThis is how you do it:\n%s"
                ,recipe.getName(), recipe.getCookingTime(), recipe.getIngredients(), recipe.getGuide());
    }

    private void exportNewRecipe(List<Recipe> recipes) {
        List<Recipe2> newRecipes = new ArrayList<>();
        for(Recipe recipe : recipes) {
            StringBuilder sb = new StringBuilder();
            String ingredients = recipe.getIngredients().stream()
                    .collect(Collectors.joining(",\n"));
            newRecipes.add(new Recipe2(recipe.getName(), recipe.getCookingTime(), ingredients, recipe.getGuide()));
        }
        Gson gson = new Gson();
        try(FileWriter writer = new FileWriter("C:\\javaProjects\\knla2000_project_vt23\\src\\main\\resources\\recipes3.json")) {
            gson.toJson(newRecipes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
