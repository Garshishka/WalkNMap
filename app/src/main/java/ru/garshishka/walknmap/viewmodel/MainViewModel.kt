package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.GridPoint
import ru.garshishka.walknmap.data.PointRepository

private val empty = GridPoint(Point(0.0, 0.0))

class MainViewModel(private val repository: PointRepository) : ViewModel() {
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun empty() {
        edited.value = empty
    }

    fun save(point: Point, enabled: Boolean = false) = viewModelScope.launch {
        edited.value?.let {
            edited.value = it.copy(point, enabled)
        }
        edited.value?.let {
            repository.save(it)
        }
        empty()
    }

    fun delete(point: Point) {
        repository.delete(point.latitude, point.longitude)
    }

    fun getPoint(point: Point): GridPoint = repository.getPoint(point)
}