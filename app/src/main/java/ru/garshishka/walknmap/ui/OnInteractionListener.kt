package ru.garshishka.walknmap.ui

import ru.garshishka.walknmap.data.MapPoint


interface OnInteractionListener {
    fun onPlaceClick(place: MapPoint) {}

    fun onDeleteClick(place: MapPoint) {}
}