package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map

class MapInputListener(val onMapInteractionListener: OnMapInteractionListener) : InputListener {
    override fun onMapTap(map: Map, point: Point) {
    }

    override fun onMapLongTap(map: Map, point: Point) {
        //onMapInteractionListener.onMapLongClick(point) //TODO Probably useless. Delete
    }
}