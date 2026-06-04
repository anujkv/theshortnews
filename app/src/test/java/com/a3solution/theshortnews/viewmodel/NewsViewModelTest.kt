package com.a3solution.theshortnews.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.repository.NewsRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    private val application = mockk<Application>(relaxed = true)
    private val repository = mockk<NewsRepository>()
    private lateinit var viewModel: NewsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mocking the initial load in the init block
        coEvery { repository.getTopArticles() } returns flowOf(emptyList())
        
        viewModel = NewsViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialLoad updates articles state`() = runTest {
        val mockArticles = listOf(
            Article(uri = "1", title = "Test Article 1", body = "Body 1", image = null, source = null, date = null),
            Article(uri = "2", title = "Test Article 2", body = "Body 2", image = null, source = null, date = null)
        )
        coEvery { repository.getTopArticles() } returns flowOf(mockArticles)

        // We need to re-initialize because initialLoad is called in init
        viewModel = NewsViewModel(application, repository)
        testDispatcher.scheduler.runCurrent()

        viewModel.articles.test {
            assertEquals(mockArticles, awaitItem())
        }
    }

    @Test
    fun `fetchNews with query updates articles state`() = runTest {
        val query = "android"
        val mockArticles = listOf(
            Article(uri = "1", title = "Android News", body = "Body", image = null, source = null, date = null)
        )
        coEvery { repository.getTopArticles(query) } returns flowOf(mockArticles)

        viewModel.fetchNews(query)
        testDispatcher.scheduler.runCurrent()

        viewModel.articles.test {
            assertEquals(mockArticles, awaitItem())
        }
        
        coVerify { repository.getTopArticles(query) }
    }

    @Test
    fun `isLoading state updates correctly during fetchNews`() = runTest {
        coEvery { repository.getTopArticles(any()) } returns flowOf(emptyList())

        viewModel.isLoading.test {
            // Initial state should be false (after setup's init call)
            assertEquals(false, awaitItem())
            
            viewModel.fetchNews("query")
            
            // Advance until isLoading is set to true
            testDispatcher.scheduler.runCurrent()
            assertEquals(true, awaitItem())
            
            // Advance until flow is collected and isLoading set back to false
            testDispatcher.scheduler.runCurrent()
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `refreshNews updates articles state`() = runTest {
        val mockArticles = listOf(
            Article(uri = "1", title = "New Article", body = "Body", image = null, source = null, date = null)
        )
        coEvery { repository.getTopArticles() } returns flowOf(mockArticles)

        viewModel.refreshNews()
        testDispatcher.scheduler.runCurrent()

        viewModel.articles.test {
            assertEquals(mockArticles, awaitItem())
        }
    }
}
