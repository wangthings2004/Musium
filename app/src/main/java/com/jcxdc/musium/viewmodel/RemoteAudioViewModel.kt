import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.RemoteAudio
import com.jcxdc.musium.repository.APIRepository
import com.jcxdc.musium.utils.CommonFunction.isNetworkAvailable
import com.jcxdc.musium.utils.RemoteAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteAudioViewModel @Inject constructor(
    private val repository: APIRepository,
    @ApplicationContext private var context: Context
) : ViewModel() {

    private val _responseRemoteAudio: MutableLiveData<RemoteAudioState<RemoteAudio>> = MutableLiveData(RemoteAudioState.Loading)
    val responseRemoteAudio: LiveData<RemoteAudioState<RemoteAudio>> = _responseRemoteAudio

    init {
        fetchRemoteAudio()
    }

    private fun fetchRemoteAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isNetworkAvailable(context)) {
                try {
                    val response = repository.getRemoteAudioRepo().first() // fetch remote audio data
                    _responseRemoteAudio.postValue(RemoteAudioState.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An Error Occurred. Please try again"
                    _responseRemoteAudio.postValue(RemoteAudioState.Error(errorMessage))
                }
            } else {
                val errorMessage = "No Internet Connection"
                _responseRemoteAudio.postValue(RemoteAudioState.Error(errorMessage))
            }
        }
    }
}
