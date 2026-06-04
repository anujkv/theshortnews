package com.a3solution.theshortnews.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel = viewModel(),
    onArticleClick: (Article) -> Unit
) {
    val articles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        viewModel.fetchNews(it)
                        isSearchActive = false
                    },
                    onClose = {
                        isSearchActive = false
                        searchQuery = ""
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("The Short News") },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading && articles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(articles) { article ->
                        NewsItem(article, onClick = { onArticleClick(article) })
                    }
                }
            }

            if (isSearchActive && searchQuery.isEmpty() && searchHistory.isNotEmpty()) {
                SearchHistoryPanel(
                    history = searchHistory,
                    onHistoryClick = {
                        searchQuery = it
                        viewModel.fetchNews(it)
                        isSearchActive = false
                    },
                    onClearHistory = { viewModel.clearHistory() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClose: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search news...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Clear, contentDescription = "Close Search")
            }
        },
        actions = {
            IconButton(onClick = { if (query.isNotEmpty()) onSearch(query) }) {
                Icon(Icons.Default.Search, contentDescription = "Execute Search")
            }
        }
    )
}

@Composable
fun SearchHistoryPanel(
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Searches", fontWeight = FontWeight.Bold)
                TextButton(onClick = onClearHistory) {
                    Text("Clear All")
                }
            }
            LazyColumn {
                items(history) { item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                        modifier = Modifier.clickable { onHistoryClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun NewsItem(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            article.image?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = article.title ?: "No Title",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = article.body ?: "",
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = article.source?.title ?: "Unknown Source",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = article.date ?: "",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
