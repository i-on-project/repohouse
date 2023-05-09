package isel.ps.classcode.presentation.errorCallback

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import isel.ps.classcode.ui.theme.ClasscodeTheme

/**
 * The user that is trying to log in classcode server is not present in the classcode
 */
class UserNotFoundErrorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            ClasscodeTheme {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    Text(text = "User not present in classcode")
                    Button(onClick = { finish() }) {
                        Text(text = "press me")
                    }
                }
            }
        }
    }
}
