package isel.ps.classcode.presentation.credits

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class CreditsActivity : ComponentActivity() {
    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, CreditsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}