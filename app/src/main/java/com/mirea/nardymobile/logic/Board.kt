package com.mirea.nardymobile.logic

import com.mirea.nardymobile.models.Chip

object Board {
    const val TRACK_SIZE = 24

    val track = Array(TRACK_SIZE) { mutableListOf<Chip>() }

    fun reset() = track.forEach { it.clear() }
}
