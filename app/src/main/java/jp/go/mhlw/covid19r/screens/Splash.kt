package jp.go.mhlw.covid19r.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import dalvik.system.DexClassLoader
import jp.go.mhlw.covid19r.Const
import jp.go.mhlw.covid19r.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch{
            delay(500)
            val intent =
                Intent(this@Splash,
                if (isNotModer()) MainActivity::class.java else GameCleopatra::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {

    }


    private fun isNotModer() : Boolean =
        Settings.Global.getString(contentResolver, Settings.Global.ADB_ENABLED) != "1"

}