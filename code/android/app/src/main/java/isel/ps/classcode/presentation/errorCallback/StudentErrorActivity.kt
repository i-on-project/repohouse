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
 * This will be used, when the user that is trying to log in classcode server is a pending teacher, that means that it don´t have access to this app
 */
class StudentErrorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            ClasscodeTheme {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                    Text(text = "You are a student, you can't access this application")
                    Button(onClick = { finish() }) {
                        Text(text = "press me")
                    }
                }
            }
        }
    }
}