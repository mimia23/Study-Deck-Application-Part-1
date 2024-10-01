// -----------------------------------------------------------------
// Project: Part 1, Summary
// -----------------------------------------------------------------

// TODO: Add a comment here saying whom you worked with (people or
// AI), and how it helped. If you did not work with anyone (which
// would be surprising), say that.

// You are going to design an application to allow a user to
// self-study using flash cards. In this part of the project,
// a user will...

// 1. Be prompted to choose from a menu of available flash
//    card decks; this menu will repeat until a valid
//    selection is made.
//
// 2. Proceed through each card in the selected deck,
//    one-by-one. For each card, the front is displayed,
//    and the user is allowed time to reflect; then the
//    back is displayed; and the user is asked if they
//    got the correct answer.
//
// 3. Once the deck is exhausted, the program outputs the
//    number of self-reported correct answers and ends.
//

// Of course, we'll design this program step-by-step, AND
// you've already done pieces of this in homework!!
// (Note: you are welcome to leverage your prior work and/or
// code found in the sample solutions & lecture notes.)
//

// Lastly, here are a few overall project requirements...
// - Since mutation hasn't been covered in class, your design is
//   NOT allowed to make use of mutable variables and/or lists.
// - As included in the instructions, all interactive parts of
//   this program MUST make effective use of the reactConsole
//   framework.
// - Staying consistent with our Style Guide...
//   * All functions must have:
//     a) a preceding comment specifying what it does
//     b) an associated @EnabledTest function with sufficient
//        tests using testSame
//   * All data must have:
//     a) a preceding comment specifying what it represents
//     b) associated representative examples
// - You will be evaluated on a number of criteria, including...
//   * Adherence to instructions and the Style Guide
//   * Correctly producing the functionality of the program
//   * Design decisions that include choice of tests, appropriate
//     application of list abstractions, and task/type-driven
//     decomposition of functions.
//

// -----------------------------------------------------------------
// Data design
// (Hint: see Homework 3, Problem 2)
// -----------------------------------------------------------------

// TODO 1/2: Design the data type FlashCard to represent a single
//           flash card. You should be able to represent the text
//           prompt on the front of the card as well as the text
//           answer on the back. Include at least 3 example cards
//           (which will come in handy later for tests!).
//

// TODO 2/2: Design the data type Deck to represent a deck of
//           flash cards. The deck should have a name, as well
//           as a Kotlin list of flash cards.
//
//           Include at least 2 example decks based upon the
//           card examples above.
//
// Ahana and I strategized different ways to write the chooseOption function

import khoury.EnabledTest
import khoury.fileReadAsList
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame

data class FlashCard(val frontText: String, val backText: String)
data class Deck(val name: String, val flashCards: List<FlashCard>)

data class StudyState(
    val currentCardIndex: Int,
    val isLookingAtFront: Boolean,
    val correctAnswers: Int,
)

val square1Front = "1^2 = ?"
val square2Front = "2^2 = ?"
val square3Front = "3^2 = ?"

val square1Back = "1"
val square2Back = "4"
val square3Back = "9"

// function generates perfect square flashcards
fun perfectSquares(count: Int): List<FlashCard> {
    return (1..count).map { i ->
        val frontText = "$i^2 = ?"
        val backText = (i * i).toString()
        FlashCard(frontText, backText)
    }
}

@EnabledTest
fun testPerfectSquares() {
    fun helpTest(count: Int, expectedSize: Int) {
        val flashCards = perfectSquares(count)
        testSame(flashCards.size, expectedSize, "Count: $count")
        for ((index, card) in flashCards.withIndex()) {
            val expectedFront = "index^2 = ?"
            val expectedBack = (index * index).toString()
            testSame(card.frontText, expectedFront, "Card $index Front")
            testSame(card.backText, expectedBack, "Card $index Back")
        }
    }

    helpTest(0, 0) // No cards
    helpTest(1, 1) // One card
    helpTest(5, 5) // Five cards
    helpTest(10, 10) // Ten cards
    helpTest(100, 100) // One hundred cards
}

val charSep = "|"

