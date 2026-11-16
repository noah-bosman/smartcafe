package land.noah.smartcafe.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun register(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(Result.success(Unit))
            } else {
                val exception = task.exception

                if (exception is FirebaseAuthUserCollisionException) {
                    onResult(
                        Result.failure(
                            IllegalStateException(
                                "An account already exists with this email"
                            )
                        )
                    )
                } else {
                    onResult(
                        Result.failure(
                            exception ?: IllegalStateException(
                                "Registration failed"
                            )
                        )
                    )
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(Result.success(Unit))
            } else {
                onResult(
                    Result.failure(
                        task.exception ?: IllegalStateException(
                            "Login failed"
                        )
                    )
                )
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}