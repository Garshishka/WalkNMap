package ru.garshishka.walknmap

import android.graphics.Color

const val TOP_LAT = 89.3 //Higher than this and it's beginning to glitch
const val BOTTOM_LAT = -90.0
const val LEFT_LON = -180.0
const val RIGHT_LON = 180.0

var SQUARE_COLOR = Color.argb(60, 43, 255, 251)
var FOG_COLOR = Color.argb(200, 0, 0, 0)
var REDUCE_BLINKING = true
var DRAW_FOG = true
var LAT_ADJUSTMENT = 0.0005
var LON_ADJUSTMENT = 0.001
var DOUBLE_LAT_ADJUSTMENT = 0.001
var DOUBLE_LON_ADJUSTMENT = 0.002
var LAT_ROUNDER = 1000
var LON_ROUNDER = 500