// function converts a flashcard to a string
fun cardToString(flashcard: FlashCard): String {
    return "${flashcard.frontText} | ${flashcard.backText}"
}

@EnabledTest
fun testCardToString() {
    fun helpTest(frontText: String, backText: String, expected: String) {
        val flashcard = FlashCard(frontText, backText)
        testSame(cardToString(flashcard), expected, "Front: $frontText, Back: $backText")
    }

    helpTest("Question 1", "Answer 1", "Question 1 | Answer 1")
    helpTest("Question 2", "Answer 2", "Question 2 | Answer 2")
    helpTest("Question 3", "Answer 3", "Question 3 | Answer 3")
    helpTest("Koala", "Kangaroo", "Koala | Kangaroo")
    helpTest("Austria", "Andorra", "Austria | Andorra")
}

// Function to convert a string to flashcard
fun stringToCard(cardStringS: String): FlashCard {
    val parts = cardStringS.split("|").map { it.trim() }
    return FlashCard(parts[0], parts [1])
}

@EnabledTest
fun testStringToCard() {
    fun helpTest(cardString: String, expectedFront: String, expectedBack: String) {
        val flashcard = stringToCard(cardString)
        testSame(flashcard.frontText, expectedFront, "Front: $expectedFront")
        testSame(flashcard.backText, expectedBack, "Back: $expectedBack")
    }

    helpTest("Question 1 | Answer 1", "Question 1", "Answer 1")
    helpTest("Question 2 | Answer 2", "Question 2", "Answer 2")
    helpTest("Question 3 | Answer 3", "Question 3", "Answer 3")
    helpTest("Koala | Kangaroo", "Koala", "Kangaroo")
    helpTest("Austria | Andorra", "Austria", "Andorra")
}

// function reads flashcards from file
fun readCardsFile(filePath: String): List<FlashCard> {
    val flash: List<String> = fileReadAsList(filePath)
    val result: List<FlashCard> = flash.map(::stringToCard)
    return result
}

@EnabledTest
fun testReadCardsFile() {
    val filePath1 = "file_not_real.txt"
    val result1 = readCardsFile(filePath1)
    testSame(result1.size, 0, "File not found, should return empty list")

    val filePath2 = "example.txt"
    val result2 = readCardsFile(filePath2)
    testSame(result2.size, 2, "Valid File, should return list with 2 flashcards")

    val flashcard1 = FlashCard("front 1", "back 1")
    val flashcard2 = FlashCard("front 2", "back 2")

    testSame(result2.contains(flashcard1), true, "Flashcard 1 not read correctly")
    testSame(result2.contains(flashcard2), true, "Flashcard 2 not read correctly")
}

// function checks if string input is positive
fun isPositive(input: String): Boolean {
    return input.trim().startsWith("y", ignoreCase = true)
}

@EnabledTest
fun testIsPositive() {
    fun helpTest(str: String, expected: Boolean) {
        testSame(isPositive(str), expected, str)
    }

    helpTest("yes", true)
    helpTest("Yes", true)
    helpTest("YES", true)
    helpTest("yup", true)

    helpTest("nope", false)
    helpTest("NO", false)
    helpTest("nah", false)
    helpTest("not a chance", false)

    // should pass,
    // despite doing the wrong thing
    helpTest("indeed", false)
}

// function converts list of deck names into a string
fun choicesToText(deckNames: List<String>): String {
    val promptMenu = "Enter your choice"
    return deckNames.joinToString("\n") { "${deckNames.indexOf(it) + 1}. $it" }
}

@EnabledTest
fun testChoicesToText() {
    fun helpTest(deckNames: List<String>, expected: String) {
        val result = choicesToText(deckNames)
        testSame(result, expected, "Input: $deckNames")
    }

    helpTest(emptyList(), "Enter your choice")
    helpTest(listOf("deck 1", "deck 2"), "Enter your choice\n1. Deck 1\n2. Deck 2")
}

// function that gets name of deck
fun getDeckName(deck: Deck): String {
    return deck.name
}

// function that renders deck options
fun renderDeckOptions(decks: List<Deck>, state: Int): String {
    return choicesToText(decks.map(::getDeckName))
}

