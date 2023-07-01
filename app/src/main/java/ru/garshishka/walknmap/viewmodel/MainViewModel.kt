package ru.garshishka.walknmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObjectCollection
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.AreaCoordinates
import ru.garshishka.walknmap.data.MapPoint
import ru.garshishka.walknmap.data.PointRepository
import ru.garshishka.walknmap.data.addSquare

private val emptyPoints = mutableListOf<MapPoint>()

class MainViewModel(private val repository: PointRepository) : ViewModel() {
    private val _pointList = MutableLiveData(emptyPoints)
    val pointList: LiveData<MutableList<MapPoint>>
        get() = _pointList

    var oldPointList: List<MapPoint> = emptyList()

    private var _loadingMap = MutableLiveData(false)
    val loadingMap: LiveData<Boolean>
        get() = _loadingMap

    var pointJustAdded = false


    fun getPointsOnScreen(area: AreaCoordinates) = viewModelScope.launch {
        _loadingMap.value = true
        _pointList.value?.let { oldPointList = it }
        _pointList.value =
            repository.getPointsInArea(area.minLat, area.maxLat, area.minLon, area.maxLon)
                .toMutableList()
    }

    fun changeLoadingState(state: Boolean) {
        _loadingMap.value = state
    }

    fun save(point: MapPoint) = viewModelScope.launch {
        repository.save(point)
        pointJustAdded = true
        _pointList.value!!.add(point)
    }

    fun delete(point: Point) {
        repository.delete(point.latitude, point.longitude)
    }

    fun getPoint(point: Point): Point? = repository.getPoint(point)

    fun addSquare(mapObjectCollection: MapObjectCollection, point: Point) = viewModelScope.launch {
        point.addSquare(mapObjectCollection, true)
    }
}