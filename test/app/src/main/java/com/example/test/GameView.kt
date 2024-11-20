package com.example.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val gameThread: GameThread
    private val blockSize = 60
    private val rows = 20
    private val cols = 10
    private val grid = Array(rows) { IntArray(cols) { 0 } }
    private val paint = Paint()

    private var score = 0
    private var scoreUpdateListener: ((Int) -> Unit)? = null
    private var currentBlock = Tetromino.randomBlock()

    init {
        // SurfaceHolder에 Callback 등록
        holder.addCallback(this)
        gameThread = GameThread(holder, this)
        isFocusable = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK)
        drawGrid(canvas)
        drawBlock(canvas, currentBlock)
        drawPlacedBlocks(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        paint.color = Color.GRAY
        for (i in 0..rows) {
            canvas.drawLine(0f, i * blockSize.toFloat(), (cols * blockSize).toFloat(), i * blockSize.toFloat(), paint)
        }
        for (j in 0..cols) {
            canvas.drawLine(j * blockSize.toFloat(), 0f, j * blockSize.toFloat(), (rows * blockSize).toFloat(), paint)
        }
    }

    private fun drawBlock(canvas: Canvas, block: Tetromino) {
        paint.color = block.color
        block.coordinates.forEach { (x, y) ->
            if (x in 0 until cols && y in 0 until rows) { // 경계 검사 추가
                val left = x * blockSize.toFloat()
                val top = y * blockSize.toFloat()
                canvas.drawRect(left, top, left + blockSize, top + blockSize, paint)
            }
        }
    }

    private fun drawPlacedBlocks(canvas: Canvas) {
        paint.color = Color.WHITE
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                if (grid[y][x] != 0) {
                    val left = x * blockSize.toFloat()
                    val top = y * blockSize.toFloat()
                    canvas.drawRect(left, top, left + blockSize, top + blockSize, paint)
                }
            }
        }
    }

    fun update() {
        currentBlock.moveDown()
        if (checkCollision()) {
            currentBlock.moveUp() // 충돌 시 원위치
            placeBlock()
            clearLines()
            currentBlock = Tetromino.randomBlock()

            if (checkGameOver()) {
                gameThread.running = false
                scoreUpdateListener?.invoke(-1) // 게임 종료 알림
            }
        }
    }

    private fun checkCollision(): Boolean {
        return currentBlock.coordinates.any { (x, y) ->
            y >= rows || x !in 0 until cols || (y >= 0 && grid[y][x] != 0)
        }
    }

    private fun placeBlock() {
        currentBlock.coordinates.forEach { (x, y) ->
            if (y in 0 until rows && x in 0 until cols) {
                grid[y][x] = currentBlock.color
            }
        }
    }

    private fun clearLines() {
        val fullLines = grid.indices.filter { row -> grid[row].all { it != 0 } }
        fullLines.forEach { row ->
            for (i in row downTo 1) {
                grid[i] = grid[i - 1].clone()
            }
            grid[0] = IntArray(cols)
        }
        score += fullLines.size * 10
        scoreUpdateListener?.invoke(score)
    }

    private fun checkGameOver(): Boolean {
        return grid[0].any { it != 0 }
    }

    fun setOnScoreUpdateListener(listener: (Int) -> Unit) {
        scoreUpdateListener = listener
    }

    fun moveBlockLeft() {
        currentBlock.moveLeft()
        if (checkCollision()) currentBlock.moveRight() // 충돌 시 되돌림
    }

    fun moveBlockRight() {
        currentBlock.moveRight()
        if (checkCollision()) currentBlock.moveLeft() // 충돌 시 되돌림
    }

    fun rotateBlock() {
        currentBlock.rotate()
        if (checkCollision()) currentBlock.unrotate() // 충돌 시 되돌림
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread.running = true
        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameThread.running = false
        gameThread.join()
    }
}
