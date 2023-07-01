package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus

class LocationChangeListener(private val onMapInteractionListener: OnMapInteractionListener):LocationListener {
    private var locationStatusOk = false

    override fun onLocationUpdated(location: Location) {
        if(locationStatusOk) {
            onMapInteractionListener.userMoved()
        }
    }

    override fun onLocationStatusUpdated(status: LocationStatus) {
        locationStatusOk = status == LocationStatus.AVAILABLE
    }
}