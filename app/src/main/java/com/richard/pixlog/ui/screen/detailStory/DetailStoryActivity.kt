package com.richard.pixlog.ui.screen.detailStory

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.richard.pixlog.R
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.databinding.ActivityDetailStoryBinding
import com.richard.pixlog.utils.formatTimeAgo
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat

class DetailStoryActivity : AppCompatActivity() {
    private var _binding : ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupHeader()
        setupData()

    }

    @Suppress("DEPRECATION")
    private fun setupData(){
        val story = intent.getParcelableExtra<ListStory>("Story") as ListStory
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivAvatar)
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.ivContent)
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
        binding.tvLastUpdate.text = formatTimeAgo(story.createdAt)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupHeader() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.purple_40))
        val upArrow = AppCompatResources.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        if (upArrow != null) {
            val wrapped = DrawableCompat.wrap(upArrow)
            DrawableCompat.setTint(wrapped, Color.WHITE)
            supportActionBar?.setHomeAsUpIndicator(wrapped)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}