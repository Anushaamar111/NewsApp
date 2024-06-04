package com.example.newsapp.db

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.newsapp.models.Article
import java.util.concurrent.locks.Lock

@Database
    (
            entities = [Article::class],
            version = 1
            )
@TypeConverters(Converter::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDAO(): DAO

    companion object{
        @Volatile
        private var instance: ArticleDatabase?=null
        private val Lock = Any()

        operator fun invoke(context: Context) = instance?: synchronized(Lock){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private  fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase:: class.java,
                "article_db.db"
            ).build()

    }

}