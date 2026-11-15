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
import com.google.firebase.auth.FirebaseAuthUserCollisionException

@Composable
fun RegisterScreen() {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmPasswordError by rememberSaveable { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()

    var registrationError by rememberSaveable { mutableStateOf<String?>(null) }
    var registrationSuccess by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 96.dp
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create account")

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm password") },
            singleLine = true,
            isError = confirmPasswordError != null,
            supportingText = {
                confirmPasswordError?.let {
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
                    password.length < 8 -> "Password must be at least 8 characters"
                    else -> null
                }

                confirmPasswordError = when {
                    confirmPassword.isBlank() -> "Please confirm your password"
                    confirmPassword != password -> "Passwords do not match"
                    else -> null
                }

                val formIsValid =
                    emailError == null &&
                            passwordError == null &&
                            confirmPasswordError == null

                if (formIsValid) {
                    registrationError = null
                    isLoading = true

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false

                            if (task.isSuccessful) {
                                registrationSuccess = true
                            } else {
                                registrationError = when (task.exception) {
                                    is FirebaseAuthUserCollisionException ->
                                        "An account already exists with this email"

                                    else ->
                                        "Account creation failed. Please try again."
                                }
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isLoading) "Creating account..."
                else "Create account"
            )
        }

        registrationError?.let {
            Text(it)
        }

        if (registrationSuccess) {
            Text("Account created successfully")
        }
    }
}