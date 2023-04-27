package jp.go.mhlw.covid19r.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityGameCleopatraBinding
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.random.Random

class GameCleopatra : AppCompatActivity() {

    private lateinit var binding: ActivityGameCleopatraBinding
    private val imagesList = listOf(
        R.drawable.element_1_927,
        R.drawable.element_2_927,
        R.drawable.element_3_927,
        R.drawable.element_4_927,
        R.drawable.element_5_927,
    )

    private val mapOfChoices = mutableMapOf<String, Int>()
    private val listOfChoices = mutableListOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameCleopatraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initScreen()
        window.statusBarColor = resources.getColor(R.color.black, theme)

        binding.btnPlay.setOnClickListener {
            play()
        }

        binding.btnChoose.setOnClickListener{
            checkIfWinner()
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

        binding.img1.setOnClickListener {
            binding.img1.clearAnimation()
            val choice: Int = mapOfChoices["img1"]!!
            listOfChoices.add(choice)
        }

        binding.img2.setOnClickListener {
            binding.img2.clearAnimation()
            val choice: Int = mapOfChoices["img2"]!!
            listOfChoices.add(choice)
        }

        binding.img3.setOnClickListener {
            binding.img3.clearAnimation()
            val choice: Int = mapOfChoices["img3"]!!
            listOfChoices.add(choice)
        }

        binding.img4.setOnClickListener {
            binding.img4.clearAnimation()
            val choice: Int = mapOfChoices["img4"]!!
            listOfChoices.add(choice)
        }


        binding.img5.setOnClickListener {
            binding.img5.clearAnimation()
            val choice: Int = mapOfChoices["img5"]!!
            listOfChoices.add(choice)
        }

        binding.img6.setOnClickListener {
            binding.img6.clearAnimation()
            val choice: Int = mapOfChoices["img6"]!!
            listOfChoices.add(choice)
        }
    }

    private fun play(){

        val img1randomNumb = Random.nextInt(0,4)
        val img2randomNumb = Random.nextInt(0,4)
        val img3randomNumb = Random.nextInt(0,4)
        val img4randomNumb = Random.nextInt(0,4)
        val img5randomNumb = Random.nextInt(0,4)
        val img6randomNumb = Random.nextInt(0,4)

        mapOfChoices["img1"] = img1randomNumb
        mapOfChoices["img2"] = img2randomNumb
        mapOfChoices["img3"] = img3randomNumb
        mapOfChoices["img4"] = img4randomNumb
        mapOfChoices["img5"] = img5randomNumb
        mapOfChoices["img6"] = img6randomNumb

        with(binding){
            img1.setImageResource(imagesList[img1randomNumb])
            img2.setImageResource(imagesList[img2randomNumb])
            img3.setImageResource(imagesList[img3randomNumb])
            img4.setImageResource(imagesList[img4randomNumb])
            img5.setImageResource(imagesList[img5randomNumb])
            img6.setImageResource(imagesList[img6randomNumb])
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


    private fun checkIfWinner(){
        if (listOfChoices.size > 2){
            Toast.makeText(this, "You cah choose only 2 items", Toast.LENGTH_LONG).show()
        } else if (listOfChoices.size < 2){
            Toast.makeText(this, "Select one more item", Toast.LENGTH_LONG).show()
        } else if (listOfChoices[0]==listOfChoices[1]){
            Toast.makeText(this, "You are winner !!!", Toast.LENGTH_LONG).show()
            increaseScore()
        } else {
            Toast.makeText(this, "Choose the same pictures", Toast.LENGTH_LONG).show()
        }
        listOfChoices.clear()

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

    private fun increaseScore() {
        val sharedPref = getSharedPreferences(Const.SHARED_PREF_NAME, MODE_PRIVATE)
        var score = sharedPref.getInt(Const.SHARED_SCORE_NAME, 0)
        sharedPref.edit().putInt(Const.SHARED_SCORE_NAME, ++score).apply()
        binding.scoreTitleValue.text = score.toString()
    }

    private fun setScoreFromSharedPref(){
        val sharedPref = getSharedPreferences(Const.SHARED_PREF_NAME, MODE_PRIVATE)
        val score = sharedPref.getInt(Const.SHARED_SCORE_NAME, 0)
        binding.scoreTitleValue.text = score.toString()
    }

    private fun initScreen(){
        play()
        setScoreFromSharedPref()
    }
}