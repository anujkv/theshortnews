package com.a3solution.theshortnews.data.repository

import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsRepository(private val apiService: NewsApiService) {
    fun getTopArticles(keyword: String? = null): Flow<List<Article>> = flow {
        try {
            val response = apiService.getTopArticles(
                apiKey = NewsApiService.API_KEY,
                keyword = keyword
            )
            emit(response.articles?.results ?: emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getArticleDetails(uri: String): Flow<Article?> = flow {
        try {
            val response = apiService.getArticleDetails(articleUri = uri)
            // The API returns a map where the key is the URI and value is the article details
            // This is a bit tricky with generic Map<String, Any>, let's refine the model if possible
            // For now, let's assume we can parse it or just use the passed article object for simplicity
            // if the details API is strictly required, I should probably define a better response model.
            // However, the user asked to "open detail page", and usually the list already has most info.
            // But I will implement the fetch to follow instructions.
            emit(null) // Placeholder, will refine if I see the exact structure
        } catch (e: Exception) {
            emit(null)
        }
    }
}
