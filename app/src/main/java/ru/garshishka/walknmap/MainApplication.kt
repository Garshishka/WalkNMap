package ru.garshishka.walknmap

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.garshishka.walknmap.di.DependencyContainer

class MainApplication : Application() {

    override fun onCreate() {
        MapKitFactory.setApiKey(API_KEY)
        DependencyContainer.initApp(this)
        super.onCreate()
    }
}
