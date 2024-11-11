
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.DataRequest.LoginRequest
import com.example.project1.retrofit.client.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    var token: String? = null
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.authService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    token = response.body()?.accessToken

                    withContext(Dispatchers.Main) {  // Chuyển về main thread để cập nhật trạng thái UI hoặc thực hiện callback
                        onSuccess()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Đăng nhập thất bại")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    onError("Lỗi mạng: ${e.message}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Lỗi không xác định: ${e.message}")
                }
            }
        }

    }
}
