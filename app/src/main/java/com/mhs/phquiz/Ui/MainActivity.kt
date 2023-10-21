package com.mhs.phquiz.Ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mhs.phquiz.Utils.Constants
import com.mhs.phquiz.Utils.PreferenceManager
import com.mhs.phquiz.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var preferenceManager: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, QuesAnsActivity::class.java))
            finish()
        }

        val highScoreFromPreferences = preferenceManager?.getInt(Constants.HIGHSCORE) ?: 0
        binding.txtScoreValue.text = "$highScoreFromPreferences points"
    }
}