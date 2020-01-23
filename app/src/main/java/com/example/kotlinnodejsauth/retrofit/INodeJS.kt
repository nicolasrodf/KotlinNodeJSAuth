package com.example.kotlinnodejsauth.retrofit

import android.app.Person
import com.example.kotlinnodejsauth.model.User
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface INodeJS {

    @POST("register")
    @FormUrlEncoded
    fun registerUser(@Field("email") email:String,
                     @Field("name") name:String,
                     @Field("password") password:String):Observable<String>

    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field("email") email:String,
                     @Field("password") password:String):Observable<String>

    @POST("search")
    @FormUrlEncoded
    fun searchPerson(@Field("search") searchQuery:String):Observable<List<User>>

    @get:GET("users")
    val userList:Observable<List<User>>

}