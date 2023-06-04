package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.PointRepository

private val empty = MapPoint(0.0, 0.0)
private val emptyPoints = emptyList<MapPoint>()

class MainViewModel(private val repository: PointRepository) : ViewModel() {
    val data = repository.getAll() //MutableLiveData(emptyPoints)//
    val edited = MutableLiveData(empty)

    fun getPointsOnScreen(minLat : Double, maxLat: Double, minLon: Double, maxLon: Double){
        //data.value = repository.getPointsInArea(minLat,maxLat,minLon,maxLon)
    }

    fun empty() {
        edited.value = empty
    }

    fun save(point: MapPoint, enabled: Boolean = false) = viewModelScope.launch {
        edited.value?.let {
            edited.value = point.copy(point.lat, point.lon)
        }
        edited.value?.let {
            repository.save(it)
        }
        empty()
    }

    fun delete(point: Point) {
        repository.delete(point.latitude, point.longitude)
    }

    fun getPoint(point: Point): Point? = repository.getPoint(point)
}