package com.cafe.dghackathon.shimhg02.dghack


import com.google.gson.annotations.SerializedName

class RackData {
    @SerializedName("rackId")
    var rackId = ""
    @SerializedName("rackNum")
    var rackNum = 0
    @SerializedName("linked")
    var linked = false
}