package com.example.newsapp.api
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.util.Constants.Companion.API_Key
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface news_api {
    @GET("v2/everything")
    suspend fun searchfornews(
        @Query("q")
        searchQuery: String,
       @Query("page")
       pageNumber: Int = 1,
        @Query("apiKey")
       apiKey: String = API_Key
    ): Response<NewsResponse>

    @GET("v2/top-headlines")
    suspend fun topHeadlines(
        @Query("country")
        countryCode: String = "in",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_Key
    ) : Response<NewsResponse>
}