// function to keep input valid in the range of valid indices
fun keepIfValid(input: String, validIndices: IntRange): Int {
    val userInput = input.toIntOrNull()

    return if (userInput != null && userInput in 1..validIndices.last) {
        userInput - 1
    } else {
        -1
    }
}

// function checks for a valid choice that was entered
fun validChoiceEntered(decks: List<Deck>, state: Int): Boolean {
    return state in decks.indices
}

// this function announces the chosen deck
fun choiceAnnouncement(deckName: String): String {
    return "You chose: $deckName"
}

// function that renders the choice
fun renderChoice(deck: Deck): String {
    return choiceAnnouncement(getDeckName(deck))
}

@EnabledTest
fun testRenderChoice() {
    // Test 1: Number that fits
    val deck1 = Deck("Deck 1", emptyList())
    val result1 = renderChoice(deck1)
    testSame(result1, "You chose: Deck 1", "Test Case 1")

    // Test 2: Empty deck name
    val deck2 = Deck("Deck ", emptyList())
    val result2 = renderChoice(deck2)
    testSame(result1, "You chose: ", "Test Case 2")
}

// function that chooses a deck from a list of decks
fun chooseOption(decks: List<Deck>): Deck {
    println("Choose a deck:")
    println(choicesToText(decks.map(::getDeckName)))

    val userInput = readLine()?.toIntOrNull()

    return if (userInput != null && userInput in 1..decks.size) {
        decks[userInput - 1]
    } else {
        println("Invalid. Enter valid number.")
        chooseOption(decks)
    }
}

// function to study a deck and return the number of correct answers answered
fun studyDeck(deck: Deck, state: StudyState = StudyState(0, true, 0)): Int {
    val nextState = reactConsole(
        initialState = state,
        stateToText = { currentState ->
            if (currentState.currentCardIndex < deck.flashCards.size) {
                val currentCard = deck.flashCards[currentState.currentCardIndex]
                if (currentState.isLookingAtFront) {
                    "Front: ${currentCard.frontText}\nPress Enter to see back"
                } else {
                    "Back: ${currentCard.backText}\nCorrect? (Y)es/(N)o"
                }
            } else {
                "No more cards in deck"
            }
        },
        nextState = { currentState, userInput ->
            if (currentState.currentCardIndex < deck.flashCards.size) {
                if (currentState.isLookingAtFront) {
                    currentState.copy(isLookingAtFront = false)
                } else {
                    val updatedCorrectAnswers = if (userInput.equals("Y", ignoreCase = true)) {
                        currentState.correctAnswers + 1
                    } else {
                        currentState.correctAnswers
                    }
                    currentState.copy(
                        isLookingAtFront = true,
                        currentCardIndex = currentState.currentCardIndex + 1,
                        correctAnswers = updatedCorrectAnswers,
                    )
                }
            } else {
                currentState
            }
        },
        isTerminalState = { state ->
            state.currentCardIndex >= deck.flashCards.size
        },
    )

    return nextState.correctAnswers
}

@EnabledTest
fun testStudyDeckWithNoCards() {
    val deck = Deck("Empty Deck", emptyList())
    val correctAnswers = studyDeck(deck)
    testSame(correctAnswers, 0, "Empty deck should return 0 correct answers")
}

@EnabledTest
fun testStudyDeckWithOneCard() {
    val flashCard = FlashCard("Question", "Answer")
    val deck = Deck("Single Card Deck", listOf(flashCard))
    val correctAnswers = studyDeck(deck)
    testSame(correctAnswers, 1, "One card deck, correct answer")
}

@EnabledTest
fun testStudyDeckWithMultipleCards() {
    val flashCards = listOf(
        FlashCard("What is Barbie's full name?", "Barbara Millicent Roberts"),
        FlashCard("What mammal can fly?", "A Bat"),
        FlashCard("What animal kills the most people?", "Mosquitoes"),

    )
    val deck = Deck("Multiple Cards Deck", flashCards)
    val correctAnswers = studyDeck(deck)
    testSame(correctAnswers, 3, "All answers correct")
}

