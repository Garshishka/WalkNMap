package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.map.*
import ru.garshishka.walknmap.data.AreaCoordinates
import ru.garshishka.walknmap.data.getCentralPoint
import ru.garshishka.walknmap.data.isPointOutside

class RemovePolygonsOutsiderArea(
    private val mapObjectCollection: MapObjectCollection,
    private val boundingArea: AreaCoordinates
) : MapObjectVisitor {
    override fun onPlacemarkVisited(mapObject: PlacemarkMapObject) {
    }

    override fun onPolylineVisited(p0: PolylineMapObject) {
    }

    override fun onPolygonVisited(polygon: PolygonMapObject) {
        if (boundingArea.isPointOutside(polygon.getCentralPoint())) {
            mapObjectCollection.remove(polygon)
        }
    }

    override fun onCircleVisited(p0: CircleMapObject) {
    }

    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
        return true
    }

    override fun onCollectionVisitEnd(p0: MapObjectCollection) {
    }

    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
        return true
    }

    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
    }
}