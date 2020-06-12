package com.cafe.dghackathon.shimhg02.dghack

import retrofit2.Call
import retrofit2.http.*
import kotlin.collections.ArrayList

interface API {

    @POST("/auth/login")
    @FormUrlEncoded
    fun logIn(@Field("username") id : String, @Field("password") pw : String) : Call<Login>
    @POST("/auth/session")
    @FormUrlEncoded
    fun userHistory(@Field("session") session : String?) : Call<ArrayList<HistoryData>>

    @GET("/api/getCafeStatus")
    fun getPlace() : Call<List<LocationRepo>>

    @POST("/rent/getAvailableCafe")
    @FormUrlEncoded
    fun getRacks(@Field("stName") stName : String?, @Field("stId") stId : String?, @Field("session") session : String?) : Call<List<RackData>>

    @GET("/qna")
    fun getQuestionList() : Call<ArrayList<QuestionRepo>>

    @POST("/qna")
    @FormUrlEncoded
    fun writeQuestion(@Field("writer") writer : String, @Field("content") content : String)

    @GET("/qna/{id}")
    fun getQuestionDetail(@Path("id") id : String)

    @POST("/qna/{id}")
    @FormUrlEncoded
    fun writeAnswer(@Path("id") id : String, @Field("writer") writer: String, @Field("content") content: String)
}