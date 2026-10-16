package com.laura.superheroes_android.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.laura.superheroes_android.R
import com.laura.superheroes_android.adapters.ScreenshotAdapter
import com.laura.superheroes_android.data.FreeToGameApi
import com.laura.superheroes_android.data.Game
import com.laura.superheroes_android.databinding.ActivityDetailBinding
import com.laura.superheroes_android.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {


    companion object{
        const val EXTRA_GAME_ID = "GAME_ID"
    }


    lateinit var binding: ActivityDetailBinding

    lateinit var  game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra("GAME_ID",-1)


        val api = FreeToGameApi.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            game = api.getGameById(id)
            CoroutineScope(Dispatchers.Main).launch {
                loadData()

            }

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    fun loadData(){
        supportActionBar?.title = game.title
        supportActionBar?.subtitle = game.developer

        // Hero
        Picasso.get().load(game.thumbnail).into(binding.thumbnailImageView)

        // Basic info
        binding.titleTextView.text = game.title
        binding.genreChip.text = game.genre

        val platformResId = if (game.platform.contains("Windows")) {
            R.drawable.ic_platform
        } else {
            R.drawable.ic_web
        }
        binding.platformImageView.setImageResource(platformResId)

        // Description
        binding.descriptionTextView.text = game.description

        // Screenshots
        val adapter = ScreenshotAdapter(game.screenshots ?: emptyList())
        binding.screenshotsRecyclerView.adapter = adapter

        // System requirements
        binding.systemRequirementsOsTextView.text = game.systemRequirements?.os
        binding.systemRequirementsProcessorTextView.text = game.systemRequirements?.processor
        binding.systemRequirementsMemoryTextView.text = game.systemRequirements?.memory
        binding.systemRequirementsGraphicsTextView.text = game.systemRequirements?.graphics
        binding.systemRequirementsStorageTextView.text = game.systemRequirements?.storage

        // Buttons
        binding.playButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(game.gameUrl.toUri())
            startActivity(intent)
        }
        binding.shareButton.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Mira que juego está gratis: ${game.profileUrl}.")
            sendIntent.type = "text/plain"

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}