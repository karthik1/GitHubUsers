package com.thikar.githubusers.util

sealed class Event {
    data class ShowErrorMessage(val error: Throwable) : Event()
}