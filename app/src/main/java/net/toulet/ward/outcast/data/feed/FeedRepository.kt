package net.toulet.ward.outcast.data.feed

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FeedRepository(private val feedDao: FeedDao) {
    val feeds: Flow<List<Feed>> = feedDao.allFeeds()

    @Suppress()
    @WorkerThread
    suspend fun insert(feed: Feed) {
        feedDao.insert(feed)
    }
}