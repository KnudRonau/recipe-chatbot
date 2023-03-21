package com.dt199g.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ProjectRunner {
    private final HashMap<String, String[]> userInputs;
    private final HashMap<String, String> botAnswers;
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
                .map(Object::toString)
                .flatMap(this::interpretInput)
                .subscribe(System.out::println);
    }

    private List<Recipe> filterByIngredient(String s) {
        return null;
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

    private Observable<String> interpretInput(String input) {
        //return Observable.just("Hi");
        HashMap<String, String> patterns = createRegex();
        String containingRegex = "(?i)(?<=\\bwith|containing|that contains\\b)\\s+\\w+|\\w+\\s+(?=\\bin it\\b)";
        String timeRegex = "(?<=\\b)\\d+(?=\\s+minutes)";
        Matcher timeMatcher = Pattern.compile(timeRegex).matcher(input);
        String hurryRegex = "(?i)(?<=\\bquick|fast|simple|easy\\b)\\s+\\w+";
        Matcher hurryMatcher = Pattern.compile(hurryRegex).matcher(input);
        Pattern greetingPattern = Pattern.compile(patterns.get("Greeting"));
        Matcher greetingMatcher = greetingPattern.matcher(input);
        Matcher containingMatcher = Pattern.compile(containingRegex).matcher(input);

        if(greetingMatcher.find()) {
            return Observable.just(botAnswers.get("Greeting"));
        } else if(containingMatcher.find()) {

            String s = containingMatcher.group();
            System.out.println(s);

            return Observable.just("ingredientMatcher.group()");
        } else if(timeMatcher.find()) {
            String s = timeMatcher.group();
            System.out.println(s);
            return Observable.just("timeMatcher");
        } else if(hurryMatcher.find()) {
            String s = hurryMatcher.group();
            System.out.println(s);
            return Observable.just("Hurry matcher");
        }
        else  {

            return Observable.just(botAnswers.get("NA"));
        }
    }

    private String containingInterpreter(String containingString) {
        List<Recipe> possibleRecipes = recipeList.stream()
                .filter(recipe -> recipe.getIngredients().contains(containingString))
                .toList();

        Random random = new Random();
        return expressRecipe(possibleRecipes.get(random.nextInt(possibleRecipes.size())));
    }



    private String expressRecipe(Recipe recipe) {
        System.out.printf("Here's a recipe one how to make %s! I hope it's what you're looking for.\n" +
                "It takes %d minutes to make and you need the following ingredients:\n%s\nThis is how you do it:\n%s"
                ,recipe.getName(), recipe.getCookingTime(), recipe.getIngredients(), recipe.getGuide());

        return String.format("Here's a recipe one how to make %s! I hope it's what you're looking for.\n" +
                        "It takes %d minutes to make and you need the following ingredients:\n%s\nThis is how you do it:\n%s"
                ,recipe.getName(), recipe.getCookingTime(), recipe.getIngredients(), recipe.getGuide());
    }

    private HashMap<String, String[]> createInputList() {
        HashMap<String, String[]> tempHashMap = new HashMap<>();
        tempHashMap.put("Greeting", new String[]{"hi", "hello", "hey", "heyo", "hood evening","good morning",
            "good afternoon","howdy", "yo", "what's up", "hola", "bonjour", "hejsa", "hej", "tjena"});
        tempHashMap.put("Containing", new String[]{"with", "that contains", "in it"});
        tempHashMap.put("Time", new String[]{"in less than", "only have"});
        tempHashMap.put("Quick",new String[]{"quick", "simple", "fast", "easy"});

        return tempHashMap;
    }

    private HashMap<String, String> createRegex() {
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("Greeting", "\\b(" + String.join("|", userInputs.get("Greeting")) + ")\\b");
        tempHashMap.put("Containing", "\\b(" + String.join("|", userInputs.get("Containing")) + ")\\b");
        tempHashMap.put("Time", "\\b(" + String.join("|", userInputs.get("Time")) + ")\\b");
        tempHashMap.put("Quick", "\\b(" + String.join("|", userInputs.get("Quick")) + ")\\b");

        return tempHashMap;

    }

    private  HashMap<String, String> createResponses() {
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("Greeting", "Hello there User! Let me know what kind of recipe, you're looking for");
        tempHashMap.put("NA", "I'm sorry, I didn't quite understand that, what can I help you with?");

        return tempHashMap;
    }


}
