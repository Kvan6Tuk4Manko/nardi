package com.mirea.nardymobile.logic

object Dice {
    private var die1: Int = 1
    private var die2: Int = 1
    var isDouble: Boolean = false
        private set

    fun roll(): Pair<Int, Int> = (1..6).random() to (1..6).random()
    fun isDouble(d: Pair<Int, Int>) = d.first == d.second
    fun values(d: Pair<Int, Int>) =
        if (isDouble(d)) List(4) { d.first } else listOf(d.first, d.second)

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