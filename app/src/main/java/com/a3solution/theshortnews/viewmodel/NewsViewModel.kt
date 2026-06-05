package com.a3solution.theshortnews.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.a3solution.theshortnews.data.SearchHistoryManager
import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.repository.NewsRepository
import com.a3solution.theshortnews.data.local.AppDatabase
import com.a3solution.theshortnews.utils.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * ViewModel for the News screen.
 *
 * Manages the UI state for the list of articles, search history, and loading states.
 * It interacts with the [NewsRepository] to fetch data from the Event Registry API.
 */
class NewsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository: NewsRepository = createDefaultRepository(application)
    private val historyManager = SearchHistoryManager(application)
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _selectedArticle = MutableStateFlow<Article?>(null)
    val selectedArticle: StateFlow<Article?> = _selectedArticle

    private var currentPage = 1
    private var canLoadMore = true
    private var currentQuery: String? = null
    private var fetchJob: kotlinx.coroutines.Job? = null

    init {
        initialLoad()
        loadHistory()
    }

    companion object {
        private fun createDefaultRepository(application: Application): NewsRepository {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val apiKeyInterceptor = okhttp3.Interceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url

                val url = originalUrl.newBuilder()
                    .addQueryParameter("apiKey", NewsApiService.API_KEY)
                    .build()

                val requestBuilder = originalRequest.newBuilder()
                    .url(url)

                chain.proceed(requestBuilder.build())
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(apiKeyInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(NewsApiService.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(NewsApiService::class.java)
            val database = AppDatabase.getDatabase(application)
            val networkUtils = NetworkUtils(application)
            return NewsRepository(apiService, database.articleDao(), networkUtils)
        }
    }

    private fun initialLoad() {
        fetchNews(null)
    }

    fun fetchNews(query: String? = null) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            currentPage = 1
            canLoadMore = true
            currentQuery = query
            
            repository.getTopArticles(query, page = currentPage).collect { newArticles ->
                _articles.value = newArticles
                _isLoading.value = false
                if (!query.isNullOrBlank() && newArticles.isNotEmpty()) {
                    historyManager.saveSearch(query)
                    loadHistory()
                }
            }
        }
    }

    fun loadMoreNews() {
        if (_isLoading.value || !canLoadMore) return

        viewModelScope.launch {
            _isLoading.value = true
            val nextPage = currentPage + 1
            repository.getTopArticles(currentQuery, page = nextPage).collect { newArticles ->
                val prevCount = _articles.value.size
                _articles.value = newArticles
                _isLoading.value = false
                currentPage = nextPage
                
                if (newArticles.size <= prevCount) {
                    canLoadMore = false
                }
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            canLoadMore = true
            repository.getTopArticles(currentQuery, page = currentPage).collect { newArticles ->
                _articles.value = newArticles
                _isRefreshing.value = false
            }
        }
    }

    private fun loadHistory() {
        _searchHistory.value = historyManager.getHistory()
    }

    fun clearHistory() {
        historyManager.clearHistory()
        loadHistory()
    }

    fun selectArticle(article: Article) {
        _selectedArticle.value = article
        article.uri?.let { uri ->
            fetchArticleDetails(uri)
        }
    }

    private fun fetchArticleDetails(uri: String) {
        viewModelScope.launch {
            repository.getArticleDetails(uri).collect { detailedArticle ->
                detailedArticle?.let {
                    _selectedArticle.value = it
                }
            }
        }
    }
}
