package com.cafe.dghackathon.shimhg02.dghack


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.annotation.MainThread
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.transition.Slide
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SlidingDrawer
import android.widget.TextView
import android.widget.Toast
import com.cafe.dghackathon.shimhg02.sharecafe.R
import com.cafe.dghackathon.shimhg02.sharecafe.R.*
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.text.NumberFormat

class FngFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
    private lateinit var lastLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPrep: SharedPreferences
    private lateinit var lookUpButton: FloatingActionButton
    private lateinit var rootView : View
    private lateinit var items : ArrayList<MarkerItem>
    private lateinit var tv_marker : TextView
    private lateinit var bike_title : TextView
    private lateinit var bike_snipt : TextView
    private lateinit var bike_recycler : RecyclerView
    private lateinit var bike_recyclerAdapter : BikeRackRVAdapter
    private lateinit var slide: SlidingDrawer
    private lateinit var mClusterManager : ClusterManager<MarkerItem>
    private lateinit var bikeDatas : ArrayList<RackData>
    var reqEnd = false
    lateinit var mStatusChecker:Runnable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater.inflate(layout.fragment_fng, container, false)

        val mapFragment: SupportMapFragment? = childFragmentManager.findFragmentById(R.id.main_map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)
        items = ArrayList()
        bikeDatas= ArrayList()
        sharedPrep = context!!.getSharedPreferences("DSMAD", MODE_PRIVATE)
        SharedPref.openSharedPrep(context!!)
        lookUpButton = rootView!!.findViewById<View>(R.id.main_look_up) as FloatingActionButton
        bike_title = rootView.findViewById(R.id.bike_title)
        bike_snipt = rootView.findViewById(R.id.bike_side)
        bike_recycler = rootView.findViewById(R.id.bike_recycler)
        bike_recycler.layoutManager = LinearLayoutManager(bike_recycler.context)
        bike_recyclerAdapter = BikeRackRVAdapter(bikeDatas)
        bike_recycler.adapter = bike_recyclerAdapter
        slide = rootView.findViewById(R.id.slide)
        // check Tutorials
        val hida = Client.retrofitService.getPlace()
        hida.enqueue(object : retrofit2.Callback<List<LocationRepo>> {

            override fun onFailure(call: Call<List<LocationRepo>>?, t: Throwable?) {
                Log.v("FngTest", "fail!!")
                Toast.makeText(activity, "서버와의 통신이 원할하지 않습니다!", Toast.LENGTH_LONG).show()
                call!!.cancel()
            }

            override fun onResponse(call: Call<List<LocationRepo>>?, response: Response<List<LocationRepo>>?) {
                val repo = response!!.body()
                when (response!!.code()) {
                    200 -> {
                        Log.e("3443", repo!!.size.toString())
                        var k=0
                        for(it in repo){
                            k++
                            if(k%100==0)Log.v("DBGSIGN",k.toString())
                            items.add(MarkerItem(it.stationGrpSeq, it.stationId, it.stationName, it.rackTotCnt, it.parkingBikeTotCnt,
                                    it.shared, LatLng(it.stationLatitude.toDouble(), it.stationLongitude.toDouble())))
                        }
                        reqEnd = true
                    }
                }
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                //placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        createLocationRequest()
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener(this@FngFragment)
        }
        setUpMap()
        val mHandler = Handler()
        mStatusChecker = Runnable {
            try {
                if(reqEnd){
                    setCluster(mMap)
                    mHandler.removeCallbacks(mStatusChecker)
                }else{
                    mHandler.postDelayed(mStatusChecker, 500)
                }
            } finally {
                Log.v("DBGSIGN","waiting....")
            }
        }
        mStatusChecker.run()
    }

    private fun setCluster(googleMap : GoogleMap) {
        mClusterManager = ClusterManager<MarkerItem>(activity, googleMap)
        googleMap.setOnCameraIdleListener(mClusterManager)
        googleMap.setOnMarkerClickListener(mClusterManager)
        addItems(items)        // 클러스터 Marker 추가
        mClusterManager.setOnClusterItemClickListener { it: MarkerItem? ->

            val center = CameraUpdateFactory.newLatLng(it!!.position)
            mMap.animateCamera(center)
            slide.open()

            bike_title.text = it!!.stationName
            bike_snipt.text = it!!.parkingBikeTotCnt + "개의 카페 중 " + it.rackTotCnt + "운영중"
            Log.e("check retrofit Service", it.stationName + " " + it.stationId + " " + SharedPref.readLoginSession())
            Client.retrofitService.getRacks(it.stationName, it.stationId, SharedPref.readLoginSession().toString()).enqueue(object : retrofit2.Callback<List<RackData>> {
                override fun onFailure(call: Call<List<RackData>>?, t: Throwable?) {
                    Log.e("getRacks", "onFailure")
                    Toast.makeText(activity, "자전거 목록 불러오기 실패!\n서버와의 통신이 원할하지 않습니다!", Toast.LENGTH_LONG).show()
                }
                override fun onResponse(call: Call<List<RackData>>?, response: Response<List<RackData>>?) {
                    Log.e("getRacks", "onResponse  ${response!!.code()} ${response.body()}")
                    when (response!!.code()) {
                        200 -> {
                            bikeDatas.clear()
                            bikeDatas.addAll(response.body()!!)
                            bike_recyclerAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
            false
        }
        mClusterManager.renderer = CustomIconRenderer(activity, googleMap, mClusterManager)
    }

    // 마커 추가
    private fun addItems(it : ArrayList<MarkerItem>) {
        it.forEach {
            mClusterManager.addItem(it)
        }
    }
    class CustomIconRenderer(val context: Context?, map: GoogleMap?, clusterManager: ClusterManager<MarkerItem>?) : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

        val icon: ArrayList<Bitmap> = ArrayList<Bitmap>()
        val dp = context!!.resources.displayMetrics.densityDpi
        init {
            icon.clear()
            arrayOf(drawable.location_filled_1, drawable.location_filled_2, drawable.location_filled_3, drawable.location_filled_4).forEach {
                val ic : Bitmap = BitmapFactory.decodeResource(context!!.resources, it)
                icon.add(Bitmap.createScaledBitmap(ic,180, 180,false))
            }
        }

        override fun onBeforeClusterItemRendered(item : MarkerItem, markerOptions : MarkerOptions) {
            val markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)

            val markerOptions = MarkerOptions()
            markerOptions.title(item.title)
            markerOptions.snippet(item.snippet)
            markerOptions.position(item.position)
            markerOptions.icon(markerIcon)
            super.onBeforeClusterItemRendered(item, markerOptions)
        }

        override fun onClusterItemRendered(item: MarkerItem?, marker: Marker?) {
            super.onClusterItemRendered(item, marker)
            val idx: Int = when(item!!.rackTotCnt.toInt()){
                in 0..2 ->  0
                in 3..5 -> 1
                else -> 2
            }
            marker!!.setIcon(BitmapDescriptorFactory.fromBitmap(icon[idx]))
        }
    }

    override fun onMarkerClick(marker : Marker): Boolean {
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }

    }
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun setUpMap() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    //placeMarkerOnMap(currentLatLng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                }
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        mMap.addMarker(markerOptions)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    private fun addMarker(markerItem : MarkerItem, isSelectedMarker : Boolean) : Marker {
        val position = markerItem.position
        val rack = markerItem.rackTotCnt.toInt()
        val formatted = NumberFormat.getCurrencyInstance().format((rack))
        tv_marker.text = formatted

        var markerIcon : BitmapDescriptor
        if (isSelectedMarker) {
            markerIcon = BitmapDescriptorFactory.fromResource(drawable.marker4)
        } else {
            if (rack <= 2) {
                markerIcon = BitmapDescriptorFactory.fromResource(drawable.marker1)
            }
            else if (rack <= 5) {
                markerIcon = BitmapDescriptorFactory.fromResource(drawable.marker2)
            }
            else {
                markerIcon = BitmapDescriptorFactory.fromResource(drawable.marker3)
            }
        }

        val markerOptions = MarkerOptions()
        markerOptions.title(Integer.toString(rack))
        markerOptions.position(position)
        markerOptions.icon(markerIcon)


        return mMap.addMarker(markerOptions)

    }
    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(activity)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }
}

/*
class MainMapFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var mapView: MapFragment
    private lateinit var gpsManager: GPSManager
    private var googleMap: GoogleMap? = null
    private lateinit var sharedPrep: SharedPreferences

    private lateinit var gpsTrackButton: FloatingActionButton
    private lateinit var lookUpButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.rootView = inflater.inflate(R.layout.fragmn_map_main, container, false)
        var mapFragment: SupportMapFragment?
        mapFragment = fragmentManager?.findFragmentById(R.id.main_map) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReadyListener)
        //this.mapView = SupportMapFragment.
//this.rootView. findViewById<Fragment>(R.id.main_map) as MapFragment
        //this.mapView.getMapAsync(onMapReadyListener)

        this.gpsManager = GPSManager(context!!)
        this.sharedPrep = context!!.getSharedPreferences("DSMAD", MODE_PRIVATE)

        // find button.
        this.gpsTrackButton = this.rootView.findViewById<View>(R.id.main_track_me) as FloatingActionButton
        this.lookUpButton = this.rootView.findViewById<View>(R.id.main_look_up) as FloatingActionButton

        // add event listener
        this.gpsTrackButton.setOnClickListener(onGPSTrackClickListener)

        // check Tutorials
        if(!sharedPrep.getBoolean("isTutorialRun", false)) {
            //showTutorials()
        }
        //mapView.requestFocus()
        return this.rootView
    }

    override fun onStop() {
        super.onStop()
        this.gpsManager.release()
    }
    /**
     * Google map ready callback
     */
    private val onMapReadyListener = OnMapReadyCallback {
        // Map loaded. Start gps
        this.gpsManager.listen(onGPSUpdated)
        this.googleMap = it
        // 기본 위치로 카메라 이동
        it.moveCamera(CameraUpdateFactory.newLatLng(LatLng(37.56, 126.97)))
        it.animateCamera(CameraUpdateFactory.zoomTo(8F))
    }

    private var lastLocation: Location? = null
    private val onGPSUpdated = object: GPSUpdateListener {

        override fun onFirstChange(location: Location) {
            // 첫 위치 업데이트 시 카메라 줌 이동
            lastLocation = location     // 위치 정보 전역으로 승격
            moveLastLocation()
        }

        override fun onChange(location: Location) {
            lastLocation = location     // 마지막 위치를 전역으로 승격

            if(googleMap != null) {
                // TODO 최근 위치 값을 가져왔으므로 나머지 작업을 처리 한다.
                // 서버에 연결해서 근처 자전거 위치를 가져오거나, 뷁
            }
        }
    }

    private val onGPSTrackClickListener = View.OnClickListener {
        if(lastLocation == null) {
            Toast.makeText(context, "위치를 추적하고 있습니다...", Toast.LENGTH_SHORT).show()
        } else {
            moveLastLocation()
        }
    }

    /**
     * 가장 최근 위치로 카메라를 이동합니다.
     */
    private fun moveLastLocation() {
        if(lastLocation != null) {
            val latlng = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latlng))
            googleMap?.animateCamera(CameraUpdateFactory.zoomTo(16F))
        }
    }
    private fun showTutorials() {
        TapTargetView.showFor(activity, // `this` is an Activity
                TapTarget.forView(this.rootView.findViewById(R.id.main_look_up), "자전거 자동 대여", "터치하면 가장 가까운 대여소의 최적의 자전거를 자동으로 선택한 후 대여 합니다.")
                        // All options below are optional
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(1f)            // Specify the alpha amount for the outer circle
                        .titleTextSize(24)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.colorWhite)      // Specify the color of the title text
                        .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                        .textColor(R.color.colorWhite)            // Specify a color for both the title and description text
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .targetRadius(20) // Specify the target radius (in dp)
                        .transparentTarget(true)
                        .drawShadow(true)
                        .targetCircleColor(R.color.colorWhite),
                object : TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    override fun onTargetClick(view: TapTargetView) {
                        super.onTargetClick(view)      // This call is optional

                        // 튜토리얼 완료
                        sharedPrep.edit().putBoolean("isTutorialRun", true).apply()
                    }
                })
    }
}*/