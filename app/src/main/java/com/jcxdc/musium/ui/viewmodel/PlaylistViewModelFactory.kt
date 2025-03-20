import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jcxdc.musium.model.repository.DatabaseRepository
import com.jcxdc.musium.model.repository.PlaylistRepository
import com.jcxdc.musium.ui.viewmodel.DatabaseViewModel
import com.jcxdc.musium.ui.viewmodel.PlaylistViewModel

class PlaylistViewModelFactory(private val repository: PlaylistRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            return PlaylistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
