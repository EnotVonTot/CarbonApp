package com.example.carbonlk.network

import com.example.carbonlk.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Авторизация
    @GET("api/")
    suspend fun login(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.login",
        @Query("arg1") args: String
    ): Response<LoginResponse>

    // Получение полных данных пользователя
    @GET("api/")
    suspend fun getUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_user",
        @Query("arg1") args: String
    ): Response<FullUserResponse>

    // ФИО
    @GET("api/")
    suspend fun getFio(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_fio",
        @Query("arg1") args: String
    ): Response<FioResponse>

    // Лицевой счёт
    @GET("api/")
    suspend fun getAccountId(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_account_id",
        @Query("arg1") args: String
    ): Response<AccountIdResponse>

    // Баланс
    @GET("api/")
    suspend fun getBalance(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_balance",
        @Query("arg1") args: String
    ): Response<BalanceResponse>

    // Тариф
    @GET("api/")
    suspend fun getTariff(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_user_tarif",
        @Query("arg1") args: String
    ): Response<TariffResponse>

    // Дата активации
    @GET("api/")
    suspend fun getCreateDate(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_create_date",
        @Query("arg1") args: String
    ): Response<CreateDateResponse>

    // Добровольная блокировка
    @GET("api/")
    suspend fun blockUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.block_user",
        @Query("arg1") args: String
    ): Response<BlockUserResponse>
}