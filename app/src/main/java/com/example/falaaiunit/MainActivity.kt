package com.example.falaaiunit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogout: Button
    private lateinit var btnProfile: Button
    private lateinit var btnSearchUsers: Button
    private lateinit var btnMeetings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogout = findViewById(R.id.btnLogout)
        btnProfile = findViewById(R.id.btnProfile)
        btnSearchUsers = findViewById(R.id.btnSearchUsers)
        btnMeetings = findViewById(R.id.btnMeetings)

        btnLogout.setOnClickListener {
            logoutUser()
        }
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        btnSearchUsers.setOnClickListener {
            val intent = Intent(this, ScheduleMeetingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        btnMeetings.setOnClickListener {
            val intent = Intent(this, MeetingListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun logoutUser() {
        val url = "http://10.0.2.2:8000/api/accounts/user/logout/"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, null,
            { response ->
                val success = response.optBoolean("logout", false)
                if (success) {
                    Toast.makeText(this, "Logout bem-sucedido", Toast.LENGTH_SHORT).show()
                    val sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("auth_token")
                    editor.apply()

                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Falha no logout", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode
                val errorMessage = if (statusCode != null) {
                    "Erro de conexão: Código de status $statusCode"
                } else {
                    error.message ?: "Erro de conexão"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = NavigationUtils.getToken(this@MainActivity)
                if (token != null) {
                    headers["Authorization"] = "Token $token"
                }
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}