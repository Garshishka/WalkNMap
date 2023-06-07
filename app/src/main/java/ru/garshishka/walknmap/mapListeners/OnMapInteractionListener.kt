package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject

interface OnMapInteractionListener {
    /*fun onMapLongClick(point: Point){}*/ //TODO Probably useless. Delete
    fun cameraMoved()
    fun userMoved()
    fun removeMapObject(mapObject: PlacemarkMapObject){}
    fun removePolygon(polygonMapObject: PolygonMapObject)
    fun onMarkClick(id: Long, point: Point){}
    fun setAnchor()
    fun noAnchor()
}