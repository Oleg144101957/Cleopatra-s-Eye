package jp.go.mhlw.covid19r.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityMainBinding
import jp.go.mhlw.covid19r.vm.CleoVM

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var vm: CleoVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[CleoVM::class.java]
        vm.startViewModel(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)
        val pulsate = AnimationUtils.loadAnimation(this, R.anim.alpha_pulse)

        with(binding){
            img1.startAnimation(pulsate)
            img2.startAnimation(pulsate)
            img3.startAnimation(pulsate)
            img4.startAnimation(pulsate)
            img5.startAnimation(pulsate)
            img6.startAnimation(pulsate)
        }

        vm.mutableLiveLink.observe(this, Observer {
            if (it.toString() != "null"){
                Log.d(Const.TAG, "Link is ${vm.mutableLiveLink.value}")
                Log.d(Const.TAG, "it is $it")
                val intent = Intent(this, EndPointScreen::class.java)
                intent.putExtra(Const.SHARED_LINK_NAME, it)
                startActivity(intent)
            }
        })
    }

    override fun onBackPressed() {

    }

}