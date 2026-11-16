package land.noah.smartcafe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import land.noah.smartcafe.data.CafeRepository

class SmartCafeViewModelFactory(
    private val repository: CafeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SmartCafeViewModel::class.java)) {
            return SmartCafeViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}