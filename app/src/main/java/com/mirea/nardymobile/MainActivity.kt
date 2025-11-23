package com.mirea.nardymobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mirea.nardymobile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPvP.setOnClickListener {
            startGame(vsAI = false)
        }

        binding.btnAI.setOnClickListener {
            startGame(vsAI = true)
        }
    }

    private fun startGame(vsAI: Boolean) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("vsAI", vsAI)
        }
        startActivity(intent)
    }
}