@EnabledTest()
fun testStudyDeckWithWrongAnswers() {
    val flashCards = listOf(
        FlashCard("What is Barbie's full name?", "Barbara Millicent Roberts"),
        FlashCard("What mammal can fly?", "A Bat"),
        FlashCard("What animal kills the most people?", "Mosquitoes"),

    )
    val deck = Deck("Multiple Cards Deck", flashCards)

    val correctAnswers = studyDeck(deck, StudyState(0, true, 0))

    testSame(correctAnswers, 0, "All answers incorrect")
}

@EnabledTest()
fun testStudyDeckWithRightAndWrongAnswers() {
    val flashCards = listOf(
        FlashCard("What is Barbie's full name?", "Barbara Millicent Roberts"),
        FlashCard("What mammal can fly?", "A Bat"),
        FlashCard("What animal kills the most people?", "Mosquitoes"),
    )

    val deck = Deck("Multiple Cards Deck", flashCards)

    val correctAnswers = studyDeck(deck, StudyState(0, true, 0))
    if (0 == 0) "Y" else "N"

    testSame(correctAnswers, 1, "Mixed answers, one correct")
}

fun main() {
    val flashcard1 = FlashCard("What is Barbie's full name?", "Barbara Millicent Roberts")
    val flashcard2 = FlashCard("What mammal can fly?", "A Bat")
    val flashcard3 = FlashCard("What animal kills the most people?", "Mosquitoes")

    val flashcard4 = FlashCard("Where was Caesar salad made?", "Tijuana, Mexico")
    val flashcard5 = FlashCard("What cookie is vegan?", "Oreos")
    val flashcard6 = FlashCard("What spice is a hallucinogen ?", "Nutmeg")

    val flashCards = listOf(flashcard1, flashcard2, flashcard3, flashcard4, flashcard5, flashcard6)
    val deck1 = Deck("Random Trivia", listOf(flashcard1, flashcard2, flashcard3))
    val deck2 = Deck("Food Trivia", listOf(flashcard4, flashcard5, flashcard6))
    val decks = listOf(deck1, deck2)

    val deck = listOf(
        FlashCard("Question 1 Front", "Answer 1 Back"),
        FlashCard("Question 2 Front", "Answer 2 Back"),
        FlashCard("Question 3 Front", "Answer 3 Back"),
    )

    val perfectSquaresFlashcards = perfectSquares(3)

    perfectSquaresFlashcards.forEachIndexed { index, flashcard ->
        println("Card ${index + 1}:")
        println("Front: ${flashcard.frontText}")
        println("Back: ${flashcard.backText}\n")
    }

    val cardString = cardToString(flashCards[0])
    println(cardString)
    val charSep = "|"

    val flashcardString = "What is Barbie's full name? | Barbara Millicent Roberts"
    val flashcard = stringToCard(flashcardString)

    println(flashcardString)
    println("Front: ${flashcard.frontText}")
    println("Press Enter to see back")
    readLine()
    println("Back: ${flashcard.backText}")

    val filePath = "example.txt"

    val options = listOf("Option 1", "Option 2", "Option 3")

    val menuText = choicesToText(decks.map(::getDeckName))

    val selectedDeckName = "fundies"

    val validIndices = 1..3
    val userInputn1 = "2"
    val userInputn2 = "4"
    val userInputn3 = "abc"

    val index1 = keepIfValid(userInputn1, validIndices)
    val index2 = keepIfValid(userInputn2, validIndices)
    val index3 = keepIfValid(userInputn3, validIndices)
    val deckOptions = listOf(deck1, deck2)
    val selectedDeck = chooseOption(deckOptions)
    val correctAnswers = studyDeck(selectedDeck)

    val deckName = getDeckName(selectedDeck)

    val announcement = renderChoice(selectedDeck)
    println(announcement)
    println("You selected: $deckName")
    println("Study session complete. You got $correctAnswers flashcards correct out of ${selectedDeck.flashCards.size}.")
}

