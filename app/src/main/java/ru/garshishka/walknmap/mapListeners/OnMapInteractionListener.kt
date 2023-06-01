package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject

interface OnMapInteractionListener {
    /*fun onMapLongClick(point: Point){}*/ //TODO Probably useless. Delete

    fun removeMapObject(mapObject: PlacemarkMapObject){}

    fun onMarkClick(id: Long, point: Point){}

    fun setAnchor(){}

    fun noAnchor(){}
}