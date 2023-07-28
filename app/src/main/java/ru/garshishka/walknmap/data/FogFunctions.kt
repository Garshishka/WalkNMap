package ru.garshishka.walknmap.data

import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import ru.garshishka.walknmap.*
import kotlin.math.round

//To curb rounding errors in doubles this function makes integers(using ROUNDER), rounds them up and
//then divides them using the same Rounder. For points in square corners ROUNDERS are doubled
fun Double.roundForCoordinates(forLat: Boolean, forCorners: Boolean = false): Double =
    round(this * (if (forLat) LAT_ROUNDER else LON_ROUNDER) * (if (forCorners) 2 else 1)) /
            ((if (forLat) LAT_ROUNDER else LON_ROUNDER) * (if (forCorners) 2 else 1))

//Makes from list of points matrix that is rows+2 x columns+2 with 1 in point places
fun List<MapPoint>.makePointMatrix(minPoint: MapPoint, rows: Int, cols: Int): Array<IntArray> {
    return Array(rows + 2) { r ->
        IntArray(cols + 2) { c ->
            if (r == 0 || c == 0 || r == rows + 1 || c == cols + 1) {
                //this matrix has a border of 0's for the next step (wall matrix)
                0
            } else {
                if (this.contains(
                        //beginning from most left and bottom point
                        MapPoint(
                            (minPoint.lat + ((r - 1) * DOUBLE_LAT_ADJUSTMENT)).roundForCoordinates(
                                true
                            ),
                            (minPoint.lon + ((c - 1) * DOUBLE_LON_ADJUSTMENT)).roundForCoordinates(
                                false
                            )
                        )
                    )
                ) {
                    1
                } else {
                    0
                }
            }
        }
    }
}

//Makes from 1 and 0 matrix a matrix that shows vertical and horizontal continuous walls
fun Array<IntArray>.makeWallsMatrix(rows: Int, cols: Int): Array<IntArray> =
    Array(rows + 2) { r ->
        IntArray(cols + 2) { c ->
            if (r == rows + 1 || c == cols + 1) {
                //Right and top border are ignored
                0
            } else {
                //If element on top of this element is not the same - this element will be 1
                //if the next one is not the same - this element will be 2
                //if both of them are different from this one - 3
                if (this[r][c] != this[r][c + 1]) {
                    2
                } else {
                    0
                } +
                        if (this[r][c] != this[r + 1][c]) {
                            1
                        } else {
                            0
                        }
            }//1 - there is a horizontal wall between this element and one on top of it
        } //2 - there is a vertical wall between this element and the next one
    }//3 - there is a vertical and a horizontal wall

//From wall matrix we create a list of corner points of all polygons in the order of drawing
fun Array<IntArray>.makePolygonPointsLists(
    rows: Int,
    cols: Int
): MutableList<MutableList<MatrixPoint>> {
    val result = mutableListOf<MutableList<MatrixPoint>>()

    //Special matrix that let us detect two squares that only touch each other with one corner
    val indexMatrix = Array(rows + 2) {
        IntArray(cols + 2) { -1 }
    }

    for (r in 0..rows + 1) {
        for (c in 0..cols + 1) {
            if ((this[r][c] and 1) == 1) {
                var i = r + 1
                var j = c
                var cycle = mutableListOf(MatrixPoint(i, j))
                indexMatrix[i][j] = 0
                //Using bitwise operator we check if element before this one has a vertical wall
                //then we check element before and below this one
                //then we check element directly below this one for a horizontal wall
                //and lastly we check element before and below this one for a horizontal wall
                while (true) {
                    if (i < rows + 1 && (this[i][j - 1] and 2) == 2) {
                        this[i][j - 1] -= 2
                        i++
                    } else if (i > 0 && (this[i - 1][j - 1] and 2) == 2) {
                        this[i - 1][j - 1] -= 2
                        i--
                    } else if (j < cols + 1 && (this[i - 1][j] and 1 == 1)) {
                        this[i - 1][j] -= 1
                        j++
                    } else if (j > 0 && (this[i - 1][j - 1] and 1 == 1)) {
                        this[i - 1][j - 1] -= 1
                        j--
                    } else {
                        break
                    }
                    //When we find walls we subtract 1 or 2 for horizontal or vertical walls
                    //the we "move along" the wall to the next point and check again
                    //when we can't find new walls this while ends
                    cycle.add(MatrixPoint(i, j))
                    //Cycle stores "visited" points
                    val ix = indexMatrix[i][j]
                    if (ix >= 0) {
                        //When we get not -1 in index - it means we made a loop to already visited
                        //and the ix number shows us how may points are not in this loop
                        result.add(cycle.subList(ix, cycle.size).removeConnectingPoints())
                        //we get one polygon point list (and remove unnecessary points from it)
                        cycle.subList(ix, cycle.size).forEach {
                            //for taken out points we make their index -1 again
                            indexMatrix[it.lat][it.lon] = -1
                        }
                        //points that were not in the loop but already visited are left in the cycle
                        cycle = cycle.slice(0..ix) as MutableList<MatrixPoint>
                    }
                    //In index matrix we save count of visited elements in element coordinates
                    //it helps us determine how many points et in one loop
                    indexMatrix[i][j] = cycle.size - 1
                }
            }
        }
    }
    return result
}

