package com.example.carbonlk.network

import com.example.carbonlk.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(".")
    suspend fun login(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.login",
        @Query("arg1") args: String
    ): Response<LoginResponse>

    @GET(".")
    suspend fun getUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_user",
        @Query("arg1") args: String
    ): Response<FullUserResponse>

    @GET(".")
    suspend fun blockUser(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.block_user",
        @Query("arg1") args: String
    ): Response<BlockUserResponse>

    @GET(".")
    suspend fun getServicesList(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_usluga_list",
        @Query("arg1") args: String
    ): Response<List<List<Map<String, Any>>>>

    @GET(".")
    suspend fun setUserUsluga(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.set_user_usluga",
        @Query("arg1") args: String
    ): Response<ServiceActionResponse>

    @GET(".")
    suspend fun removeUserUsluga(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.remove_user_usluga",
        @Query("arg1") args: String
    ): Response<ServiceActionResponse>

    @GET(".")
    suspend fun addCardPayment(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.add_card_payment_operation",
        @Query("arg1") args: String
    ): Response<List<PaymentResponse>>

    @GET(".")
    suspend fun getHelpdeskDialogs(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_helpdesk_dialogs",
        @Query("arg1") args: String
    ): Response<TicketsListResponse>

    @GET(".")
    suspend fun getHelpdeskComments(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.get_helpdesk_comments",
        @Query("arg1") args: String
    ): Response<CommentsListResponse>

    @GET(".")
    suspend fun createTicket(
        @Query("format") format: String = "json",
        @Query("context") context: String = "web",
        @Query("model") model: String = "users",
        @Query("method1") method: String = "web_cabinet.create_ticket",
        @Query("arg1") args: String
    ): Response<CreateTicketResponse>

}