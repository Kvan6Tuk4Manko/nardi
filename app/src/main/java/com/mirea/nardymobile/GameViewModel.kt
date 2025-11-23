package com.mirea.nardymobile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mirea.nardymobile.logic.Board
import com.mirea.nardymobile.logic.Dice
import com.mirea.nardymobile.logic.GameEngine
import com.mirea.nardymobile.models.Chip
import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Player

class GameViewModel : ViewModel() {

    private val p1 = Player("Белые", ChipColor.WHITE)
    private val p2 = Player("Чёрные", ChipColor.BLACK)

    private val _dice = MutableLiveData<Pair<Int, Int>>()
    val dice: LiveData<Pair<Int, Int>> = _dice

    private val _board = MutableLiveData<Array<MutableList<Chip>>>()
    val board: LiveData<Array<MutableList<Chip>>> = _board

    private val _currentPlayer = MutableLiveData<Player>()
    val currentPlayer: LiveData<Player> = _currentPlayer

    fun onNewGame() {
        GameEngine.startGame(p1, p2)
        _currentPlayer.value = p1
        _board.value = Board.track
    }

    fun rollDice() {
        val d = Dice.roll()
        _dice.value = d
        GameEngine.rollDice()
    }

    fun onMoveDone() {
        GameEngine.switchPlayer(p1, p2)
        _currentPlayer.value = GameEngine.currentPlayer
        _board.value = Board.track
    }
}
