package com.thikar.githubusers.api

import com.google.gson.annotations.SerializedName

data class UserDetails(

    val id:Long,

    @SerializedName("avatar_url")
    val imageUrl:String,

    @SerializedName("followers_url")
    val followersUrl:String,
)