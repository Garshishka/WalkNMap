package ru.garshishka.walknmap.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import ru.garshishka.walknmap.data.checkSquareFirstCorner

class RemoveMapObjectByPoint(val onMapInteractionListener: OnMapInteractionListener, val point: Point) : MapObjectVisitor {
    override fun onPlacemarkVisited(mapObject: PlacemarkMapObject) {
        if(mapObject.geometry.latitude == point.latitude && mapObject.geometry.longitude == point.longitude) {
            onMapInteractionListener.removeMapObject(mapObject)
        }
    }

    override fun onPolylineVisited(p0: PolylineMapObject) {
    }

    override fun onPolygonVisited(polygon: PolygonMapObject) {
        if(point.checkSquareFirstCorner(polygon.geometry.outerRing.points[0])){
            onMapInteractionListener.removePolygon(polygon)
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