package land.noah.smartcafe.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Smart Café")

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                loginError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(it)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                loginError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                passwordError?.let {
                    Text(it)
                }
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                emailError = when {
                    email.isBlank() -> "Email is required"
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        "Enter a valid email address"
                    else -> null
                }

                passwordError = when {
                    password.isBlank() -> "Password is required"
                    else -> null
                }

                val formIsValid =
                    emailError == null &&
                            passwordError == null

                if (formIsValid) {
                    loginError = null
                    isLoading = true

                    auth.signInWithEmailAndPassword(
                        email.trim(),
                        password
                    ).addOnCompleteListener { task ->
                        isLoading = false

                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            loginError = "Invalid email or password"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Logging in..."
                else "Log in"
            )
        }

        loginError?.let {
            Text(it)
        }
    }
}