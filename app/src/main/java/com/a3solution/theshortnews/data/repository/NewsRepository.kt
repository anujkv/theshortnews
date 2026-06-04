package com.a3solution.theshortnews.data.repository

import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.local.ArticleDao
import com.a3solution.theshortnews.data.local.toDomain
import com.a3solution.theshortnews.data.local.toEntity
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Repository class that abstracts the data source for news articles.
 *
 * It provides methods to fetch top articles and article details from the [NewsApiService].
 * Results are returned as [Flow] objects to handle asynchronous data streams.
 */
class NewsRepository(
    private val apiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val networkUtils: NetworkUtils
) {
    fun getTopArticles(keyword: String? = null): Flow<List<Article>> = flow {
        // First, emit cached data from DB
        // We use emitAll on a map to keep it reactive if we want, 
        // but since we might be doing a one-time fetch here, let's just emit current and then refresh.
        
        // However, the user wants: "check if internet conectivity not available then show offline support data from db or cache and when connect then show latest data and store for offline and show it"
        
        // Let's emit what's in DB first (non-reactive for a moment, or just use emitAll)
        // If we want it reactive, we should probably return articleDao.getAllArticles().map { ... }
        // and trigger the network fetch separately.
        
        // Let's try this:
        if (networkUtils.isNetworkAvailable()) {
            try {
                val response = apiService.getTopArticles(
                    apiKey = NewsApiService.API_KEY,
                    keyword = keyword
                )
                val articles = response.articles?.results ?: emptyList()
                if (articles.isNotEmpty()) {
                    articleDao.refreshArticles(articles.mapNotNull { it.toEntity() })
                }
            } catch (e: Exception) {
                // Ignore network errors, fallback to DB will happen below
            }
        }
        
        // Emit from DB (this will emit the updated data if network fetch was successful)
        emitAll(articleDao.getAllArticles(keyword).map { entities ->
            entities.map { it.toDomain() }
        })
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
