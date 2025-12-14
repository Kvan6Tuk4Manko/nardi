package com.mirea.nardymobile.logic

import com.mirea.nardymobile.models.Chip
import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Move
import com.mirea.nardymobile.models.Player

object AI {

    fun makeMove(player: Player, dice: Pair<Int, Int>): Move? {
        if (player.color != ChipColor.BLACK) return null

        /* 1. собираем ВСЕ чёрные шашки через ваш IntArray */
        val blackChips = mutableListOf<Chip>()
        for (idx in 0 until Board.TOTAL_POINTS) {
            val cnt = Board.getPointState(idx)   // ваш метод
            if (cnt <= 0) continue               // чёрные = отрицательные
            repeat(cnt) { id ->
                blackChips += Chip(id, ChipColor.BLACK, idx)
            }
        }
        if (blackChips.isEmpty()) return null

        /* 2. случайный ход */
        val chip = blackChips.random()
        val steps = dice.first + dice.second
        return Move(chip, steps)
    }
}