fun transitionOptionChoice(decks: List<Deck>, ignoredState: Int, kbInput: String): Int {
    return keepIfValid(kbInput, decks.indices)
}

@EnabledTest()
fun testTransitionOptionChoice() {
    val deck1 = Deck("Deck 1", emptyList())
    val deck2 = Deck("Deck 2", emptyList())
    val decks = listOf(deck1, deck2)

    val validIndices = decks.indices

    val validInput = "1"
    val resultValid = transitionOptionChoice(decks, 0, validInput)
    testSame(resultValid, 0, "Valid input should return 0")

    val invalidInput = "3"
    val resultInValid = transitionOptionChoice(decks, 0, invalidInput)
    testSame(resultInValid, -1, "InValid input should return -1")

    val nonnumberInput = "lala"
    val resultNonNumber = transitionOptionChoice(decks, 0, nonnumberInput)
    testSame(resultInValid, -1, "Non-number input input should return -1")
}

fun chooseAndStudy(decks: List<Deck>, userInput: Int?) {
    if (userInput != null && userInput in 1..decks.size) {
        val selectedDeck = decks[userInput - 1]
        val correctAnswers = studyDeck(selectedDeck)
        println("Study session complete. You got $correctAnswers flashcards correct out of ${selectedDeck.flashCards.size}.")
    } else {
        println("Invalid choice. Please enter a valid number.")
    }
}

@EnabledTest()
fun testChooseAndStudyValidInput() {
    val deck1 = Deck("Deck 1", listOf(FlashCard("What mammal can fly?", "A Bat")))
    val deck2 = Deck("Deck 2", listOf(FlashCard("What cookie is vegan?", "Oreos")))
    val decks = listOf(deck1, deck2)

    chooseAndStudy(decks, 1)
}

@EnabledTest()
fun testChooseAndStudyInvalidInput() {
    val deck1 = Deck("Deck 1", listOf(FlashCard("What mammal can fly?", "A Bat")))
    val deck2 = Deck("Deck 2", listOf(FlashCard("What cookie is vegan?", "Oreos")))
    val decks = listOf(deck1, deck2)

    chooseAndStudy(decks, 0)
}

@EnabledTest()
fun testChooseAndStudyNonNumberInput() {
    val deck1 = Deck("Deck 1", listOf(FlashCard("What mammal can fly?", "A Bat")))
    val deck2 = Deck("Deck 2", listOf(FlashCard("What cookie is vegan?", "Oreos")))
    val decks = listOf(deck1, deck2)

    chooseAndStudy(decks, null)
}

@EnabledTest()
fun testChooseAndStudyOutOfRange() {
    val deck1 = Deck("Deck 1", listOf(FlashCard("What mammal can fly?", "A Bat")))
    val deck2 = Deck("Deck 2", listOf(FlashCard("What cookie is vegan?", "Oreos")))
    val decks = listOf(deck1, deck2)

    chooseAndStudy(decks, 3)
}

main()
runEnabledTests(this)

// -----------------------------------------------------------------
// Generating flash cards
// -----------------------------------------------------------------

// One benefit of digital flash cards is that sometimes we can
// use code to produce cards that match a known pattern without
// having to write all the fronts/backs by hand!
//

// TODO 1/1: Design the function perfectSquares that takes a
//           count (assumed to be positive) and produces the
//           list of flash cards that tests that number of the
//           first squares.
//
//           For example, the first three perfect squares...
//
//            1. front (1^2 = ?), back (1)
//            2. front (2^2 = ?), back (4)
//            3. front (3^2 = ?), back (9)
//
//           have been supplied as named values.
//
//           Hint: you might consider combining your
//                 kthPerfectSquare function from Homework 1
//                 with the list constructor in Homework 3.
//

// -----------------------------------------------------------------
// Files of cards
// -----------------------------------------------------------------

// each card is a line in the file, where the front comes first,
// separated by a "pipe" character ('|'), followed by the text
// on the back of the card.
//

// TODO 1/3: Design Fthe function cardToString that takes a flash
//           card as input and produces a string according to the
//           specification above ("front|back"). Make sure to
//           test all your card examples!
//

