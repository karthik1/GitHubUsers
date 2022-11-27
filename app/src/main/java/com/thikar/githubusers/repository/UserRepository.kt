package com.thikar.githubusers.repository

import android.util.Log
import com.thikar.githubusers.api.UserApi
import com.thikar.githubusers.api.UserDetails
import com.thikar.githubusers.api.UserDetailsResponse
import com.thikar.githubusers.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi
)  {


    fun getFollowers(name:String, onFetchFailed: (Throwable) -> Unit)
    : Flow<Resource<List<UserDetails>>> = flow {
        var response = ArrayList<UserDetails>()
        emit(Resource.Loading(response))
        try {
            response = userApi.getFollowersList(name) as ArrayList<UserDetails>
            emit(Resource.Success(response))
        } catch (t: Throwable) {
            onFetchFailed.invoke(t)
            emit(Resource.Error(t, response))
        }
    }

    suspend fun getUsers(query:String, onFetchFailed: (Throwable) -> Unit): Flow<Resource<List<UserDetails>>> =
        flow {

            var response = UserDetailsResponse(ArrayList())
            emit(Resource.Loading(response.items))

            try {
                response = userApi.getUserList(query)
                emit(Resource.Success(response.items))
            } catch (t: Throwable) {
                onFetchFailed.invoke(t)
                emit(Resource.Error(t, response.items))
            }
        }
}

//    suspend fun getFollowersList(query:String): List<UserDetails>? {
//        return try {
//            val response = userApi.getFollowersList(query)
//            response.items
//        } catch (t: Throwable) {
//            null
//        }
//    }

//suspend fun getUsersList(query:String): UserDetailsResponse? {
//    return try {
//        val response = userApi.getUserList(query)
//        response
//    } catch (t: Throwable) {
//        null
//    }
//}