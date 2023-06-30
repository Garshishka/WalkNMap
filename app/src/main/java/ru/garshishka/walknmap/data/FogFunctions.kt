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
                            (minPoint.lat + ((r - 1) * DOUBLE_LAT_ADJUSTMENT)).roundForCoordinates(true),
                            (minPoint.lon + ((c - 1) * DOUBLE_LON_ADJUSTMENT)).roundForCoordinates(false)
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
): MutableList<List<Pair<Int, Int>>> {
    val result = mutableListOf<List<Pair<Int, Int>>>()

    //Special matrix that let us detect two squares that only touch each other with one corner
    val indexMatrix = Array(rows + 2) { r ->
        IntArray(cols + 2) { c -> -1 }
    }

    for (r in 0..rows + 1) {
        for (c in 0..cols + 1) {
            if ((this[r][c] and 1) == 1) {
                var i = r + 1
                var j = c
                var cycle = mutableListOf((i to j))
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
                    cycle.add(i to j)
                    //Cycle stores "visited" points
                    val ix = indexMatrix[i][j]
                    if (ix >= 0) {
                        //When we get not -1 in index - it means we made a loop to already visited
                        //and the ix number shows us how may points are not in this loop
                        result.add(cycle.subList(ix, cycle.size).removeConnectingPoints())
                        //we get one polygon point list (and remove unnecessary points from it)
                        cycle.subList(ix, cycle.size).forEach {
                            //for taken out points we make their index -1 again
                            indexMatrix[it.first][it.second] = -1
                        }
                        //points that were not in the loop but already visited are left in the cycle
                        cycle = cycle.slice(0..ix) as MutableList<Pair<Int, Int>>
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
fun List<Pair<Int, Int>>.removeConnectingPoints(): List<Pair<Int, Int>> {
    //Flag that shows are we moving horizontally or vertically
    //every corner we change the axis so the flag changes accordingly
    var latMovement = true
    return this.filterIndexed { i, point ->
        //Excluding last and first points we remove points that have same lat on horizontal movement
        //and same lon on vertical
        if (i == 0 || i == this.size - 1) {
            true
        } else if ((point.first != this[i + 1].first && !latMovement) || (latMovement && point.second != this[i + 1].second)) {
            latMovement = !latMovement
            true
        } else {
            false
        }
    }
}

fun List<Pair<Int, Int>>.isInsideOtherPolygon(other: List<Pair<Int, Int>>): Boolean {
    var isInside = false
    //Considering that way our system is setup all of the polygon pointa must be inside another one
    //So we only check first point against other polygons
    val firstPoint = this.first()
    var j = other.size - 1

    //This algorithm is based on quick version of raytracing algorithm
    //Points sends a "ray" and "count" every time this ray crosses other polygon walls
    //If the number of crosses is odd - point is inside. Even - it is not
    for (i in 0..other.size - 1) {
        if ((other[i].second > firstPoint.second) != (other[j].second > firstPoint.second)) {
            if (firstPoint.first < ((other[j].first - other[i].first) * (firstPoint.first - other[i].second) / (other[j].second - other[i].second) + other[i].first)) {
                isInside = !isInside
            }
        }
        j = i
    }
    return isInside
}

fun MutableList<List<Pair<Int, Int>>>.separateInsidePolygons(): MutableList<List<Pair<Int, Int>>> {
    val insidePolygons = mutableListOf<List<Pair<Int, Int>>>()

    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        val polygon = iterator.next()
        run breaking@{
            this.filterNot { it == polygon }.forEach { other ->
                if (polygon.isInsideOtherPolygon(other)) {
                    iterator.remove()
                    insidePolygons.add(polygon)
                    return@breaking
                }
            }
        }
    }

    return insidePolygons
}

fun MutableList<List<Pair<Int, Int>>>.makeLinearRing(minPoint: MapPoint): ArrayList<LinearRing> =
    //Here the int matrix once again becomes doubles lat and lon for the map
    ArrayList(this.map { list ->
        LinearRing(list.map {
            Point(
                ((minPoint.lat + ((it.first - 1) * 2 * LAT_ADJUSTMENT)) - LAT_ADJUSTMENT).roundForCoordinates(
                    true, true
                ),
                (minPoint.lon + ((it.second - 1) * 2 * LON_ADJUSTMENT) - LON_ADJUSTMENT).roundForCoordinates(
                    false, true
                )
            )
        })
    })
