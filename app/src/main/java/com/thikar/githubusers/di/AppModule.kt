package com.thikar.githubusers.di

import com.thikar.githubusers.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit():Retrofit = Retrofit.Builder()
        .baseUrl(UserApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideUsersAPi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)


}