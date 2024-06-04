package com.example.newsapp.repository

import com.example.newsapp.api.retrofit_instance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.models.Article
import retrofit2.Retrofit
import retrofit2.http.Query
import java.util.Locale.IsoCountryCode

class NewsRepository(val db: ArticleDatabase){

suspend fun getHeadlines(countryCode: String, pageNumber: Int)=
    retrofit_instance.api.topHeadlines(countryCode, pageNumber)

    suspend fun searchfornews(searchQuery: String, pageNumber: Int)=
        retrofit_instance.api.searchfornews(searchQuery,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDAO().upsert(article)

    fun getFavoriteNews()= db.getArticleDAO().getAllarticles()
    suspend fun DeleteArticle(article: Article) = db.getArticleDAO().DeleteArticle(article)
}