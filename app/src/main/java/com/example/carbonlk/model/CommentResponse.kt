package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("id") val id: String,
    @SerializedName("pk") val pk: String,
    @SerializedName("text") val text: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("__creator_abonent") val creatorName: String,
    @SerializedName("read_comment") val readComment: String
)

// Для ответа от get_helpdesk_comments (массив комментариев)
typealias CommentsListResponse = List<CommentResponse>