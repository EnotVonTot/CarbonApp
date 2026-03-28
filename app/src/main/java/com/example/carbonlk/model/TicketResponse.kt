package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class TicketsListResponse(
    @SerializedName("count") val count: String,
    @SerializedName("has_previous_page") val hasPreviousPage: String,
    @SerializedName("num_pages") val numPages: String,
    @SerializedName("items") val items: List<TicketItem>,
    @SerializedName("current_page") val currentPage: String,
    @SerializedName("has_next_page") val hasNextPage: String
)

data class TicketItem(
    @SerializedName("id") val id: String,
    @SerializedName("pk") val pk: String,
    @SerializedName("subj") val subject: String,
    @SerializedName("__status") val status: String,
    @SerializedName("hdsk_datetime") val createdDate: String,
    @SerializedName("text") val text: String,
    @SerializedName("child_count") val childCount: String
)