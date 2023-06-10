package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point

interface OnMapInteractionListener {
    /*fun onMapLongClick(point: Point){}*/ //TODO Probably useless. Delete
    fun cameraMoved()
    fun userMoved()
    fun onMarkClick(id: Long, point: Point){}
    fun setAnchor()
    fun noAnchor()
}