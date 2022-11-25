package com.thikar.githubusers.api

interface UserApi {
    companion object{
        const val BASE_URL = "https://api.github.com/"
    }

suspend fun getUserList():UserDetails
}