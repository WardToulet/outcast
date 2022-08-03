package net.toulet.ward.outcast.data.feed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Feed (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val rssUri: String,

    val title: String,
    val description: String,
)