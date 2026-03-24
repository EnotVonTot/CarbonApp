package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class BlockUserResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("result") val result: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("block_id") val blockId: String? = null
)