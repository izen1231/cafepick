package com.cafe.dghackathon.shimhg02.dghack


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem

/*
 * Created by sdie3 on 2018-09-30.
 */

class MarkerItem(val stationGrpSeq : Int, val stationId : String, val stationName : String, val rackTotCnt : String,
                 val parkingBikeTotCnt : String, val shared : String, val mPosition : LatLng) : ClusterItem {
    override fun getSnippet(): String {
        return "오픈한 카페 수: $rackTotCnt"
    }
    override fun getTitle(): String {
        return stationName
    }
    override fun getPosition(): LatLng {
        return mPosition
    }
}