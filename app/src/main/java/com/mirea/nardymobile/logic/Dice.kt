package com.rtumirea.nardi.gamelogic


class Dice {
    private var die1: Int = 1
    private var die2: Int = 1
    var isDouble: Boolean = false
        private set

    fun roll(): Pair<Int, Int> {
        die1 = (1..6).random()
        die2 = (1..6).random()
        isDouble = (die1 == die2)
        return Pair(die1, die2)
    }

    fun getCurrentDice(): Pair<Int, Int> = Pair(die1, die2)

    fun getPossibleMoves(): List<Int> {
        return if (isDouble) {
            List(4) { die1 } // 4 одинаковых хода для дубля
        } else {
            listOf(die1, die2)
        }
    }

    fun reset() {
        die1 = 1
        die2 = 1
        isDouble = false
    }
}
