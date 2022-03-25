//Six dices used
val diceOne = Dice()
val diceTwo = Dice()
val diceThree = Dice()
val diceFour = Dice()
val diceFive = Dice()
val diceSix = Dice()

fun main() {
    println("Yahtzee")

    var counter = 0
    val gameSystem = System()
    val userOne = User("Guest")
    var userAnswer: String

    for (j in 1..5) {
        gameSystem.reset() //Reset game system before every round
        for (i in 1..3) {
            println("Write 'roll' to roll your dices!")
            do {
                userAnswer = readLine().toString().lowercase()
            }while(userAnswer != "roll")
            println("Rolling..")

            for (dice in gameSystem.dices) {
                userOne.rollDices(dice) //Dice roll
                println("Dice number " + gameSystem.diceCounter[counter] + ": " + dice.getValue())
                counter++
            }

            if (i != 3) {
                userOne.lockDice(gameSystem) //User chooses which dices to lock
                counter = 0
            }
            else { //Last roll add all unlocked dices to locked ones
                for (dice in gameSystem.dices) {
                    gameSystem.lockedDices.add(dice)
                    counter = 0
                }
            }
        }

        println("Dices you rolled are: ")
        for (dice in gameSystem.lockedDices) {
            print(dice.getValue())
            print(" ")
        }
        println()
        gameSystem.checkCombinations() //Check all combinations user rolled

        println("Choose your combination: ")
        for (combination in gameSystem.allCombinations) {
            println(combination)
        }

        userOne.chooseCombination(gameSystem) //User chooses the combination

        userOne.setUserScore(gameSystem.scoreCombination(userOne.getChosenCombination())) //The combination chosen by the user is calculated

        println("Total score: " + userOne.getUserScore()) //Total score achieved by the user
    }
}

class User(private val userName: String){
     private var totalScore: Int = 0
     private lateinit var diceToLock: String
     private lateinit var chosenCombination: String

    fun getUserScore(): Int{
        return totalScore
    }

    fun setUserScore(totalScore:Int){
        this.totalScore += totalScore
    }

    fun rollDices(diceToRoll: Dice){
        diceToRoll.roll()
    }

    fun chooseCombination(gameSystem: System){
        do {
            chosenCombination = readLine().toString()
        } while (!gameSystem.allCombinations.contains(chosenCombination))
    }

    fun getChosenCombination(): String{
        return chosenCombination
    }

    fun lockDice(gameSystem: System){
        do {
            println("Choose which dice to lock (Example: '1' to lock dice number one, 'exit' to stop locking dices):")
            diceToLock = readLine().toString()
            try {
                when (diceToLock.toInt()) {
                    1 -> gameSystem.lockDice(diceOne, 1)
                    2 -> gameSystem.lockDice(diceTwo, 2)
                    3 -> gameSystem.lockDice(diceThree, 3)
                    4 -> gameSystem.lockDice(diceFour, 4)
                    5 -> gameSystem.lockDice(diceFive, 5)
                    6 -> gameSystem.lockDice(diceSix, 6)
                }
            } catch (msg: NumberFormatException) { }
        } while (diceToLock != "exit")
    }
}

class Dice{
    private val number: List<Int> = listOf(1,2,3,4,5,6)
    private var value: Int = 0

    fun getValue(): Int { return value }

    fun roll(): Int{
        this.value = number.random()
        return this.value
    }
}

class System{
    var dices: MutableList<Dice> = mutableListOf(diceOne, diceTwo, diceThree, diceFour, diceFive, diceSix) //Dices used for the game
    val lockedDices: MutableList<Dice> = mutableListOf() //Dices locked by the user
    var diceCounter = intArrayOf(1, 2, 3, 4, 5, 6)

    var allCombinations: MutableList<String> = mutableListOf("Yahtzee", "Poker", "Tris", "Straight", "Chance") //All combinations possible
    private val combinations: MutableList<String> = mutableListOf() //Combinations which the user rolled
    private var score: Int = 0 //Score achieved by the user

    fun reset(){ //reset game every round
        this.dices = mutableListOf(diceOne, diceTwo, diceThree, diceFour, diceFive, diceSix)
        this.diceCounter = intArrayOf(1, 2, 3, 4, 5, 6)
        this.lockedDices.clear() //Clear locked dices before the next round
        this.combinations.clear() //Clear combinations user got before the next round
    }

    fun lockDice(dice: Dice, diceToLock: Int) {
        dices.remove(dice)
        lockedDices.add(dice)
        val temp = diceCounter.toMutableList()
        temp.remove(diceToLock)
        diceCounter = temp.toIntArray()
    }

    fun checkCombinations(){
        var checker = 0
        for(i in 1..6) {
            for (lockedDice in lockedDices) {
                if(lockedDice.getValue() == i) {
                    checker++
                }
            }
            if(checker > 4){
                combinations.add("Yahtzee")
            }
            if(checker > 3){
                combinations.add("Poker")
            }
            if(checker > 2){
                combinations.add("Tris")
            }
            checker = 0
        }

        val straight: MutableList<Int> = mutableListOf()
        for(lockedDice in lockedDices){
            if(!straight.contains(lockedDice.getValue())){
                checker++
                straight.add(lockedDice.getValue())
            }
        }
        if(checker > 4 && straight.contains(2) && straight.contains(3) && straight.contains(4) && straight.contains(5)){
            combinations.add("Straight") //Straight is achieved if the user rolled 5 different numbers, with 4 of those being 2,3,4,5
        }
        combinations.add("Chance") //Chance is always added as chance is just the sum of all numbers rolled
    }

    fun scoreCombination(combination: String): Int {
        var sum = 0
        when(combination){
            "Yahtzee" -> {
                allCombinations.remove("Yahtzee") //When a combination is used by the user it cannot be used again next round
                if(combinations.contains("Yahtzee")) {
                    score = 50
                    return score
                }
                else{
                    score = 0
                    return score
                }
            }
            "Poker" -> {
                allCombinations.remove("Poker")
                if(combinations.contains("Poker")) {
                    for (dice in lockedDices) {
                        sum += dice.getValue()
                    }
                    score = sum
                    return score
                }
                else{
                    score = 0
                    return score
                }
            }
            "Tris" -> {
                allCombinations.remove("Tris")
                if(combinations.contains("Tris")) {
                    for (dice in lockedDices) {
                        sum += dice.getValue()
                    }
                    score = sum
                    return score
                }
                else{
                    score = 0
                    return score
                }
            }
            "Straight" -> {
                allCombinations.remove("Straight")
                if(combinations.contains("Straight")) {
                    score = 40
                    return score
                }
                else{
                    score = 0
                    return score
                }
            }
            "Chance" -> {
                allCombinations.remove("Chance")
                if(combinations.contains("Chance")) {
                    for (dice in lockedDices) {
                        sum += dice.getValue()
                    }
                    score = sum
                    return score
                }
            }
        }
        return 0
    }
}