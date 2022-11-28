package com.thikar.githubusers.ui.followerslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thikar.githubusers.api.UserDetails
import com.thikar.githubusers.repository.UserRepository
import com.thikar.githubusers.util.Resource
import com.thikar.githubusers.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersListViewModel @Inject constructor(repository:UserRepository):ViewModel() {


    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val selectedUserName = MutableStateFlow<String?>(null)

    private val refreshTriggerChannel = Channel<Unit>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val followersList = refreshTrigger.flatMapLatest {
        selectedUserName.value?.let {query ->
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

    fun setSelectedUserName(name: String) {
        selectedUserName.value = name
    }

    fun setTrigger(){
        viewModelScope.launch {
            refreshTriggerChannel.send(Unit)
        }
    }
}

//    val followersList = selectedUserName.flatMapLatest { query ->
//        query?.let{
//            repository.getFollowers( query ,
//                onFetchFailed = { t ->
//                    viewModelScope.launch {
//                        eventChannel.send(Event.ShowErrorMessage(t))
//                    }
//                }
//            )
//        } ?: emptyFlow<Resource<List<UserDetails>>>().stateIn(viewModelScope,SharingStarted.Lazily, null)
//    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
