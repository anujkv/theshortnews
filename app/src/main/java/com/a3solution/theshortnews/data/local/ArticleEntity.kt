package com.a3solution.theshortnews.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a3solution.theshortnews.data.model.Article
import com.a3solution.theshortnews.data.model.Source

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val uri: String,
    val title: String?,
    val body: String?,
    val image: String?,
    val sourceTitle: String?,
    val date: String?
)

fun Article.toEntity(): ArticleEntity? {
    val articleUri = uri ?: return null
    return ArticleEntity(
        uri = articleUri,
        title = title,
        body = body,
        image = image,
        sourceTitle = source?.title,
        date = date
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        uri = uri,
        title = title,
        body = body,
        image = image,
        source = Source(title = sourceTitle),
        date = date
    )
}
