package com.dt199g.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class running the bot, showing functional and reactive programming.
 *
 * @author Knud Ronau Larsen
 */
public class ProjectRunner {
    private final List<Recipe> recipeList;

    /**
     * Constructor for the class, initializing the list of recipes.
     */
    public ProjectRunner() {
        recipeList = loadRecipes();
    }

    /**
     * The primary method of the program, in which Observables are created and subscribed to.
     */
    public void runProject() {
        //stores local HashMap with bot answers and emits a welcome guide
        HashMap<String, String> botAnswers = createResponses();
        Observable.just(botAnswers.get("welcome")).subscribe(System.out::println);

        //Showcases simple interval and debounce usage
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(i -> "When in doubt, try writing 'Overview'")
                .take(5)
                .debounce(200000, TimeUnit.MILLISECONDS)
                .subscribe(System.out::println);

        //Showcases simple buffer, throttle and take usage
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(i -> "Remember, you can filter by time and ingredient")
                .buffer(3000, TimeUnit.MILLISECONDS)
                .flatMapIterable(list -> list)
                .throttleFirst(75000, TimeUnit.MILLISECONDS)
                .take(10)
                .subscribe(System.out::println);


        //Starts the scanner to get user inputs
        Scanner scanner = new Scanner(System.in);
        //Emit every inputted line
        Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                String line = scanner.nextLine();
                emitter.onNext(line);
            }
        })
                .observeOn(Schedulers.io())
                .map(Object::toString)
                //Calls firstCheck() using flatmap to handle input
                .flatMap(this::firstCheck)
                .subscribe(System.out::println);
    }

    /**
     * Loads recipes from a json file using a classloader and gson
     * @return A List of Recipes loaded from the json file
     */
    private List<Recipe> loadRecipes() {
        String jsonRecipes = "";
        try(InputStream inputStream = getClass().getResourceAsStream("/recipes.json")) {
            assert inputStream != null;
            jsonRecipes = new String(inputStream.readAllBytes());
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        return gson.fromJson(jsonRecipes, new TypeToken<List<Recipe>>(){}.getType());
    }

    /**
     * Interprets the user input using regex and calls the proper function
     * @param input String inputted by user
     * @return An Observable with appropriate response to the user
     */
    private Observable<String> firstCheck(String input) {
        HashMap<String, Matcher> matchers = createRegex(input);
        HashMap<String, String> botAnswers = createResponses();

        //checks if input matches regular expression for getting an overview
        if(matchers.get("overview").find()) {
            return getOverview(recipeList);
        }
        //checks if input matches regular expression for looking for a recipe
        if ((matchers.get("containing").find()|matchers.get("quick").find()|matchers.get("time").find()|matchers.get("recipe").find())) {
            return filterFunction(input, recipeList);
        }
        //checks if input matches regular expression for getting a greeting
        if (matchers.get("greeting").find()) {
            return Observable.just(botAnswers.get("greeting"));
        } else {
            //The input was not understood
            return Observable.just(botAnswers.get("na"));
        }

    }

    /**
     * Method responsible for returning an Observable with an Overview over the recipes.
     * @param recipeList List of recipes to provide an overview of
     * @return Observable, which can emit an overview of the recipes
     */
    private Observable<String> getOverview(List<Recipe> recipeList) {
        return Observable.create(emitter -> {
            emitter.onNext("Here's a list of all the possible recipes: \n");
            //emits the name of all the recipes
            recipeList.stream().forEach(recipe -> emitter.onNext(recipe.getName()));
            //emits the number of available recipes
            emitter.onNext("\nThere are in total: " + recipeList.stream().count() + " recipes available");
            //emits how long time it would take to make all the recipes
            emitter.onNext("In total it would take: " +
                    recipeList.stream().map(Recipe::getCookingTime).reduce(0, Integer::sum) +
                    " minutes to make all the recipes");
            emitter.onComplete();
        });
    }

    /**
     * Method responsible for finding a recipe matching the inputted criteria
     * @param input The inputted criteria
     * @param recipeList The inputted list of recipes to comb through
     * @return Observable with a recipe, if any were appropriate
     */
    private Observable<String> filterFunction(String input, List<Recipe> recipeList) {
        //Loads regular expressions and a String to express that no matching recipe was found
        HashMap<String, Matcher> matchers = createRegex(input);
        String notFound = "Sorry, I couldn't find any recipes matching your criteria";
        //Checks if the input desires a filter based on ingredient
        if(matchers.get("containing").find()) {
            //desired ingredient
            String ingredient = matchers.get("containing").group();
            //list of recipes with the ingredient
            List<Recipe> filteredRecipeList = recipeList.stream()
                    .filter(recipe -> recipe.getIngredients().contains(ingredient))
                    .toList();
            //removes the words showing desire to filter upon ingredient from the input
            String filteredStringInput = input.replaceAll("\\b(with|contains|that contains|in it)\\b", "");
            //calls this method with the filtered input and recipe list
            return filterFunction(filteredStringInput, filteredRecipeList);
        //Checks if the user expressed desire for a quick or simple recipe
        } else if (matchers.get("quick").find()) {
            //sorts and finds takes first/quickest. returns this unless the list is empty, then a sorry Observable is returned
            Optional<String> sortedByFastest = recipeList.stream()
                    .sorted(Comparator.comparing(Recipe::getCookingTime))
                    .map(this::expressRecipe)
                    .findFirst();
            return sortedByFastest.map(Observable::just).orElseGet(() ->
                    Observable.just(notFound));
        //Checks if the user has expressed a time limit
        } else if (matchers.get("time").find()) {
            int maxCookingTime = Integer.parseInt(matchers.get("time").group());
            //Returns a recipe corresponding to desired criteria, or a sorry message
            Optional<String> filteredRecipeList = recipeList.stream()
                    .filter(recipe -> recipe.getCookingTime() <= maxCookingTime)
                    .map(this::expressRecipe)
                    .findFirst();
            return filteredRecipeList.map(Observable::just).orElseGet(() ->
                    Observable.just(notFound));
        } else {
            //Returns a random recipe, of one lives up to the criteria
            Optional<String> soughtAfterRecipe = recipeList.stream()
                    .skip((int) (recipeList.size() * Math.random()))
                    .map(this::expressRecipe)
                    .findFirst();
            return soughtAfterRecipe.map(Observable::just).orElseGet(() ->
                    Observable.just(notFound));
        }
    }


    /**
     * Method to format a given recipe to a String
     * @param recipe Provided recipe to format to a String
     * @return String containing all details about Recipe
     */
    private String expressRecipe(Recipe recipe) {
        return String.format("\nHere's a recipe on how to make %s! I hope it's what you're looking for.\n" +
                        "It takes %d minutes to make and you need the following ingredients:\n%s\n\nThis is how you do it:\n%s\n"
                ,recipe.getName(), recipe.getCookingTime(), recipe.getIngredients(), recipe.getGuide());
    }

    /**
     * Method that returns all the used regular expressions, compiled and stored as Matchers, ready to look through the provided String
     * @param input Inputted String to match the regular expressions upon
     * @return A Hashmap of Matchers to check inputted Strings against
     */
    private HashMap<String, Matcher> createRegex(String input) {
        HashMap<String, Matcher> tempHashMap = new HashMap<>();
        String[] greetings = {"hi", "hello", "hey", "heyo", "hood evening","good morning",
                "good afternoon","howdy", "yo", "what's up", "hola", "bonjour", "hejsa", "hej", "tjena"};
        //Matches with any of the above words
        tempHashMap.put("greeting", Pattern.compile("(?i)\\b(" + String.join("|", greetings) + ")\\b").matcher(input));
        //Matches with the word after containing... and before in it
        tempHashMap.put("containing", Pattern.compile("(?i)(?<=\\bwith|containing|that contains\\b)\\s+\\w+|\\w+\\s+(?=\\bin it\\b)").matcher(input));
        //Matches with the number before minutes
        tempHashMap.put("time", Pattern.compile("(?i)(?<=\\b)\\d+(?=\\s+minutes)").matcher(input));
        //Matches with any of the words in the pattern
        tempHashMap.put("quick", Pattern.compile("(?i)(\\bquick|fast|simple|easy\\b)").matcher(input));
        //Matches with any of the words
        tempHashMap.put("overview", Pattern.compile("(?i)(\\boverview|how many|all recipes\\b)").matcher(input));
        //Matches with any of the words
        tempHashMap.put("recipe", Pattern.compile("(?i)(\\brecipe|anything\\b)").matcher(input));
        return tempHashMap;
    }

    /**
     * Method that returns som Strings that the bot will output.
     * @return HashMap of Bot outputs.
     */
    private  HashMap<String, String> createResponses() {
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("welcome", "Welcome to Recipe Bot 1.0! My job is to deliver some tasty recipes to you.\n" +
                "If you'd like an overview of possible recipes, you can simply ask for it.\n" +
                "If you're open for anything, tell me that by just mentioning 'recipe' or 'anything', " +
                "and I'll get you a recipe in no time\n" +
                "If you'd like a recipe with something specific, just ask for a recipe with that ingredient.\n" +
                "If you're on a schedule, just state how much time you have. \n" +
                "Just write 'gimme a recipe I can make in 30 minutes', and it'll come right up!\n" +
                "If it just has to be quick and simple, you can also tell med that, I'll fetch you a recipe, " +
                "that can be done in no time.\nFor instance, try 'show me a simple recipe with beef'!\n");
        tempHashMap.put("greeting", "Hello there User! Let me know what kind of recipe, you're looking for");
        tempHashMap.put("na", "I'm sorry, I didn't quite understand that, what can I help you with?");
        return tempHashMap;
    }


}
