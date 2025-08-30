package com.richard.pixlog.data.remote.request

import com.google.gson.annotations.SerializedName
data class StoryRequest(
    @SerializedName("page")
    val page : Int? = 1,
    val size : Int? = 10,
    val location : Int? = 0,
)