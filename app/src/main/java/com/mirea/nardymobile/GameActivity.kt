package com.mirea.nardymobile

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mirea.nardymobile.databinding.ActivityGameBinding
import com.mirea.nardymobile.logic.*
import com.mirea.nardymobile.models.Chip
import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private val player1 = Player("Белые", ChipColor.WHITE)
    private val player2 = Player("Чёрные", ChipColor.BLACK)

    private val pointViews = arrayOfNulls<LinearLayout>(Board.TRACK_SIZE)
    private val chipViews = mutableMapOf<Int, ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Нарды"

        inflateBoard()
        observeViewModel()
        binding.rollDiceButton.setOnClickListener { onRollClick() }
        viewModel.onNewGame()
    }

    private fun inflateBoard() {
        val boardContainer = binding.boardContainer
        boardContainer.removeAllViews()

        val side = resources.displayMetrics.densityDpi / 160f
        val pointWidth = (resources.displayMetrics.widthPixels / 12) - (4 * side).toInt()

        for (i in 0 until Board.TRACK_SIZE) {
            val point = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(pointWidth, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    setMargins(2, 0, 2, 0)
                }
                setBackgroundResource(
                    if ((i / 6 + i) % 2 == 0) R.drawable.point_brown else R.drawable.point_beige
                )
                gravity = Gravity.CENTER_HORIZONTAL
            }
            boardContainer.addView(point)
            pointViews[i] = point
        }
    }

    private fun observeViewModel() {
        viewModel.dice.observe(this) { d ->
            binding.diceText.text = "${d.first} : ${d.second}"
        }
        viewModel.currentPlayer.observe(this) { p ->
            binding.currentPlayerText.text = "Ход: ${p.name}"
        }
        viewModel.board.observe(this) { renderChips() }
    }

    private fun onRollClick() {
        if (GameEngine.currentPlayer == null) return
        viewModel.rollDice()
        GameEngine.rollDice()

        val vsAI = intent.getBooleanExtra("vsAI", true)
        if (GameEngine.currentPlayer == player2 && vsAI) {
            binding.rollDiceButton.isEnabled = false
            lifecycleScope.launch { aiTurn() }
        }
    }

    private suspend fun aiTurn() {
        delay(600)
        val dice = GameEngine.dice ?: return
        val move = AI.makeMove(player2, dice)
        if (move != null) {
            GameEngine.makeMove(move.chip, move.steps)
            viewModel.onMoveDone()
        }
        binding.rollDiceButton.isEnabled = true
    }

    private fun renderChips() {
        chipViews.values.forEach { (it.parent as? ViewGroup)?.removeView(it) }
        chipViews.clear()

        for (i in 0 until Board.TRACK_SIZE) {
            val stack = Board.track[i]
            val point = pointViews[i] ?: continue
            stack.forEachIndexed { index, chip ->
                val iv = ImageView(this).apply {
                    setImageResource(
                        if (chip.color == ChipColor.WHITE) R.drawable.chip_white
                        else R.drawable.chip_black
                    )
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        resources.getDimensionPixelSize(R.dimen.chip_height)
                    ).apply { topMargin = index * 6 }
                }
                point.addView(iv)
                chipViews[chip.id] = iv
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
