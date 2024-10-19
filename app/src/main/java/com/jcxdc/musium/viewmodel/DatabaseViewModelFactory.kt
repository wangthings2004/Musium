import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jcxdc.musium.repository.DatabaseRepository
import com.jcxdc.musium.viewmodel.DatabaseViewModel

class DatabaseViewModelFactory(private val repository: DatabaseRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
