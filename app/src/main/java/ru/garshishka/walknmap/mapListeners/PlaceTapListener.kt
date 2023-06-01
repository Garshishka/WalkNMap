package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener

class PlaceTapListener(val onMapInteractionListener: OnMapInteractionListener):MapObjectTapListener {
    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        onMapInteractionListener.onMarkClick(mapObject.userData as Long, point)
        return true
    }
}