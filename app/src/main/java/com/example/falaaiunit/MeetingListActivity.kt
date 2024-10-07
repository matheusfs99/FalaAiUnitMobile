package com.example.falaaiunit

import Meeting
import MeetingAdapter
import NavigationUtils
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class MeetingListActivity : AppCompatActivity() {

    private lateinit var rvMeetingList: RecyclerView
    private lateinit var meetingAdapter: MeetingAdapter
    private val meetingList = mutableListOf<Meeting>()
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings_list)

        rvMeetingList = findViewById(R.id.rvMeetingList)
        rvMeetingList.layoutManager = LinearLayoutManager(this)

        meetingAdapter = MeetingAdapter(meetingList)
        rvMeetingList.adapter = meetingAdapter

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { NavigationUtils.backToMenu(this) }

        fetchMeetings()
    }

    private fun fetchMeetings() {
        val url = "http://10.0.2.2:8000/api/meetings/meeting/"

        val jsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                parseMeetings(response)
            },
            { error ->
                Toast.makeText(this, "Erro ao buscar reuniões: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = NavigationUtils.getToken(this@MeetingListActivity)
                if (token != null){
                    headers["Authorization"] = "Token $token"
                }
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun parseMeetings(response: JSONArray) {
        meetingList.clear()
        for (i in 0 until response.length()) {
            val meetingJson = response.getJSONObject(i)
            val owner = meetingJson.getJSONObject("owner")
            val owner_name = owner.getString("get_full_name")
            val guest = meetingJson.getJSONObject("guest")
            val guest_name = guest.getString("get_full_name")
            val description = meetingJson.getString("description")
            val startTime = meetingJson.getString("start_time")
            val endTime = meetingJson.getString("end_time")

            val meeting = Meeting("$owner_name - $guest_name", description, startTime, endTime)
            meetingList.add(meeting)
        }

        meetingAdapter.notifyDataSetChanged()
    }
}
