package ru.garshishka.walknmap.mapListeners

import android.graphics.Color
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.*
import ru.garshishka.walknmap.FOG_COLOR
import ru.garshishka.walknmap.data.equalsOtherPolygon

class RedrawInsidePolygons(
    private val mapObjectCollection: MapObjectCollection,
    private val thisPolygon: Polygon,
    private val deleteThis: Boolean = false,
) : MapObjectVisitor {
    var mapCollectionHasPolygon = false
    override fun onPlacemarkVisited(mapObject: PlacemarkMapObject) {
    }

    override fun onPolylineVisited(p0: PolylineMapObject) {
    }

    override fun onPolygonVisited(polygon: PolygonMapObject) {
        if (polygon.geometry.equalsOtherPolygon(thisPolygon)) {
            if (deleteThis) {
                mapObjectCollection.remove(polygon)
            } else {
                mapCollectionHasPolygon = true
            }
        }
    }

    override fun onCircleVisited(p0: CircleMapObject) {
    }

    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
        return true
    }

    override fun onCollectionVisitEnd(mapObjectCollection: MapObjectCollection) {
        if (!mapCollectionHasPolygon) {
            mapObjectCollection.addPolygon(thisPolygon)
                .also {
                    it.fillColor = FOG_COLOR
                    it.strokeColor = Color.TRANSPARENT
                }
        }
    }

    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
        return true
    }

    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
    }
}