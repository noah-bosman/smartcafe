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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    isLoading: Boolean,
    registrationError: String?,
    onRegister: (
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var confirmPassword by rememberSaveable {
        mutableStateOf("")
    }

    var emailError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var passwordError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var confirmPasswordError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var registrationSuccess by rememberSaveable {
        mutableStateOf(false)
    }

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
                registrationSuccess = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Email")
            },
            singleLine = true,
            isError = emailError != null,
            supportingText = {
                emailError?.let { error ->
                    Text(error)
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
                registrationSuccess = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                passwordError?.let { error ->
                    Text(error)
                }
            },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = null
                registrationSuccess = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Confirm password")
            },
            singleLine = true,
            isError = confirmPasswordError != null,
            supportingText = {
                confirmPasswordError?.let { error ->
                    Text(error)
                }
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                emailError = when {
                    email.isBlank() ->
                        "Email is required"

                    !Patterns.EMAIL_ADDRESS
                        .matcher(email)
                        .matches() ->
                        "Enter a valid email address"

                    else ->
                        null
                }

                passwordError = when {
                    password.isBlank() ->
                        "Password is required"

                    password.length < 8 ->
                        "Password must be at least 8 characters"

                    else ->
                        null
                }

                confirmPasswordError = when {
                    confirmPassword.isBlank() ->
                        "Please confirm your password"

                    confirmPassword != password ->
                        "Passwords do not match"

                    else ->
                        null
                }

                val formIsValid =
                    emailError == null &&
                            passwordError == null &&
                            confirmPasswordError == null

                if (formIsValid) {
                    onRegister(
                        email.trim(),
                        password
                    ) {
                        registrationSuccess = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) {
                    "Creating account..."
                } else {
                    "Create account"
                }
            )
        }

        registrationError?.let { error ->
            Text(error)
        }

        if (registrationSuccess) {
            Text("Account created successfully")
        }

        TextButton(
            onClick = onBackToLogin
        ) {
            Text("Back to login")
        }
    }
}