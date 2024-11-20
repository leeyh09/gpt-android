package com.example.test

import android.graphics.Color
import kotlin.random.Random

data class Tetromino(
    var coordinates: MutableList<Pair<Int, Int>>,
    val color: Int
) {
    fun moveDown() {
        coordinates.forEachIndexed { index, (x, y) -> coordinates[index] = Pair(x, y + 1) }
    }

    fun moveLeft() {
        coordinates.forEachIndexed { index, (x, y) -> coordinates[index] = Pair(x - 1, y) }
    }

    fun moveRight() {
        coordinates.forEachIndexed { index, (x, y) -> coordinates[index] = Pair(x + 1, y) }
    }

    fun moveUp() {
        coordinates.forEachIndexed { index, (x, y) -> coordinates[index] = Pair(x, y - 1) }
    }

    fun rotate() {
        val center = coordinates[0]
        coordinates = coordinates.map { (x, y) ->
            val dx = x - center.first
            val dy = y - center.second
            Pair(center.first - dy, center.second + dx)
        }.toMutableList()
    }

    fun unrotate() {
        val center = coordinates[0]
        coordinates = coordinates.map { (x, y) ->
            val dx = x - center.first
            val dy = y - center.second
            Pair(center.first + dy, center.second - dx)
        }.toMutableList()
    }

    companion object {
        fun randomBlock(): Tetromino {
            val shapes = listOf(
                listOf(Pair(5, 0), Pair(6, 0), Pair(5, 1), Pair(6, 1)), // O
                listOf(Pair(4, 0), Pair(5, 0), Pair(6, 0), Pair(7, 0)), // I
                listOf(Pair(5, 0), Pair(5, 1), Pair(5, 2), Pair(6, 2))  // L
            )
            val randomShape = shapes[Random.nextInt(shapes.size)]
            return Tetromino(randomShape.toMutableList(), Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)))
        }
    }
}
