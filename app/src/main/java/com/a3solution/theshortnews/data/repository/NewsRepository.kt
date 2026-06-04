package com.a3solution.theshortnews.data.repository

import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepository(private val apiService: NewsApiService) {
    fun getTopArticles(): Flow<List<Article>> = flow {
        try {
            val response = apiService.getTopArticles(apiKey = NewsApiService.API_KEY)
            emit(response.articles?.results ?: emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
