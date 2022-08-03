package net.toulet.ward.outcast.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.toulet.ward.outcast.data.feed.Feed
import net.toulet.ward.outcast.data.feed.FeedRepository

class FeedViewModel(private val repository: FeedRepository): ViewModel() {
    val feeds: LiveData<List<Feed>> = repository.feeds.asLiveData()
    //val feeds by mutableStateOf(repository.feeds)
    //val feeds  = MutableStateFlow(repository.feeds)

    fun insert(feed: Feed) = viewModelScope.launch {
        repository.insert(feed)
    }
}

class FeedViewModelFactory(private val repository: FeedRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // SAFETY: this is ok to do as we mannulay check before casting
        if(modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}