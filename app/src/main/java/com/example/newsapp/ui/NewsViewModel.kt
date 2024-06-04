package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

import java.util.Locale.IsoCountryCode

class NewsViewModel(app: Application, val newsRepository: NewsRepository): AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null

    init {
        getHeadlines("in")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlines(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                headlinesPage++
                if (headlinesResponse == null) {
                    headlinesResponse = resultResponse
                } else {
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)

                }
                return Resource.Success(headlinesResponse ?: resultResponse)

            }
        }

        return Resource.Error(response.message())
    }
    private fun handleNewsSearchResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null || newSearchQuery != oldSearchQuery) {
                    searchNewsResponse = resultResponse
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                } else {
                    searchNewsPage++
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun addAllToFavorites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavoriteNews() = newsRepository.getFavoriteNews()
    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.DeleteArticle(article)
    }
    fun internetConnection(context: Context): Boolean{
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager
        return connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
            when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } ?: false
    }
   suspend fun headlinesInternet(countryCode: String) {
       headlines.postValue(Resource.Loading())
       try {
           if (internetConnection(this.getApplication())) {
               val response = newsRepository.getHeadlines(countryCode, headlinesPage)
               headlines.postValue(handleHeadlines(response))
           } else {
               headlines.postValue(Resource.Error("No Internet Connection"))
           }

       } catch (t: Throwable) {
           when (t) {
               is IOException -> headlines.postValue(Resource.Error("Network Failure"))
               else -> headlines.postValue(Resource.Error("Conversion Error"))
           }
       }
   }

    private suspend fun searchNewsInternet(searchQuery: String) {
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())) {
                val response = newsRepository.searchfornews(searchQuery, searchNewsPage)
                searchNews.postValue(handleNewsSearchResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
   }
        catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }

        }        }
}
