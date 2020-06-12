package com.cafe.dghackathon.shimhg02.dghack


import com.google.gson.annotations.SerializedName


class LocationRepo {
    @SerializedName("stationGrpSeq")
    var stationGrpSeq = 0

    @SerializedName("stationId")
    var stationId = ""

    @SerializedName("stationName")
    var stationName = ""

    @SerializedName("rackTotCnt")
    var rackTotCnt = ""

    @SerializedName("parkingBikeTotCnt")
    var parkingBikeTotCnt = ""

    @SerializedName("shared")
    var shared = ""

    @SerializedName("stationLatitude")
    var stationLatitude = ""

    @SerializedName("stationLongitude")
    var stationLongitude= ""
}