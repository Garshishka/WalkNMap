package ru.garshishka.walknmap.mapListeners

import android.graphics.Color
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView

class MapUserLocationListener(val onMapInteractionListener: OnMapInteractionListener): UserLocationObjectListener {
    override fun onObjectAdded(userLocationListener: UserLocationView) {
        userLocationListener.accuracyCircle.fillColor = Color.TRANSPARENT
    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        onMapInteractionListener.userMoved()
    }
    override fun onObjectRemoved(p0: UserLocationView) {

    }
}
