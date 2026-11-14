package land.noah.smartcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import land.noah.smartcafe.ui.screens.LoginScreen
import land.noah.smartcafe.ui.theme.SmartCafeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartCafeTheme {
                LoginScreen()
            }
        }
    }
}