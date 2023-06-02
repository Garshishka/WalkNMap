package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus

class LocationChangeListener(val onMapInteractionListener: OnMapInteractionListener):LocationListener {
    var locationStatusOk = false

    override fun onLocationUpdated(location: Location) {
        if(locationStatusOk) {
            println("LL: ${location.position.latitude} - ${location.position.longitude} | ${location.speed} | ${location.relativeTimestamp} | ${location.accuracy}")
            onMapInteractionListener.userMoved()
        }
    }

    override fun onLocationStatusUpdated(status: LocationStatus) {
        locationStatusOk = status == LocationStatus.AVAILABLE
        println("location status - ${status.toString()}")
    }
}