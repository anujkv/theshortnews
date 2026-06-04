package com.a3solution.theshortnews.data.api

import com.a3solution.theshortnews.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("article/getArticles")
    suspend fun getTopArticles(
        @Query("apiKey") apiKey: String,
        @Query("action") action: String = "getArticles",
        @Query("resultType") resultType: String = "articles",
        @Query("articlesPage") page: Int = 1,
        @Query("articlesCount") count: Int = 20,
        @Query("articlesSortBy") sortBy: String = "date",
        @Query("articlesSortByAsc") sortByAsc: Boolean = false,
        @Query("lang") lang: String = "eng",
        @Query("keyword") keyword: String? = null
    ): NewsResponse

    @GET("article/getArticle")
    suspend fun getArticleDetails(
        @Query("articleUri") articleUri: String,
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("resultType") resultType: String = "info"
    ): Map<String, Any>

    companion object {
        const val BASE_URL = "https://eventregistry.org/api/v1/"
        const val API_KEY = "18d26111-b84a-424f-b65c-63803d0fa802"
    }
}
