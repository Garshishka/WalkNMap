package ru.garshishka.walknmap.ui

import ru.garshishka.walknmap.data.GridPoint

interface OnInteractionListener {
    fun onPlaceClick(place: GridPoint){}

    fun onEditClick(place: GridPoint){}

    fun onDeleteClick(place: GridPoint){}
}