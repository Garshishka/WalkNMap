package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.Point

data class GridPoint(
    val point: Point,
    val enabled: Boolean = false,
)
