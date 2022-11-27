package com.thikar.githubusers.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thikar.githubusers.api.UserDetails
import com.thikar.githubusers.repository.UserRepository
import com.thikar.githubusers.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(repository: UserRepository):ViewModel() {

    //Shared
    val selectedName = MutableLiveData<String>()

    fun selectedName(name: String) {
        selectedName.value = name
    }

    //Users
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Unit>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    private val currentQuery = MutableStateFlow<String?>(null)
    val currentQueryFlow = currentQuery

    val hasCurrentQuery = currentQuery.map { it != null }
    var newQueryInProgress = false

    //Followers
    private val selectedUserName = MutableStateFlow<String?>(null)


    val fol = refreshTrigger.flatMapLatest {
        currentQuery.value?.let {query ->
            repository.getFollowers( query ,
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )
        }?: emptyFlow<Resource<List<UserDetails>>>().stateIn(viewModelScope,
            SharingStarted.Lazily, null)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val followersList = selectedUserName.flatMapLatest { query ->
        query?.let{
            repository.getFollowers( query ,
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )
        } ?: emptyFlow<Resource<List<UserDetails>>>().stateIn(viewModelScope,
            SharingStarted.Lazily, null)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val results = currentQuery.flatMapLatest { query ->
        query?.let{
            repository.getUsers( query ,
                onFetchFailed = { t ->
                    viewModelScope.launch {
                        eventChannel.send(Event.ShowErrorMessage(t))
                    }
                }
            )
        } ?: emptyFlow<Resource<List<UserDetails>>>().stateIn(viewModelScope,SharingStarted.Lazily, null)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setSelectedUserName(name: String) {
        selectedUserName.value = name
    }

    fun onSearchQuerySubmit(query: String) {
        currentQuery.value = query
    }

    fun setTrigger(){
        viewModelScope.launch {
            refreshTriggerChannel.send(Unit)
        }
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }

}