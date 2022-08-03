package net.toulet.ward.outcast.data.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed")
    fun allFeeds(): Flow<List<Feed>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feed: Feed)
}