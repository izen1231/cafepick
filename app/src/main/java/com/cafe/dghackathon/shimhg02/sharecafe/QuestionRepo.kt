package com.cafe.dghackathon.shimhg02.dghack

import com.google.gson.annotations.SerializedName

class QuestionRepo {
    @SerializedName("id")
    internal var id: String? = null
    @SerializedName("writer")
    internal var writer: String? = null
    @SerializedName("content")
    internal var content: String? = null
    @SerializedName("date")
    internal var date: String? = null
}