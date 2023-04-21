package jp.go.mhlw.covid19r.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityEndPointScreenBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class EndPointScreen : AppCompatActivity() {

    private lateinit var binding: ActivityEndPointScreenBinding
    private lateinit var chooseCallback: ValueCallback<Array<Uri?>>
    val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){
        chooseCallback.onReceiveValue(it.toTypedArray())
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndPointScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)
        setWebClicks(binding.myView)

        val link = intent.extras?.getString(Const.SHARED_LINK_NAME).toString()
        binding.myView.loadUrl(link)

        val intent = Intent(this, GameCleopatra::class.java)
        val sharedPreferences = getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE)


        binding.myView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url == "https://cleopatraseye.live/"){
                    startActivity(intent)
                } else {
                    sharedPreferences.edit().putString(Const.SHARED_LINK_NAME, url).apply()
                }
                Log.d(Const.TAG, "The final URL is $url")
            }
        }

        with(binding.myView.settings){
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = false
            userAgentString = binding.myView.settings.userAgentString.replace("wv", "")
        }

        binding.myView.webChromeClient = object : WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                chooseCallback = filePathCallback
                getContent.launch("image/*")
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val createdWV = WebView(this@EndPointScreen)
                with(createdWV.settings){
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    domStorageEnabled = true
                    setSupportMultipleWindows(true)
                }
                createdWV.webChromeClient = this
                val trans = resultMsg.obj as WebView.WebViewTransport
                trans.webView = createdWV
                resultMsg.sendToTarget()
                return true
            }
        }
    }

    private fun setWebClicks(webview : WebView){
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webview.canGoBack()) {
                        webview.goBack()
                    }
                }
            })
    }
}