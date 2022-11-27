package com.thikar.githubusers.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    companion object{
        const val BASE_URL = "https://api.github.com/"
    }

    @GET("search/users")
    suspend fun getUserList(
        @Query("q") query: String,
    ): UserDetailsResponse

    @GET("users/{name}/followers")
    suspend fun getFollowersList(
        @Path("name") name: String
    ): List<UserDetails>

}