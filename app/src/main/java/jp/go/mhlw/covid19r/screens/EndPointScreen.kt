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
import android.provider.MediaStore
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityEndPointScreenBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val FCR = 1

class EndPointScreen : AppCompatActivity() {

    private lateinit var binding: ActivityEndPointScreenBinding
    private var mCM: String? = null
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndPointScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.black)
        setWebView()

    }

    override fun onBackPressed() {
        if(binding.myView.canGoBack()){
            binding.myView.goBack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        val webView = binding.myView
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess=true
        webView.settings.allowContentAccess=true
        webView.settings.javaScriptCanOpenWindowsAutomatically=true

        val link = intent.extras?.getString(Const.SHARED_LINK_NAME).toString()
        //https://ru.imgbb.com/

        webView.loadUrl("https://ru.imgbb.com/")
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (mUMA != null) {
                    mUMA!!.onReceiveValue(null)
                }
                mUMA = filePathCallback
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(getPackageManager()) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCM)
                    } catch (ex: IOException) {
                        Log.e("Webview", "Image file creation failed", ex)
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath()
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "*/*"
                val intentArray: Array<Intent>
                intentArray = takePictureIntent?.let { arrayOf(it) } ?: arrayOf<Intent>()
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, FCR)
                return true
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File? {
        @SuppressLint("SimpleDateFormat") val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun openFileChooser(
        uploadMsg: ValueCallback<Uri?>?,
        acceptType: String?
    ) {
        this.openFileChooser(uploadMsg, acceptType, null)
    }

    fun openFileChooser(
        uploadMsg: ValueCallback<Uri?>?,
        acceptType: String?,
        capture: String?
    ) {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "*/*"
        startActivityForResult(
            Intent.createChooser(i, "File Browser"),
            FCR
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri>? = null
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return
                    }
                    if (intent == null) { //Capture Photo if no image available
                        if (mCM != null) {
                            results = arrayOf(Uri.parse(mCM))
                        }
                    } else {
                        val dataString = intent.dataString
                        if (dataString != null) {
                            results = arrayOf(Uri.parse(dataString))
                        }
                    }
                }
            }
            mUMA!!.onReceiveValue(results)
            mUMA = null
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return
                val result =
                    if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                mUM!!.onReceiveValue(result)
                mUM = null
            }
        }
    }
}