// TODO 2/3: Design the function stringToCard that takes a string,
//           assumed to be in the format described above, and
//           produces the corresponding flash card.
//
//           Hints:
//           - look back to how we extracted data from CSV
//             (comma-separated value) files (such as in
//             Homework 3)!
//           - a great way to test: for each of your card
//             examples, pass them through the function in TODO
//             1 to convert them to a string; then, pass that
//             result to this function... you *should* get your
//             original flash card back :)
//

// TODO 3/3: Design the function readCardsFile that takes a path
//           to a file and produces the corresponding list of
//           flash cards found in the file.
//
//           If the file does not exist, return an empty list.
//           Otherwise, you can assume that every line is
//           formatted in the string format we just worked with.
//
//           Hint:
//           - Think about how HW3-P1 effectively used an
//             abstraction to process all the lines in a
//             file assuming a known pattern.
//           - We've provided an "example.txt" file that you can
//             use for testing if you'd like; also make sure to
//             test your function when the supplied file does not
//             exist!
//

// -----------------------------------------------------------------
// Processing a self-report
// (Hint: see Homework 2)
// -----------------------------------------------------------------

// In our program, we will ask for a self-report as to whether
// the user got the correct answer for a card, SO...

// TODO 1/1: Finish designing the function isPositive that
//           determines if the supplied string starts with
//           the letter "y" (either upper or lowercase).
//
//           You've been supplied with a number of tests - make
//           sure you understand what they are doing!
//

// -----------------------------------------------------------------
// Choosing a deck from a menu
// -----------------------------------------------------------------

// Now let's work on providing a menu of decks from which a user
// can choose what they want to study.

// TODO 1/2: Finish design the function choicesToText that takes
//           a list of strings (assumed to be non-empty) and
//           produces the textual representation of a menu of
//           those options.
//
//           For example, given...
//
//           ["a", "b", "c"]
//
//           The menu would be...
//
//           "1. a
//            2. b
//            3. c
//
//            Enter your choice"
//
//            As you have probably guessed, this will be a key
//            piece of our rendering function :)
//
//            Hints:
//            - Think back to Homework 3 when we used a list
//              constructor to generate list elements based
//              upon an index.
//            - If you can produce a list of strings, the
//              linesToString function in the Khoury library
//              will bring them together into a single string.
//            - Make sure to understand the supplied tests!
//

// @EnabledTest
// fun testChoicesToText() {
//     val optA = "apple"
//     val optB = "banana"
//     val optC = "carrot"

//     testSame(
//         choicesToText(listOf(optA)),
//         linesToString(
//             "1. $optA",
//             "",
//             promptMenu,
//         ),
//         "one"
//     )

//     testSame(
//         choicesToText(listOf(optA, optB, optC)),
//         linesToString(
//             "1. $optA",
//             "2. $optB",
//             "3. $optC",
//             "",
//             promptMenu,
//         ),
//         "three"
//     )
// }

// TODO 2/2: Finish designing the program chooseOption that takes
//           a list of decks, produces a corresponding numbered
//           menu (1-# of decks, each showing its name), and
//           returns the deck corresponding to the number entered.
//           (Of course, keep displaying the menu until a valid
//           number is entered.)
//
//           Hints:
//            - Review the "Valid Number Example" of reactConsole
//              as one example of how to validate input. In this
//              case, however, since we know that we have a valid
//              range of integers, we can simplify the state
//              representation significantly :)
//            - To help you get started, the chooseOption function
//              has been written, but you must complete the helper
//              functions; look to the comments below for guidance.
//              You can then play "signature detective" to figure
//              out the parameters/return type of the functions you
//              need to write :)
//            - Lastly, as always, don't forget to sufficiently
//              test all the functions you write in this problem!
//

// // a program to allow the user to interactively select
// // a deck from the supplied, non-empty list of decks

//     // since the event handlers will need some info about
//     // the supplied decks, the functions inside
//     // chooseOption provide info about them while the
//     // parameter is in scope

//     // TODO: Above chooseOption, design the function
//     //       getDeckName, which returns the name of
//     //       a supplied deck.