//Considering we only need corner points to make a polygon, this function removes unnecessary points
fun List<MatrixPoint>.removeConnectingPoints(): MutableList<MatrixPoint> {
    //Flag that shows are we moving horizontally or vertically
    //every corner we change the axis so the flag changes accordingly
    var latMovement = true
    return this.filterIndexed { i, point ->
        //Excluding last and first points we remove points that have same lat on horizontal movement
        //and same lon on vertical
        if (i == 0 || i == this.size - 1) {
            true
        } else if ((point.lat != this[i + 1].lat && !latMovement) || (latMovement && point.lon != this[i + 1].lon)) {
            latMovement = !latMovement
            true
        } else {
            false
        }
    }.toMutableList()
}

fun List<MatrixPoint>.isInsideOtherPolygon(other: List<MatrixPoint>): PolygonState {
    //Considering that way our system is setup all of the polygon points must be inside another one
    //So we only check first point against other polygons
    var firstPointState = this.first().isInsideOtherPolygon(other)
    //two polygons that share one point can be sometimes designated as interlocked
    //so for this we check if two points or more are not the same as first one
    var oneTimeNotTheSame = false
    for (i in 1..this.size - 2) {
        //But because of one way our point algorithm can fail we can get interlocked polygons
        //To capture them we have to check every other point of the polygon
        //if its different from the first point - they are interlocked
        //but only if this happens 2 times or more
        val position = this[i].isInsideOtherPolygon(other)
        if (position != PolygonState.CORNER && position != firstPointState) {
            //if point was a corner point with other polygon it can get troublesome so we skip it
            //and in case the first point was a corner - we make first not corner point the base
            if(firstPointState == PolygonState.CORNER){
                firstPointState = position
            } else {
                if (oneTimeNotTheSame) {
                    return PolygonState.INTERLOCKED
                } else {
                    oneTimeNotTheSame = true
                }
            }
        }
    }
    return firstPointState
}

fun MutableList<MutableList<MatrixPoint>>.separateInsidePolygons(): MutableList<MutableList<MatrixPoint>> {
    val insidePolygons = mutableListOf<MutableList<MatrixPoint>>()

    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        val polygon = iterator.next()
        run breaking@{
            this.filterNot { it == polygon }.forEach { other ->
                when (polygon.isInsideOtherPolygon(other)) {
                    PolygonState.INSIDE -> {
                        iterator.remove()
                        insidePolygons.add(polygon)
                        return@breaking
                    }
                    PolygonState.INTERLOCKED -> {
                        (polygon to other).resolveIntersection()
                        iterator.remove()
                        insidePolygons.add(polygon)
                        return@breaking
                    }
                    else -> {}
                }
            }
        }
    }
    return insidePolygons
}

fun List<List<MatrixPoint>>.makeLinearRing(minPoint: MapPoint): ArrayList<LinearRing> =
    //Here the int matrix once again becomes doubles lat and lon for the map
    ArrayList(this.map { list ->
        LinearRing(list.map {
            Point(
                ((minPoint.lat + ((it.lat - 1) * 2 * LAT_ADJUSTMENT)) - LAT_ADJUSTMENT).roundForCoordinates(
                    true, true
                ),
                (minPoint.lon + ((it.lon - 1) * 2 * LON_ADJUSTMENT) - LON_ADJUSTMENT).roundForCoordinates(
                    false, true
                )
            )
        })
    })