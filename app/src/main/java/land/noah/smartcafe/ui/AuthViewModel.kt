package land.noah.smartcafe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import land.noah.smartcafe.data.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    val currentUserId: String?
        get() = authRepository.currentUserId

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        errorMessage = null
        isLoading = true

        authRepository.login(
            email = email,
            password = password
        ) { result ->
            isLoading = false

            result
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    errorMessage = "Invalid email or password"
                }
        }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        errorMessage = null
        isLoading = true

        authRepository.register(
            email = email,
            password = password
        ) { result ->
            isLoading = false

            result
                .onSuccess {
                    onSuccess()
                }
                .onFailure { exception ->
                    errorMessage =
                        exception.message ?: "Registration failed"
                }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun clearError() {
        errorMessage = null
    }
}