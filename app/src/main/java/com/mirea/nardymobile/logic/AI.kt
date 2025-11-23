package com.mirea.nardymobile.logic

import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Move
import com.mirea.nardymobile.models.Player

object AI {

    fun makeMove(player: Player, dice: Pair<Int, Int>): Move? {
        if (player.color != ChipColor.BLACK) return null

        val all = Board.track.flatten().filter { it.color == ChipColor.BLACK }
        if (all.isEmpty()) return null

        val chip = all.random()
        val steps = dice.first + dice.second
        return Move(chip, steps)
    }
}
