package ru.garshishka.walknmap.data

//This functions separate two interlocked polygons
fun Pair<List<MatrixPoint>,List<MatrixPoint>>.resolveIntersection() : Pair<List<MatrixPoint>,List<MatrixPoint>>{
    val insideP = this.first
    val outsideP = this.second

    val interlockedLines: MutableList<MatrixLine> = mutableListOf()
    var oldStatus = insideP[0].isInsideOtherPolygon(outsideP)
    val insidePointsSet = mutableSetOf<MatrixPoint>()
    val outsidePointsSet = mutableSetOf<MatrixPoint>()
    //We divide the "inside" polygon points in inside and outside points
    //And also everytime it changes from inside to outside and vice versa we save
    //an intersecting line
    for (i in 0 until insideP.size) {
        val status = insideP[i].isInsideOtherPolygon(outsideP)
        println(status)
        if (status == PolygonState.INSIDE) {
            insidePointsSet.add(insideP[i])
        } else {
            outsidePointsSet.add(insideP[i])
        }
        if (oldStatus != status) {
            interlockedLines.add(insideP[i].lineWith(insideP[i - 1]))
        }
        oldStatus = status
    }
    //Points are put in a set first to avoid duplicating same last point
    //But than we convert them to Lists to be able to put elememnt on special indexes
    val outsidePoints = outsidePointsSet.toMutableList()
    val insidePoints = insidePointsSet.toMutableList()

    if (interlockedLines.size > 2) {
        //Maybe it can have more than two, but for now the scope is on two intersections
        throw Exception("Too much interlocking lines")
    }
    //From lines we get the points of intersection
    val intersectingPoints = mutableSetOf<MatrixPoint>()
    interlockedLines.forEach { intersectingPoints.add(it.findIntersectingPoint(outsideP)) }

    //And the we add them to our inside and outside points
    insidePoints.add(0, intersectingPoints.first())
    insidePoints.add(insidePoints.size, intersectingPoints.last())

    outsidePoints.add(0, intersectingPoints.last())
    outsidePoints.add(outsidePoints.size, intersectingPoints.first())

    //we get indexes where the separation should begin
    val intersectingIndexes =
        outsideP.getIntersectingIndexes(interlockedLines.first().horizontal, intersectingPoints)

    //Outside polygon gets outside points it has and the outside part of interlocked "inside" polygon
    val newOutsidePolygon =
        outsideP.subList(0, intersectingIndexes.first) + outsidePoints + outsideP.subList(
            intersectingIndexes.second,
            outsideP.size
        ) + outsideP.first()
    //Inside polygon gets the inside part of interlocked "inside" polygon and inside points from
    //outside polygon (those are between intersecting indexes)
    val newInsidePolygon = insidePoints + outsideP.subList(
        intersectingIndexes.first + 1,
        intersectingIndexes.second
    ) + insidePoints.first()

    return newInsidePolygon to newOutsidePolygon
}

//This function finds indexes of two points in the outside polygon
//First is the point after which the intersecting point should be(the second one in their list)
//Second is the point before which the intersecting point should be(the first one in their list)
//It does it by going trough polygon points in ascending order for the first one
//and descending order for the second one.
fun List<MatrixPoint>.getIntersectingIndexes(
    horizontalInterlock: Boolean,
    intersectingPoints: Set<MatrixPoint>
): Pair<Int, Int> {
    var afterFirst = 0
    var beforeLast = this.size - 1
    //Considering the orientation of interlocking lines it checks differently
    //It goes through points until it finds the first point after which we can put our intersecting point
    //And then the other way to find the last point before which we can put our intersecting point
    if (horizontalInterlock) {
        while (true) {
            if (checkIfPointBetweenHorizontal(
                    afterFirst,
                    afterFirst + 1,
                    intersectingPoints.last()
                )
            ) {
                break
            }
            afterFirst++
        }
        while (true) {
            if (checkIfPointBetweenHorizontal(
                    beforeLast,
                    beforeLast - 1,
                    intersectingPoints.first()
                )
            ) {
                break
            }
            beforeLast--
        }
    } else {
        while (true) {
            if (checkIfPointBetweenVertical(
                    afterFirst,
                    afterFirst + 1,
                    intersectingPoints.last()
                )
            ) {
                break
            }
            afterFirst++
        }
        while (true) {
            if (checkIfPointBetweenVertical(
                    beforeLast,
                    beforeLast - 1,
                    intersectingPoints.first()
                )
            ) {
                break
            }
            beforeLast--
        }
    }
    println("$afterFirst -- $beforeLast")
    return afterFirst to beforeLast
}


//this two next functions check first if this and next points are vertical or horizontal line
//then they check if intersecting point is on this line and between two points
fun List<MatrixPoint>.checkIfPointBetweenHorizontal(
    firstIndex: Int,
    secondIndex: Int,
    intersectingPoint: MatrixPoint
): Boolean =
    (this[firstIndex].lon == this[secondIndex].lon &&
            intersectingPoint.lon == this[firstIndex].lon &&
            ((this[firstIndex].lat..this[secondIndex].lat).contains(
                intersectingPoint.lat
            ) || (this[secondIndex].lat..this[firstIndex].lat).contains(
                intersectingPoint.lat
            ))
            )

fun List<MatrixPoint>.checkIfPointBetweenVertical(
    firstIndex: Int,
    secondIndex: Int,
    intersectingPoint: MatrixPoint
): Boolean =
    (this[firstIndex].lat == this[secondIndex].lat &&
            intersectingPoint.lat == this[firstIndex].lat &&
            ((this[firstIndex].lon..this[secondIndex].lon).contains(
                intersectingPoint.lon
            ) || (this[secondIndex].lon..this[firstIndex].lon).contains(
                intersectingPoint.lon
            ))
            )
