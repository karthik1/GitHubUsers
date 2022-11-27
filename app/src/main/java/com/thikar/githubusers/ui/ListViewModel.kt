package com.thikar.githubusers.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thikar.githubusers.api.UserDetails
import com.thikar.githubusers.repository.UserRepository
import com.thikar.githubusers.util.Event
import com.thikar.githubusers.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(repository: UserRepository):ViewModel() {


    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val currentQuery = MutableStateFlow<String?>(null)
    val currentQueryFlow = currentQuery

    private val refreshTriggerChannel = Channel<Unit>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val hasCurrentQuery = currentQuery.map { it != null }
    var newQueryInProgress = false


    val results = refreshTrigger.flatMapLatest {
        currentQuery.value?.let {query ->
            repository.getUsers( query ,
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )
        }?: emptyFlow<Resource<List<UserDetails>>>().stateIn(viewModelScope,
            SharingStarted.Lazily, null)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun onSearchQuerySubmit(query: String) {
        currentQuery.value = query
    }

    fun setTrigger(){
        viewModelScope.launch {
            refreshTriggerChannel.send(Unit)
        }
    }

}