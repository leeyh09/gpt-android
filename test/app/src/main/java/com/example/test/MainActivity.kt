package com.example.test

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        scoreText = findViewById(R.id.scoreText)

        // 좌/우/회전 버튼
        findViewById<Button>(R.id.btnLeft).setOnClickListener { gameView.moveBlockLeft() }
        findViewById<Button>(R.id.btnRight).setOnClickListener { gameView.moveBlockRight() }
        findViewById<Button>(R.id.btnRotate).setOnClickListener { gameView.rotateBlock() }

        // 점수 업데이트 리스너 설정
        gameView.setOnScoreUpdateListener { score ->
            scoreText.text = "Score: $score"
        }
    }
}
