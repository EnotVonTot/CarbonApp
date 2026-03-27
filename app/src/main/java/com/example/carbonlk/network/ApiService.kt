package com.example.carbonlk.network

import com.example.carbonlk.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Авторизация
    @GET(".")
    suspend fun login(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.login",
        @Query("arg1") args: String
    ): Response<LoginResponse>

    // Получение полных данных пользователя
    @GET(".")
    suspend fun getUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_user",
        @Query("arg1") args: String
    ): Response<FullUserResponse>

    // Добровольная блокировка
    @GET(".")
    suspend fun blockUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.block_user",
        @Query("arg1") args: String
    ): Response<BlockUserResponse>

    // Получение списка услуг
    @GET(".")
    suspend fun getServicesList(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_usluga_list",
        @Query("arg1") args: String
    ): Response<List<List<Map<String, Any>>>>

    // Подключение услуги
    @GET(".")
    suspend fun setUserUsluga(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.set_user_usluga",
        @Query("arg1") args: String
    ): Response<BlockUserResponse>  // можно использовать ту же модель BlockUserResponse

    // Отключение услуги
    @GET(".")
    suspend fun removeUserUsluga(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.remove_user_usluga",
        @Query("arg1") args: String
    ): Response<ServiceActionResponse>
}