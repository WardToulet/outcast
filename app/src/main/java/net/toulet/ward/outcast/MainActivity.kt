package net.toulet.ward.outcast

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.toulet.ward.outcast.data.feed.Feed
import net.toulet.ward.outcast.ui.theme.OutCastTheme
import net.toulet.ward.outcast.viewmodel.FeedViewModel
import net.toulet.ward.outcast.viewmodel.FeedViewModelFactory

class MainActivity : ComponentActivity() {
    private val feedViewModel: FeedViewModel by viewModels {
        FeedViewModelFactory((application as OutcastApplication).feedRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            getFeed(uri = "https://anchor.fm/s/354c6280/podcast/rss")
        }

        setContent {
            OutCastTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar {}
                        },
                        content = {
                            FeedList(feeds = feedViewModel.feeds.observeAsState(listOf()).value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FeedList(feeds: List<Feed>) {
    LazyColumn {
        items(feeds) { feed ->
            FeedListItem(feed = feed)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedList() {
    OutCastTheme {
        FeedList(
            feeds = listOf(
                Feed(
                    id = 1,
                    title = "99 Percent invisible",
                    description = "Design is everywhere in our lives, perhaps most importantly in the places where we've just stopped noticing. 99% Invisible is a weekly exploration of the process and power of design and architecture.",
                    rssUri = "https://feeds.simplecast.com/BqbsxVfO"
                ),
                Feed(
                    id = 2,
                    title = "The kitchen sisters present",
                    description = "",
                    rssUri = "https://feeds.simplecast.com/BqbsxVfO"
                ),
            )
        )
    }
}

@Composable
fun FeedListItem(feed: Feed) {
    Row (
        modifier = Modifier.padding(4.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo for $feed.title"
        )
        Spacer(
            modifier = Modifier.padding(4.dp)
        )
        Column (
        ){
            Text(text = feed.title, )
            Text(text = feed.description, style = TextStyle( fontWeight = FontWeight(300)))
        }
    }

}

@Preview
@Composable
fun PreviewFeedListItem() {
    FeedListItem(feed = Feed(
        id = 1,
        title = "99 Percent invisible",
        description = "Design is everywhere in our lives, perhaps most importantly in the places where we've just stopped noticing. 99% Invisible is a weekly exploration of the process and power of design and architecture.",
        rssUri = "https://feeds.simplecast.com/BqbsxVfO"
    ))
}