package com.a3solution.theshortnews.data.repository

import app.cash.turbine.test
import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.model.Articles
import com.a3solution.theshortnews.data.model.NewsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    private val apiService = mockk<NewsApiService>()
    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        repository = NewsRepository(apiService)
    }

    @Test
    fun `getTopArticles returns list of articles on success`() = runTest {
        val mockArticles = listOf(
            Article(uri = "1", title = "Title 1", body = "Body 1", image = null, source = null, date = null),
            Article(uri = "2", title = "Title 2", body = "Body 2", image = null, source = null, date = null)
        )
        val mockResponse = NewsResponse(articles = Articles(results = mockArticles))
        
        coEvery { 
            apiService.getTopArticles(apiKey = any(), keyword = any()) 
        } returns mockResponse

        repository.getTopArticles("test").test {
            assertEquals(mockArticles, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getTopArticles returns empty list on api failure`() = runTest {
        coEvery { 
            apiService.getTopArticles(apiKey = any(), keyword = any()) 
        } throws Exception("Network error")

        repository.getTopArticles().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getTopArticles returns empty list when response results are null`() = runTest {
        val mockResponse = NewsResponse(articles = Articles(results = null))
        
        coEvery { 
            apiService.getTopArticles(apiKey = any(), keyword = any()) 
        } returns mockResponse

        repository.getTopArticles().test {
            val result = awaitItem()
            assertTrue(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getArticleDetails returns null as placeholder`() = runTest {
        // Currently getArticleDetails returns flow { emit(null) }
        repository.getArticleDetails("some-uri").test {
            assertEquals(null, awaitItem())
            awaitComplete()
        }
    }
}
