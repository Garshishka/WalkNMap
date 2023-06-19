package ru.garshishka.walknmap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.LayerIds
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.coroutines.launch
import ru.garshishka.walknmap.data.*
import ru.garshishka.walknmap.databinding.ActivityMainBinding
import ru.garshishka.walknmap.di.DependencyContainer
import ru.garshishka.walknmap.mapListeners.*
import ru.garshishka.walknmap.ui.OnInteractionListener
import ru.garshishka.walknmap.ui.PlacesAdapter
import ru.garshishka.walknmap.viewmodel.MainViewModel
import ru.garshishka.walknmap.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    //Dependency part
    private val container = DependencyContainer.getInstance()

    //Permission part
    private var locationPermission = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationPermission = true
                if (!this::userLocationLayer.isInitialized) {
                    setUpUserPosition()
                }
            } else {
                showPermissionSnackbar()
            }
        }

    //Listeners part
    private val onInteractionListener = object : OnInteractionListener {
        override fun onPlaceClick(place: MapPoint) {
            moveMap(place.toYandexPoint())
        }

        override fun onDeleteClick(place: MapPoint) {
            traverseMapObjectsToRemove(place.toYandexPoint())
        }

    }
    private val onMapInteractionListener = object : OnMapInteractionListener {
        override fun cameraMoved() {
            //if we have a new screen area - we change the set of visible squares
            if (tryToChangeScreenMapArea()) {
                viewModel.getPointsOnScreen(mapScreenArea)
            }
        }

        override fun userMoved() {
            addUserLocation()
        }

        override fun onMarkClick(id: Long, point: Point) {
            val place = viewModel.getPoint(point)
            place?.let {
                moveMap(it)
            }
        }

        override fun setAnchor() {
            userLocationLayer.setAnchor(
                PointF(
                    (binding.mapView.width() * 0.5).toFloat(),
                    (binding.mapView.height() * 0.5).toFloat()
                ),
                PointF(
                    (binding.mapView.width() * 0.5).toFloat(),
                    (binding.mapView.height() * 0.83).toFloat()
                )
            )
        }

        override fun noAnchor() {
            if (this@MainActivity::userLocationLayer.isInitialized) {
                userLocationLayer.resetAnchor()
                userLocationLayer.isHeadingEnabled = false
            }
        }
    }

    //UI and data part
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels {
        ViewModelFactory(container.repository)
    }
    private var mapScreenArea = AreaCoordinates(0.0, 1.0, 0.0, 1.0)
    private var boundingFogArea = AreaCoordinates(0.0, 0.0, 0.0, 0.0)

    //Map part
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var boundingFogObjectCollection: MapObjectCollection
    private lateinit var userLocationLayer: UserLocationLayer
    private var locationManager: com.yandex.mapkit.location.LocationManager? = null
    private val cameraListener = MapCameraListener(onMapInteractionListener)
    private val mapInputListener = MapInputListener(onMapInteractionListener)
    private val userObjectListener = MapUserLocationListener()
    private val locationListener = LocationChangeListener(onMapInteractionListener)

    private lateinit var boundingPolygon: PolygonMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkMapPermission()
        MapKitFactory.initialize(this)

        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setUpMap()
        subscribe()
    }

    private fun setUpMap() {
        binding.mapView.map.apply {
            mapObjectCollection = this.mapObjects.addCollection()
            boundingFogObjectCollection = this.mapObjects.addCollection()
            val sublayerManager = this.sublayerManager
            sublayerManager.findFirstOf(LayerIds.getMapObjectsLayerId())
                ?.let { sublayerManager.moveToEnd(it) }

            if (locationPermission) {
                setUpUserPosition()
            }

            locationManager = MapKitFactory.getInstance().createLocationManager()

            val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
            val lastLat = sharedPref.getFloat("lastLat", 0.0f).toDouble()
            val lastLon = sharedPref.getFloat("lastLon", 0.0f).toDouble()
            moveMap(Point(lastLat, lastLon))

            addCameraListener(cameraListener)
            addInputListener(mapInputListener)
        }
    }

    private fun setUpUserPosition() {
        userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = false
        userLocationLayer.setObjectListener(userObjectListener)
        subscribeToLocationUpdate()
    }

    private fun subscribe() {
        binding.apply {
            findMyLocationFab.setOnClickListener {
                if (locationPermission) {
                    cameraListener.followUserLocation = true
                    cameraToUserPosition()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            headingFab.setOnClickListener {
                if (locationPermission) {
                    userLocationLayer.isHeadingEnabled = true
                    cameraListener.followUserLocation = userLocationLayer.isHeadingEnabled
                    cameraToUserPosition(18f)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            northFab.setOnClickListener {
                val cameraPosition = binding.mapView.map.cameraPosition
                moveMap(cameraPosition.target, cameraPosition.zoom, 0f, 0f)
            }

            placeListFab.setOnClickListener {
                placesView.isVisible = !placesView.isVisible
            }

            testButton1.setOnClickListener {
                viewModel.deletePointsOnNewSquareSize()
            }
            testButton2.setOnClickListener {
                mapView.isVisible = !mapView.isVisible
            }
        }

        val adapter = PlacesAdapter(onInteractionListener)
        binding.placesView.adapter = adapter

        viewModel.apply {
            pointList.observe(this@MainActivity) { places ->
                adapter.submitList(places)
                redrawScreenSquares()
            }
            loadingMap.observe(this@MainActivity){
                binding.loading.isVisible = it
            }
        }
    }

    fun moveMap(target: Point, zoom: Float = 17f, azimuth: Float = 0f, tilt: Float = 0f) {
        binding.mapView.map.move(
            CameraPosition(target, zoom, azimuth, tilt),
            Animation(Animation.Type.SMOOTH, 3f),
            null
        )
    }

    private fun addUserLocation() {
        userLocationLayer.cameraPosition()?.let {
            val target = it.target.roundCoordinates()
            if (viewModel.getPoint(target) == null) {
                viewModel.save(MapPoint(target.latitude, target.longitude))
                target.addSquare(mapObjectCollection)
            }
        }
    }

    private var emptyPointsPrevious = listOf<MapPoint>()
    private fun redrawScreenSquares() = lifecycleScope.launch {
        viewModel.pointList.value?.let { points ->
            if (DRAW_FOG) {
                if (points.isEmpty()) {
                    redrawBoundingBoxFog(AreaCoordinates(0.0, 0.0, 0.0, 0.0))
                } else {
                    if (redrawBoundingBoxFog(
                            AreaCoordinates(
                                points.minBy { it.lat }.lat,
                                points.maxBy { it.lat }.lat,
                                points.minBy { it.lon }.lon,
                                points.maxBy { it.lon }.lon,
                            )
                        )
                    ) {
                        mapObjectCollection.traverse(
                            RemovePolygonsOutsiderArea(
                                mapObjectCollection,
                                boundingFogArea
                            )
                        )
                        val emptyPointsNow =
                            boundingFogArea.makePointList().filterNot { points.contains(it) }
                        val k = emptyPointsNow.filterNot { emptyPointsPrevious.contains(it) }//.sortedByDescending { it.lon }
                        k.addVerticalLinesOfFog(mapObjectCollection,viewModel)
//                        emptyPointsNow.filterNot { emptyPointsPrevious.contains(it) }.forEach {
//                            viewModel.addSquare(mapObjectCollection, it.toYandexPoint())
//                        }
                        emptyPointsPrevious = emptyPointsNow
                        println(emptyPointsNow.size)
                    } else {
                    }
                }
            } else {
                //new points get squares
                points.filterNot { viewModel.oldPointList.contains(it) }.forEach {
                    it.toYandexPoint().addSquare(mapObjectCollection)
                }
                //squares we can't see anymore get deleted
                viewModel.oldPointList.filterNot { points.contains(it) }.forEach {
                    traverseMapObjectsToRemove(it.toYandexPoint())
                }
            }
        }
        viewModel.changeLoadingState(false)
    }

    private fun redrawBoundingBoxFog(newBoundingArea: AreaCoordinates): Boolean {
        return if (newBoundingArea != boundingFogArea) {
            boundingFogArea = newBoundingArea
            if (this::boundingPolygon.isInitialized) {
                boundingPolygon.geometry = Polygon(
                    boundingPolygon.geometry.outerRing,
                    boundingFogArea.makeInnerSquareForPolygon()
                )
            } else {
                boundingPolygon = boundingFogArea.makeBoundingPolygon(boundingFogObjectCollection)
            }
            true
        } else {
            false
        }
    }

    private fun traverseMapObjectsToRemove(point: Point) {
        mapObjectCollection.traverse(RemoveMapObjectByPoint(mapObjectCollection, point))
    }

    private fun cameraToUserPosition(zoom: Float = 17f) {
        if (userLocationLayer.cameraPosition() != null) {
            moveMap(userLocationLayer.cameraPosition()!!.target, zoom)
        } else {
            Snackbar.make(
                binding.mapView,
                getString(R.string.no_user_location_error),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.retry)) {
                    cameraToUserPosition()
                }
                .show()
        }
    }

    private fun tryToChangeScreenMapArea(): Boolean {
        //Taking a screen region and rounding it coordinates
        val cameraRegion = binding.mapView.map.visibleRegion
        val newMapArea = AreaCoordinates(
            cameraRegion.bottomLeft.latitude,
            cameraRegion.topRight.latitude,
            cameraRegion.bottomLeft.longitude,
            cameraRegion.topRight.longitude
        ).roundCoordinates()
        //Returning if the new screen region is different
        return if (mapScreenArea != newMapArea) {
            mapScreenArea = newMapArea
            true
        } else {
            false
        }
    }


    private fun checkMapPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationPermission = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionSnackbar()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onStop() {
        saveLastLocation()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        locationManager?.unsubscribe(locationListener)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveLastLocation()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
        if (locationPermission) {
            subscribeToLocationUpdate()
        }
    }

    private fun subscribeToLocationUpdate() { //TODO make in background use ON
        locationManager?.subscribeForLocationUpdates(
            0.0,
            0,
            5.0,
            false,
            FilteringMode.OFF,
            locationListener
        )
    }

    private fun saveLastLocation() {
        if (this::userLocationLayer.isInitialized) {
            if (userLocationLayer.cameraPosition() != null) {
                val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
                with(sharedPref.edit()) {
                    putFloat(
                        "lastLat",
                        userLocationLayer.cameraPosition()!!.target.latitude.toFloat()
                    )
                    putFloat(
                        "lastLon",
                        userLocationLayer.cameraPosition()!!.target.longitude.toFloat()
                    )
                    apply()
                }
            }
        }
    }

    private fun showPermissionSnackbar() {
        Snackbar.make(binding.mapView, getString(R.string.need_geolocation), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.permission)) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .show()
    }
}