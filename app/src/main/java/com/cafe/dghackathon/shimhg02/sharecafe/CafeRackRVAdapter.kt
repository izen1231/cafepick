package com.cafe.dghackathon.shimhg02.dghack


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cafe.dghackathon.shimhg02.sharecafe.R
import kotlinx.android.synthetic.main.cafe_rack_item.view.*


class BikeRackRVAdapter(private val mRacks : List<RackData>) : RecyclerView.Adapter<BikeRackRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cafe_rack_item, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mRacks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.RackNumView.text = mRacks[position].rackNum.toString() + "카페"
        holder.RackIdView.text ="카페 고유 번호 : " + mRacks[position].rackId
    }

    inner class ViewHolder(private val mView : View) : RecyclerView.ViewHolder(mView) {
        val RackNumView : TextView = mView.findViewById(R.id.Rack_number) as TextView
        val RackIdView : TextView = mView.findViewById(R.id.Rack_id) as TextView

        override fun toString(): String {
            return super.toString() + " '" + RackNumView.text + "'" + RackIdView.text + "'"
        }
    }

}