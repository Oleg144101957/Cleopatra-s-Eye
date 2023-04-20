package jp.go.mhlw.covid19r.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityGameCleopatraBinding
import kotlin.random.Random

class GameCleopatra : AppCompatActivity() {

    private lateinit var binding: ActivityGameCleopatraBinding
    private val imagesList = listOf(
        R.drawable.element_1_927,
        R.drawable.element_2_927,
        R.drawable.element_3_927,
        R.drawable.element_4_927,
        R.drawable.element_5_927,
        R.drawable.element_6_927
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameCleopatraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.black, theme)

        binding.btnPlay.setOnClickListener {
            play()
        }

        val pulsate = AnimationUtils.loadAnimation(this, R.anim.alpha_pulse)
        with(binding){
            img1.startAnimation(pulsate)
            img2.startAnimation(pulsate)
            img3.startAnimation(pulsate)
            img4.startAnimation(pulsate)
            img5.startAnimation(pulsate)
            img6.startAnimation(pulsate)
            titleTV.startAnimation(pulsate)
        }
    }

    private fun play(){
        with(binding){
            img1.setImageResource(imagesList[Random.nextInt(0,5)])
            img2.setImageResource(imagesList[Random.nextInt(0,5)])
            img3.setImageResource(imagesList[Random.nextInt(0,5)])
            img4.setImageResource(imagesList[Random.nextInt(0,5)])
            img5.setImageResource(imagesList[Random.nextInt(0,5)])
            img6.setImageResource(imagesList[Random.nextInt(0,5)])
        }
    }
}