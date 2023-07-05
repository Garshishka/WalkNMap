package ru.garshishka.walknmap.data

data class PolygonSeparator(
    val insidePolygons: MutableList<List<MatrixPoint>> = mutableListOf(),
    val interlockedPolygons: MutableList<
            Pair<
                    List<MatrixPoint>,
                    List<MatrixPoint>
                    >
            > = mutableListOf()
)
