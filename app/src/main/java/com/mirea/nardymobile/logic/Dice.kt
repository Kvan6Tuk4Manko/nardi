package com.mirea.nardymobile.logic

object Dice {
    fun roll(): Pair<Int, Int> = (1..6).random() to (1..6).random()
}
