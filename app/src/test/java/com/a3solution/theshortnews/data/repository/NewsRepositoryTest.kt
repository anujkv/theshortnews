package com.a3solution.theshortnews.data.repository

import app.cash.turbine.test
import com.a3solution.theshortnews.data.api.NewsApiService
import com.a3solution.theshortnews.data.local.ArticleDao
import com.a3solution.theshortnews.data.local.ArticleEntity
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.model.Articles
import com.a3solution.theshortnews.data.model.NewsResponse
import com.a3solution.theshortnews.utils.NetworkUtils
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryTest {

    private val apiService = mockk<NewsApiService>()
    private val articleDao = mockk<ArticleDao>(relaxed = true)
    private val networkUtils = mockk<NetworkUtils>()
    private lateinit var repository: NewsRepository

    @Before
    fun setup() {
        repository = NewsRepository(apiService, articleDao, networkUtils)
    }

    @Test
    fun `getTopArticles emits from DB after refreshing from network when online`() = runTest {
        val mockArticles = listOf(
            Article(uri = "1", title = "Title 1", body = "Body 1", image = null, source = com.a3solution.theshortnews.data.model.Source(null), date = null)
        )
        val mockEntities = listOf(
            ArticleEntity(uri = "1", title = "Title 1", body = "Body 1", image = null, sourceTitle = null, date = null)
        )
        val mockResponse = NewsResponse(articles = Articles(results = mockArticles))
        
        val dbFlow = kotlinx.coroutines.flow.MutableSharedFlow<List<ArticleEntity>>(replay = 1)

        every { networkUtils.isNetworkAvailable() } returns true
        coEvery { apiService.getTopArticles(apiKey = any(), keyword = any()) } returns mockResponse
        every { articleDao.getAllArticles(any()) } returns dbFlow
        
        coEvery { articleDao.refreshArticles(any()) } coAnswers {
            dbFlow.emit(mockEntities)
        }

        repository.getTopArticles("test").test {
            assertEquals(mockArticles, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        coVerify { articleDao.refreshArticles(any()) }
    }

    @Test
    fun `getTopArticles emits from DB without network call when offline`() = runTest {
        val mockEntities = listOf(
            ArticleEntity(uri = "1", title = "Title 1", body = "Body 1", image = null, sourceTitle = null, date = null)
        )
        
        every { networkUtils.isNetworkAvailable() } returns false
        every { articleDao.getAllArticles(any()) } returns flowOf(mockEntities)

        repository.getTopArticles().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Title 1", result[0].title)
            awaitComplete()
        }
        
        coVerify(exactly = 0) { apiService.getTopArticles(any(), any()) }
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
