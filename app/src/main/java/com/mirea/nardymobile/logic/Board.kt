package com.rtumirea.nardi.gamelogic

class Board {
    companion object {
        const val TOTAL_POINTS = 24
        const val TOTAL_CHECKERS = 15
    }

    // Позиции на доске: индекс - номер пункта, значение - количество шашек
    // Положительные значения - белые, отрицательные - черные
    private val points = IntArray(TOTAL_POINTS) { 0 }

    // Сбитые шашки
    private var whiteHitCheckers = 0
    private var blackHitCheckers = 0

    // Выведенные шашки
    private var whiteBornOff = 0
    private var blackBornOff = 0

    init {
        setupInitialPosition()
    }

    private fun setupInitialPosition() {
        // Очищаем доску
        points.fill(0)

        // Стандартная начальная расстановка для длинных нардов
        points[0] = 2   // Белые
        points[5] = -5  // Черные
        points[7] = -3  // Черные
        points[11] = 5  // Белые
        points[12] = -5 // Черные
        points[16] = 3  // Белые
        points[18] = 5  // Белые
        points[23] = -2 // Черные
    }


     //Получить состояние пункта

    fun getPointState(pointIndex: Int): Int {
        require(pointIndex in 0 until TOTAL_POINTS) { "Неверный индекс пункта" }
        return points[pointIndex]
    }

     //Проверка, можно ли выставлять шашки с бара
    fun canEnterFromBar(player: Player): Boolean {
        val targetPoints = if (player.color == PlayerColor.WHITE) {
            (0..5).toList() // Белые входят в пункты 0-5
        } else {
            (18..23).toList() // Черные входят в пункты 18-23
        }

        return targetPoints.any { points[it] >= 0 }
    }

     //Перемещение шашки
    fun moveChecker(from: Int, to: Int, player: Player): Boolean {
        if (!isValidMove(from, to, player)) {
            return false
        }

        val moveDirection = if (player.color == PlayerColor.WHITE) 1 else -1
        val actualTo = if (to >= TOTAL_POINTS) TOTAL_POINTS - 1 else to

        // Если сбиваем шашку противника
        if (points[actualTo] * moveDirection < 0) {
            if (player.color == PlayerColor.WHITE) {
                blackHitCheckers++
            } else {
                whiteHitCheckers++
            }
            points[actualTo] = 0
        }

        // Перемещаем шашку
        points[from] -= moveDirection
        points[actualTo] += moveDirection

        return true
    }

     //Проверка валидности хода

    fun isValidMove(from: Int, to: Int, player: Player): Boolean {
        if (from !in 0 until TOTAL_POINTS || to !in 0 until TOTAL_POINTS) {
            return false
        }

        val moveDirection = if (player.color == PlayerColor.WHITE) 1 else -1

        // Проверка владения шашкой
        if (points[from] * moveDirection <= 0) {
            return false
        }

        // Проверка направления движения
        if ((player.color == PlayerColor.WHITE && to <= from) ||
            (player.color == PlayerColor.BLACK && to >= from)) {
            return false
        }

        // Проверка пункта назначения (нельзя ставить на пункт с 2+ шашками противника)
        if (points[to] * moveDirection < -1) {
            return false
        }

        return true
    }

    //Получить все возможные ходы для игрока
    fun getPossibleMoves(player: Player, diceValues: List<Int>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        val moveDirection = if (player.color == PlayerColor.WHITE) 1 else -1

        for (from in 0 until TOTAL_POINTS) {
            if (points[from] * moveDirection > 0) { // Есть наши шашки
                for (dice in diceValues) {
                    val to = if (player.color == PlayerColor.WHITE) from + dice else from - dice
                    if (isValidMove(from, to, player)) {
                        moves.add(Pair(from, to))
                    }
                }
            }
        }

        return moves
    }


     //Проверка, можно ли начать вывод шашек

    fun canBearOff(player: Player): Boolean {
        val homePoints = if (player.color == PlayerColor.WHITE) {
            (18..23).toList()
        } else {
            (0..5).toList()
        }

        return homePoints.all { points[it] * (if (player.color == PlayerColor.WHITE) 1 else -1) >= 0 }
    }

    fun getBoardState(): List<Int> = points.toList()
}