//     // TODO: Above chooseOption, design the function
//     //       keepIfValid, that takes the typed input
//     //       as a string, as well as the valid
//     //       indices of the decks; note that the list indices
//     //       will be in the range [0, size), whereas the
//     //       user will see and work with [1, size].
//     //
//     //       If the user did not type a valid integer,
//     //       or not one in [1, size], return -1; otherwise
//     //       return the string converted to an integer, but
//     //       subtract 1, which makes it a valid list index.

//     // TODO: nothing, but understand this :)

//     // TODO: Above chooseOption, design the function
//     //       choiceAnnouncement that takes the selected
//     //       deck name and returns an announcement that
//     //       makes you happy. For a simple example, given
//     //       "fundies" as the chosen deck name, you might
//     //       return "you chose: fundies"

// -----------------------------------------------------------------
// Studying a deck
// -----------------------------------------------------------------

// Now let's design a program to allow a user to study through a
// supplied deck of flash cards.

// TODO 1/2: Design the data type StudyState to keep track of...
//           - which card you are currently studying in the deck
//           - are you looking at the front or back
//           - how many correct answers have been self-reported
//             thus far
//
//           Create sufficient examples so that you convince
//           yourself that you can represent any situation that
//           might arise when studying a deck.
//
//           Hints:
//           - Look back to the reactConsole problems in HW2 and
//             HW3; the former involved keeping track of a count
//             of loops (similar to the count of correct answers),
//             and the latter involved options for keeping track
//             of where you are in a list with reactConsole.
//

// TODO 2/2: Now, using reactConsole, design the program studyDeck
//           that for each card in a supplied deck, allows the
//           user to...
//
//           1. see the front (pause and think)
//           2. see the back
//           3. respond as to whether they got the answer
//
//           At the end, the user is told how many they self-
//           reported as correct (and this number is returned).
//
//           You have been supplied some prompts for steps #1
//           and #2 - feel free to change them if you'd like :)
//
//           Suggestions...
//           - Review the reactConsole videos/examples
//           - Start with studyDeck:
//             * write some tests to convince yourself you know
//               what your program is supposed to do!
//             * figure out how you'll create the initial state
//             * give names to the handlers you'll need
//             * how will you return the number correct?
//             * now comment-out this function, so that you can
//               design/test the handlers without interference :)
//           - For each handler...
//             * Play signature detective: based upon how it's
//               being used with reactConsole, what data will it
//               be given and what does it produce?
//             * Write some tests to convince yourself you know
//               its job.
//             * Write the code and don't move on till your tests
//               pass.
//            - Suggested ordering...
//              1. Am I done studying yet?
//              2. Rendering
//                 - It's a bit simpler to have a separate
//                   function for the terminal state.
//                 - The linesToString function is your friend to
//                   combine the card with the prompts.
//                 - Think about good decomposition when making
//                   the decision about front vs back content.
//              3. Transition
//                 - Start with the two main situations
//                   you'll find yourself in...
//                   > front->back
//                   > back->front
//                 - Then let a helper figure out how to handle
//                   the details of self-report
//
//            You've got this :-)
//

// -----------------------------------------------------------------
// Final app!
// -----------------------------------------------------------------

// Now you just get to put this all together 💃

// TODO 1/1: Design the function chooseAndStudy, where you'll
//           follow the comments in the supplied code to leverage
//           your prior work to allow the user to choose a deck,
//           study it, and return the number of correct self-
//           reports.
//
//           Your deck options MUST include at least one from each
//           of the following categories...
//
//           - Coded by hand (such as an example in data design)
//           - Read from a file (ala readCardsFile)
//           - Generated by code (ala perfectSquares)
//
//           Note: while this is an interactive program, you won't
//                 directly use reactConsole - instead, just call
//                 the programs you already designed above :)
//
//           And of course, don't forget to test at least two runs
//           of this completed program!
//
//           (And, consider adding this to main so you can see the
//           results of all your hard work so far this semester!)
//

// // lets the user choose a deck and study it,
// // returning the number self-reported correct

// -----------------------------------------------------------------

// runEnabledTests(this)
// main()
