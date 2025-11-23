package com.mirea.nardymobile.models

enum class ChipColor {
    WHITE, BLACK
}

data class Chip(
    val id: Int,
    val color: ChipColor,
    var position: Int
)
