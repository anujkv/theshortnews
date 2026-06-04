package com.a3solution.theshortnews.data.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("articles")
    val articles: Articles?
)

data class Articles(
    @SerializedName("results")
    val results: List<Article>?
)

data class Article(
    @SerializedName("uri")
    val uri: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("body")
    val body: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("source")
    val source: Source?,
    @SerializedName("date")
    val date: String?
)

data class Source(
    @SerializedName("title")
    val title: String?
)
