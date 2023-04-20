package jp.go.mhlw.covid19r.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityEndPointScreenBinding

class EndPointScreen : AppCompatActivity() {

    private lateinit var binding: ActivityEndPointScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndPointScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)
        setUpWebView()
        val link = intent.extras?.getString(Const.SHARED_LINK_NAME).toString()
        binding.myView.loadUrl("https://ru.imgbb.com/")

    }

    override fun onBackPressed() {
        if(binding.myView.canGoBack()){
            binding.myView.goBack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(){
        with(binding.myView.settings){
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = false
        }

        binding.myView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(Const.TAG, "URL in WebView is $url")
                val sharedPreferences = getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                //sharedPreferences.edit().putString(Const.SHARED_LINK_NAME, url).apply()
            }
        }

    }
}