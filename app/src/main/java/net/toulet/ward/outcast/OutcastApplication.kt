package net.toulet.ward.outcast

import android.app.Application
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.toulet.ward.outcast.data.OutcastDatabase
import net.toulet.ward.outcast.data.feed.Feed
import net.toulet.ward.outcast.data.feed.FeedDao
import net.toulet.ward.outcast.data.feed.FeedRepository

class OutcastApplication: Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { OutcastDatabase.getInstance(this, applicationScope) }
    val feedRepository by lazy { FeedRepository(database.feedDao()) }
}
