package com.mirea.nardymobile

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mirea.nardymobile.databinding.ActivityGameBinding
import com.mirea.nardymobile.logic.*
import com.mirea.nardymobile.models.ChipColor
import com.mirea.nardymobile.models.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.mirea.nardymobile.models.Chip

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()

    private val playerBlack = Player("Чёрные", ChipColor.BLACK)

    private val pointViews = arrayOfNulls<LinearLayout>(Board.TOTAL_POINTS)
    private val chipViews = mutableMapOf<Int, ImageView>()

    private var selectedPoint: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarGame)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        inflateBoard()
        observeViewModel()
        binding.rollDiceButton.setOnClickListener { onRollClick() }
        viewModel.onNewGame()
    }

    private fun renderChips() {
        chipViews.values.forEach { (it.parent as? ViewGroup)?.removeView(it) }
        chipViews.clear()

        for (i in 0 until Board.TOTAL_POINTS) {
            val point = pointViews[i] ?: continue
            val stack = Board.track[i]
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

                    /* ← делаем шашку кликабельной */
                    isClickable = true
                    setOnClickListener { onChipClicked(chip, i) }
                }
                point.addView(iv)
                chipViews[chip.id] = iv
            }
        }
    }


    private fun inflateBoard() {
        val topRow = binding.topRow
        val bottomRow = binding.bottomRow
        topRow.removeAllViews()
        bottomRow.removeAllViews()
        val boardContainer = binding.boardContainer
        boardContainer.removeAllViews()

        val side = resources.displayMetrics.densityDpi / 160f
        val pointWidth = (resources.displayMetrics.widthPixels / 12) - (4 * side).toInt()

        /* верхний ряд (точки 12-23) */
        for (i in 12 until Board.TOTAL_POINTS) {
            val point = createPoint(i, pointWidth)
            topRow.addView(point)
            pointViews[i] = point
        }

        /* нижний ряд (точки 0-11) — в **обратном** порядке, чтобы «дом» был справа */
        for (i in 11 downTo 0) {
            val point = createPoint(i, pointWidth)
            bottomRow.addView(point)
            pointViews[i] = point
        }

    }private fun onPointClicked(clicked: Int) {
        val player = GameEngine.currentPlayer ?: return

        /* клик на подсвеченную точку → двигаем */
        if (selectedPoint != null && clicked in (viewModel.availableMoves.value ?: emptyList())) {
            makeMove(selectedPoint!!, clicked)
            selectedPoint = null
            clearHighlight()
        }
    }

    private fun observeViewModel() {
        viewModel.dice.observe(this) { d ->
            binding.diceText.text = getString(R.string.dice_format, d.first, d.second)
        }
        viewModel.currentPlayer.observe(this) { p ->
            binding.currentPlayerText.text = getString(R.string.turn_format, p.name)
        }
        viewModel.board.observe(this) { renderChips() }

        /* ↓ подсветка возможных ходов */
        viewModel.availableMoves.observe(this) { moves ->
            pointViews.forEachIndexed { i, v -> v?.setBackgroundColor(
                    if (i in moves) ContextCompat.getColor(this, R.color.move_highlight)
                    else ContextCompat.getColor(this, R.color.point_default)
                )
            }
        }
    }

    private fun createPoint(index: Int, width: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                setMargins(2, 0, 2, 0)
            }
            setBackgroundResource(if ((index / 6 + index) % 2 == 0) R.drawable.point_brown else R.drawable.point_beige)
            gravity = if (index < 12) Gravity.BOTTOM else Gravity.TOP
        }
    }
    private fun onRollClick() {
        if (GameEngine.currentPlayer == null) return
        viewModel.rollDice()
        GameEngine.rollDice()

        val vsAI = intent.getBooleanExtra("vsAI", true)
        if (GameEngine.currentPlayer == playerBlack && vsAI) {
            binding.rollDiceButton.isEnabled = false
            lifecycleScope.launch { aiTurn() }
        }
    }

    private fun makeMove(from: Int, to: Int) {
        val chip = Board.track[from].firstOrNull { it.color == GameEngine.currentPlayer?.color } ?: return
        Board.moveChecker(from, to, chip, GameEngine.currentPlayer!!)
        viewModel.onMoveDone()   // → trigger → renderChips()
    }

    private fun onChipClicked(chip: Chip, pointIndex: Int) {
        val player = GameEngine.currentPlayer ?: return

        /* кликнули НЕ свою → ничего не делаем */
        if (chip.color != player.color) return

        /* выбираем шашку + показываем ходы именно для неё */
        selectedPoint = pointIndex
        highlightSelected(pointIndex)

        val dice = Dice.values(viewModel.dice.value ?: return)
        val moves = Board.getPossibleMovesForChip(pointIndex, player, dice)
        viewModel.onChipSelected(pointIndex, moves)
    }

    private fun highlightSelected(point: Int) {
        pointViews.forEachIndexed { i, v -> v?.isSelected = (i == point) }
    }
    private fun clearHighlight() {
        pointViews.forEach { it?.isSelected = false }
    }

    private suspend fun aiTurn() {
        delay(600)
        val dice = GameEngine.dice ?: return
        val move = AI.makeMove(playerBlack, dice)
        if (move != null) {
            GameEngine.makeMove(move.chip, move.steps)
            viewModel.onMoveDone()
        }
        binding.rollDiceButton.isEnabled = true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
