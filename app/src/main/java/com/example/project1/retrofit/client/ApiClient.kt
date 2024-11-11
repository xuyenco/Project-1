package com.example.project1.retrofit.client

import android.content.Context
import com.example.project1.retrofit.service.AuthService
import com.example.project1.retrofit.service.ItemService
import com.example.project1.retrofit.service.OrderItemService
import com.example.project1.retrofit.service.OrdersService
import com.example.project1.retrofit.service.ReservationService
import com.example.project1.retrofit.service.TableReservationService
import com.example.project1.retrofit.service.TableService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.59.1:5001"
    var token: String? = null
    private lateinit var appContext: Context
    fun initialize(context: Context) {
        appContext = context.applicationContext  // Sử dụng applicationContext để tránh memory leak
    }
    // Authenticator để xử lý lỗi 401 và refresh token
    val tokenAuthenticator = object : Authenticator {
        override fun authenticate(route: Route?, response: Response): Request? {
            return if (response.code == 401) { // Kiểm tra mã lỗi 401
                val newAccessToken = runBlocking {
                    val refreshResponse = ApiClient.authService.refreshToken()
                    if (refreshResponse.isSuccessful) {
                        val newToken = refreshResponse.body()?.accessToken
                        newToken?.let {
                            // Lưu access token mới
                            TokenManager.saveTokens(RetrofitClient.appContext, it, TokenManager.getRefreshToken(RetrofitClient.appContext) ?: "")
                            it
                        }
                    } else {
                        null
                    }
                }

                newAccessToken?.let {
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                }
            } else {
                null
            }
        }
    }

    // Interceptor để thêm access token vào tất cả các request
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        val accessToken = TokenManager.getAccessToken(appContext)  // Lấy accessToken từ TokenManager
        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

//    private const val BASE_URL = "http://192.168.59.1:5001"
    private const val BASE_URL = "http://192.168.168.1:5001"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Thêm client vào Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val authService: AuthService by lazy {
        RetrofitClient.retrofit.create(AuthService::class.java)
    }
    val tableService: TableService by lazy {
        RetrofitClient.retrofit.create(TableService::class.java)
    }
    val reservationService: ReservationService by lazy {
        RetrofitClient.retrofit.create(ReservationService::class.java)
    }
    val orderService : OrdersService by lazy {
        RetrofitClient.retrofit.create(OrdersService::class.java)
    }
    val itemService : ItemService by lazy {
        RetrofitClient.retrofit.create(ItemService::class.java)
    }
    val orderItemService : OrderItemService by lazy {
        RetrofitClient.retrofit.create(OrderItemService::class.java)
    }
    val tableReservationsService : TableReservationService by lazy {
        RetrofitClient.retrofit.create(TableReservationService::class.java)
    }
    val reservationTableService : ReservationTableService by lazy {
        RetrofitClient.retrofit.create(ReservationTableService::class.java)
    }
}
