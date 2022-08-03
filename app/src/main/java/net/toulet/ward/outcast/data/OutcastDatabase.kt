package net.toulet.ward.outcast.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.toulet.ward.outcast.data.feed.Feed
import net.toulet.ward.outcast.data.feed.FeedDao

@Database(entities = arrayOf(Feed::class), version = 1, exportSchema = false)
public abstract class OutcastDatabase: RoomDatabase() {
    abstract fun feedDao(): FeedDao

    companion object {
        @Volatile
        private var INSTANCE: OutcastDatabase? = null

        fun getInstance(
            context: Context,
            scope: CoroutineScope
        ): OutcastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        OutcastDatabase::class.java,
                        "outcast_database"
                    )
                    .addCallback(SeederCallback(scope))
                    .build();

                INSTANCE = instance

                instance
            }

        }
    }

    private class SeederCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    seedDatabase(database.feedDao())
                }

            }
        }

        suspend fun seedDatabase(feedDao: FeedDao) {
            var feeds = listOf(
                Feed(
                    id = 1,
                    title = "99 Percent invisible",
                    description = "",
                    rssUri = "https://feeds.simplecast.com/BqbsxVfO"
                ),
                Feed(
                    id = 2,
                    title = "The kitchen sisters present",
                    description = "",
                    rssUri = "https://feeds.simplecast.com/BqbsxVfO"
                ),
            )

            feeds.forEach({ feedDao.insert(it) })

            Log.d("Seed", feedDao.allFeeds().first().toString())
        }
    }
}
