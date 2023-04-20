package jp.go.mhlw.covid19r.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import jp.go.mhlw.covid19r.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch{
            delay(500)
            val intent =
                Intent(this@Splash,
                if (isNotModer()) MainActivity::class.java else MainActivity::class.java
                    )
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {

    }


    private fun isNotModer() : Boolean =
        Settings.Global.getString(contentResolver, Settings.Global.ADB_ENABLED) != "1"



}