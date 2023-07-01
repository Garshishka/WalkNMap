package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map

class MapCameraListener(private val onMapInteractionListener: OnMapInteractionListener) : CameraListener {
    var followUserLocation = false

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
               onMapInteractionListener.setAnchor()
                followUserLocation = false
            }
            onMapInteractionListener.cameraMoved()
        } else {
            if (!followUserLocation) {
                onMapInteractionListener.noAnchor()
            }
        }
    }
}
