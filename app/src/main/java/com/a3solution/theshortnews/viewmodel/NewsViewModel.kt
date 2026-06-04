package com.a3solution.theshortnews.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.a3solution.theshortnews.data.SearchHistoryManager
import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val historyManager = SearchHistoryManager(application)
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _selectedArticle = MutableStateFlow<Article?>(null)
    val selectedArticle: StateFlow<Article?> = _selectedArticle

    private val repository: NewsRepository

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(NewsApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(NewsApiService::class.java)
        repository = NewsRepository(apiService)
        fetchNews()
        loadHistory()
    }

    fun fetchNews(query: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTopArticles(query).collect {
                _articles.value = it
                _isLoading.value = false
                if (!query.isNullOrBlank()) {
                    historyManager.saveSearch(query)
                    loadHistory()
                }
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
