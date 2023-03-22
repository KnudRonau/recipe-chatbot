# Project
## Environment & Tools

Operating System: Windows 10 Pro   
IDE: IntelliJ 2022.3.2   
Java version: OpenJDK 19, 2023-01-07   
Git version: 2.33.1.windows.1
## Purpose
The purpose of this project is to showcase proper usage of Reactive programming, Functional programming and Regular
Expressions through the creation of a chatbot. The focus lies on demonstrating usage of Regex, Streams and 
basic functional programming concepts and RxJava operations, while the chatbot shall be easy to use.
## Procedure
First, the `Recipe` class was created to match the data structure of the JSON file. Then the data had to be loaded from
JSON-file. The GSON library was chosen for this purpose as it provided a simple way to create a `List<Recipe>` from the
String loaded from the JSON-file using a classloader. This method was run and the ``List`` stored in a private variable 
during construction of the `ProjectRunner` class. Naturally, both the `RecipeList` and the fields of the Recipes are 
``final``, to avoid change of state. 

The ``runProject()`` method was the created. An ``Observable`` is created in this method, which emits the inputs read
by a ``Scanner``. The ``flatMap`` operation takes these emissions as argument in the method ``firstCheck``, which 
parses the user input onward to methods handling the bot's logic, and returns an ``Observable`` containing the bot's 
answer to the input. Furthermore, the ``runProject`` method creates two more ``Observables``, whose purpose is to 
remind the user of what is possible with the chatbot. In reality, these ``Observables``' main purpose is to showcase 
some reactive functionality, that had not been shown in the rest of the program.

Before implementing the methods behind the bot's logic, the regular expressions needed process the user's input had to 
be written. In total 6 regular expressions were written to understand the user's intend. The regular expressions made 
for checking if the user wanted to say hello, wanted an overview of possibilities, wanted a quick recipe or just wanted 
any recipe simply comprise a group of words to look for in the provided ``String``. The regex that looks for an ingredient
is a little more complex, as it uses lookaheads and lookbehind to find the desired ingredient. The same is the case 
with the time regex, that matches a digit before the word "minutes". 

All of these regular expressions are compiled tp ``Patterns`` and stored in a ``HashMap`` as ``Matcher`` objects, with 
the provided ``String`` as argument for ``.matcher``. This is done in order avoid creating ``Pattern`` and 
``Matcher`` where these are, thus increasing readability. The ``HashMap`` is returned from a method instead of stored
as a field so that only methods using the ``HashMap`` has access to it. 

With the regular expressions, it is then possible to understand the user input using the called ``firstCheck`` method.
If the userInput matches with the "overview" regex, an ``Observable`` is returned created by the ``getOverview`` method.
This method creates an ``Observable``, which emits the names of all the recipes, the number of recipes and how long time
it takes to cook all the recipes. The usage of the ``stream`` operations ``forEach``, ``count`` and ``reduce`` is found 
here.

If the input instead matches with any of the regular expressions that indicate that a recipe is wanted, 
``filterFunction()`` is called with the input and the recipeList as arguments. This function will behave depending on 
the matching regular expression. If filtering on an ingredient is detected, the method calls itself, showcasing 
recursion, with a filtered list and with the words indicating ingredient filter desire removed from the input string. 
If the user wants a quick, easy or simple recipe, the ``List`` will get sorted using ``stream().sorted`` to get the 
fasted available recipe. If user input matches the time based regular expression, it will filter list to only contain
recipes faster than the user's desire. And at last, if the user just wanted any recipe, or if an ingredient based 
filter was done and the method called itself recursively, a random recipe is returned from the list using ``findFirst``
like the others. ``findFirst`` is used so that an empty recipeList cannot be returned, instead an Observable, which
emits a sorry is returned. Using ``map`` all the recipes are turned to Strings based on the ``Recipe`` using the method 
``expressRecipe``, which nicely formats the Recipe to a String.

If the user input instead matches some sort of greeting, an ``Observable`` emitting a nice greeting from the bot is 
returned. If no regular expression matches the user input, an ``Observable`` emitting such message is returned. These 
answers come from a ``HashMap`` implemented in a similar fashion as the regular expression ``HashMap``.

## Discussion
The purpose of the project has been fulfilled and is deemed a success. The chatbot is simple
and easy to use, and is implemented with code showcasing regular expressions, functional programming, Java Stream
and reactive programming principles using RxJava. 

The whole program is designed by declarative, with no functions changing any states. All methods simply return an output
based on provided argument as input. Furthermore, an unorthodox usage of recursion is shown in ``filterFunction``, 
where the function calls itself. This is an atypical usage of recursion, but serves to show how recursion can be used in 
functional programming. In the same fashion, Lambda expression have been used extensively. The only major evidence of
imperative programming is the usage of ``if/else`` statements throughout, which was deemed necessary to decipher user 
intentions.

Regular expressions are the foundation of the program, with heavy usage throughout. Most notably the regular expressions
to look for ingredients and maximum user time showcase some complexity, comprising Pattern Modifiers, assertions, 
character classes, Groups, Anchors and Quantifiers. 

For the majority of the bot's logic implementation Java's `stream` api has been used. This is 
especially evident in the ``filterFunction`` method in which a plethora of stream operations are used to get the 
desired response based on the provided input. The most used are ``filter``, `map`, and `findFirst`. ``skip`` helped in 
order find a random `Recipe`, and `count`, `forEach` and `reduce` were used to easily and with high readability extract
information about the provided List in the method ``getOverview``.

No obvious use was found for RxJava specific operations besides the very powerful ``map`` and ``flatMap`` operations, 
whose usage in calling the ``firstCheck`` method with itself as argument is the foundation of the program. 
As a result, two other Observables were created, which showcase how one would use other powerful Reactive operations.

Here ``interval`` is used to create a new ``Observable``, which emit emissions every 100 milliseconds. ``map`` is used 
to transform it into a message about how the chatbot can be used in case the user forgot. ``buffer`` picks up emissions
in a time period and thereafter emits them as a ``List``. To showcase a scenario where a ``List`` is not wanted, 
``flatMapIterable`` is used to transform the ``List`` back to the original emissions. In case the emission come in too 
frequently, ``throttleFirst`` is used to only emit every 75 seconds instead. Lastly ``take`` is used to only get 10 
emissions from the Source, and then it is subscribed to, to print the emissions. Another Observable is created to
showcase how ``debounce`` works similarly yet differently than ``throttleFirst``. 

To sum up, it is clear that the program is a success in showcasing how to use Regex, RxJava, Stream and functional
programming principles to build a simple, user-friendly chatbot, with easy-to-read code.
