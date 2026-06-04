package com.a3solution.theshortnews.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE (:keyword IS NULL OR title LIKE '%' || :keyword || '%' OR body LIKE '%' || :keyword || '%') ORDER BY date DESC")
    fun getAllArticles(keyword: String? = null): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Transaction
    suspend fun refreshArticles(articles: List<ArticleEntity>) {
        clearAll()
        insertArticles(articles)
    }
}
