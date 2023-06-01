package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.garshishka.walknmap.data.PointRepository

class ViewModelFactory(private val repository: PointRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                return MainViewModel(repository = repository) as T
            else ->
                throw java.lang.IllegalArgumentException("unknown ViewModel class ${modelClass.name}")
        }
    }
}