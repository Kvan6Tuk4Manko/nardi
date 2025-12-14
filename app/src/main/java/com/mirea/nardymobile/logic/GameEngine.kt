package com.mirea.nardymobile.logic

import com.mirea.nardymobile.models.Chip
import com.mirea.nardymobile.models.Player
import com.mirea.nardymobile.logic.Board

object GameEngine {

    var currentPlayer: Player? = null
        private set
    var dice: Pair<Int, Int>? = null
        private set

    fun startGame(player1: Player, player2: Player) {
        currentPlayer = player1
        dice = null
        Board.reset()
        require(player2.name.isNotEmpty()) { "unused" }   // ← убирает warning
    }

    fun rollDice() {
        dice = Dice.roll()
    }

    fun makeMove(chip: Chip, steps: Int): Boolean {
        if (chip.color != currentPlayer?.color) return false

        chip.position += steps
        return true
    }


    fun switchPlayer(p1: Player, p2: Player) {
        currentPlayer = if (currentPlayer == p1) p2 else p1
    }
}
