package com.mirea.nardymobile.logic

import com.mirea.nardymobile.models.Chip
import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Move
import com.mirea.nardymobile.models.Player

object Board {

    const val TOTAL_POINTS = 24
    val track = Array(TOTAL_POINTS) { mutableListOf<Chip>() }

    private val points = IntArray(TOTAL_POINTS) { 0 }

    fun reset() {
        track.forEach { it.clear() }
        placeInitial(0, 2, ChipColor.WHITE)
        placeInitial(11, 5, ChipColor.WHITE)
        placeInitial(16, 3, ChipColor.WHITE)
        placeInitial(18, 5, ChipColor.WHITE)   // сумма = 15

        placeInitial(5, -5, ChipColor.BLACK)
        placeInitial(7, -3, ChipColor.BLACK)
        placeInitial(12, -5, ChipColor.BLACK)
        placeInitial(23, -2, ChipColor.BLACK)  // сумма = 15
    }

    private fun setupInitialPosition() { // ← правильное имя
        placeInitial(0, 2, ChipColor.WHITE)
        placeInitial(11, 5, ChipColor.WHITE)
        placeInitial(16, 3, ChipColor.WHITE)
        placeInitial(18, 5, ChipColor.WHITE)

        placeInitial(5, -5, ChipColor.BLACK)
        placeInitial(7, -3, ChipColor.BLACK)
        placeInitial(12, -5, ChipColor.BLACK)
        placeInitial(23, -2, ChipColor.BLACK)
    }

    private fun placeInitial(point: Int, count: Int, color: ChipColor) {
        repeat(kotlin.math.abs(count)) { id ->
            track[point].add(Chip(id, color, point))
        }
    }


    init {
        setupInitialPosition()
        syncTrackFromPoints()
    }

    fun getPointState(index: Int): Int = points.getOrElse(index) { 0 }

    fun isValidMove(from: Int, to: Int, player: Player): Boolean {
        if (from !in 0 until TOTAL_POINTS || to !in 0 until TOTAL_POINTS) return false
        val dir = if (player.color == ChipColor.WHITE) 1 else -1
        if (points[from] * dir <= 0) return false
        if (points[to] * dir < -1) return false
        return true
    }

    fun moveChecker(from: Int, to: Int, chip: Chip, player: Player) {
        if (!isValidMove(from, to, player)) return
        track[from].remove(chip)
        chip.position = to
        track[to].add(chip)
    }

    fun getPossibleMoves(player: Player, diceValues: List<Int>): List<Int> {
        val res = mutableSetOf<Int>()
        val dir = if (player.color == ChipColor.WHITE) 1 else -1
        for (d in diceValues) {
            for (from in 0 until TOTAL_POINTS) {
                val to = from + d * dir
                if (to in 0 until TOTAL_POINTS && track[from].any { it.color == player.color }) {
                    res += to
                }
            }
        }
        return res.toList()
    }

    fun getPossibleMovesForChip(fromPoint: Int, player: Player, diceValues: List<Int>): List<Int> {
        val res = mutableSetOf<Int>()
        val dir = if (player.color == ChipColor.WHITE) 1 else -1
        for (d in diceValues) {
            val to = fromPoint + d * dir
            if (to in 0 until TOTAL_POINTS && isValidMove(fromPoint, to, player)) {
                res += to
            }
        }
        return res.toList()
    }

    /* ---------- синхронизация IntArray -> List<Chip> ---------- */
    private fun syncTrackFromPoints() {
        track.forEach { it.clear() }
        for (idx in points.indices) {
            val count = points[idx]
            if (count == 0) continue
            val color = if (count > 0) ChipColor.WHITE else ChipColor.BLACK
            repeat(kotlin.math.abs(count)) {
                track[idx].add(Chip(it, color, idx))
            }
        }
    }
}