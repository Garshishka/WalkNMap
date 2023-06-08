package ru.garshishka.walknmap.di

import android.content.Context
import androidx.room.Room
import ru.garshishka.walknmap.data.PointRepository
import ru.garshishka.walknmap.db.AppDb

class DependencyContainer private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: DependencyContainer? = null

        fun initApp(context: Context) {
            instance = DependencyContainer(context)
        }

        fun getInstance(): DependencyContainer {
            return instance!!
        }
    }

    private val appDb =
        Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val gridPointDao = appDb.mapPointDao()

    val repository: PointRepository = PointRepository(gridPointDao)
}