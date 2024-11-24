package com.example.project1.ui.activity.Login
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.DataRequest.LoginRequest
import com.example.project1.retrofit.client.ApiClient
import com.example.project1.retrofit.client.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.authService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    loginResponse?.accessToken?.let { accessToken ->
                        loginResponse.refreshToken?.let { refreshToken ->
                            // Lưu token vào SharedPreferences sau khi đăng nhập thành công
                            TokenManager.saveTokens(accessToken, refreshToken)

                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            onError("Access token không hợp lệ")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Đăng nhập thất bại: ${response.message()}")
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
