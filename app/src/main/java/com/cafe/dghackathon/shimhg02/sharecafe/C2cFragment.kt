package com.cafe.dghackathon.shimhg02.dghack

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cafe.dghackathon.shimhg02.sharecafe.Client2
import com.cafe.dghackathon.shimhg02.sharecafe.R
import kotlinx.android.synthetic.main.fragment_c2c.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class C2cFragment : Fragment() {

    private val items = java.util.ArrayList<Data>()
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerAdapter? = null
    public var cafeiii =0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_c2c, container, false)
        
        view.fab.setOnClickListener {
                items += Data("cafe", "산에뜰애", "운영중", "직접 로스팅한 원두가 매력적인 카페")
                items += Data("cafe", "원두 굽는집", "운영중", "조용한 분위기가 녹아들은 데이트카페")
                items += Data("cafe", "The Edcan", "준비중", "매일매일 다른원두를 쓰는 집")
                items += Data("cafe", "플리쳐 카페 하우스", "준비중", "전직 사진작가인 사장님이 찍은 아름다운 사진을 감상하며 즐길 수 있는 카페")
                items += Data("cafe", "리니어 카페", "운영중", "코스타리카산 원두를 사용하는 맛의 카페")
                recyclerView!!.adapter?.notifyDataSetChanged()

        }
        recyclerView = view!!.findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        recyclerView!!.adapter = com.cafe.dghackathon.shimhg02.dghack.RecyclerAdapter(items)
        adapter = recyclerView!!.adapter as RecyclerAdapter?
        Client.retrofitService.getQuestionList().enqueue(object : Callback<ArrayList<QuestionRepo>> {
            override fun onResponse(call: Call<ArrayList<QuestionRepo>>?, response: Response<ArrayList<QuestionRepo>>?) {
                val repo = response!!.body()
                when (response.code()) {
                    200 -> {
                        repo!!.indices.forEach {
                            items += Data(repo[it].id!!, repo[it].writer!!, repo[it].date!!, repo[it].content!!)
                            recyclerView!!.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<QuestionRepo>>?, t: Throwable?) {
                Log.v("C2cTest", "fail!!")
            }
        })
        return view
    }
}