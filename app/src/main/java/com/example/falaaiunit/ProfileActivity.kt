package com.example.falaaiunit

import NavigationUtils
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etEmail = findViewById(R.id.etEmail)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        loadUserProfile()

        btnSave.setOnClickListener { updateProfile() }
        btnBack.setOnClickListener { NavigationUtils.backToMenu(this) }
    }

    private fun loadUserProfile() {
        val userId = NavigationUtils.getUserId(this)
        val url = "http://10.0.2.2:8000/api/accounts/user/$userId"

        val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                etEmail.setText(response.getString("email"))
                etFirstName.setText(response.getString("first_name"))
                etLastName.setText(response.getString("last_name"))
            },
            { error ->
                val errorMessage = error.message ?: "Erro de conexão"
                Toast.makeText(this, "Erro ao carregar perfil: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = NavigationUtils.getToken(this@ProfileActivity)
                if (token != null){
                    headers["Authorization"] = "Token $token"
                }
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun updateProfile() {
        val email = etEmail.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val userId = NavigationUtils.getUserId(this)

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()){
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonBody = JSONObject().apply {
            put("email", email)
            put("first_name", firstName)
            put("last_name", lastName)
        }

        val url = "http://10.0.2.2:8000/api/accounts/user/$userId/"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.PUT, url, jsonBody,
            { response ->
                val success = response.optInt("id")
                if (success != 0) {
                    Toast.makeText(this, "Salvo", Toast.LENGTH_SHORT).show()
                    NavigationUtils.backToMenu(this)
                } else {
                    Toast.makeText(this, "Falha ao salvar", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val errorMessage = error.message ?: "Erro de conexão"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = NavigationUtils.getToken(this@ProfileActivity)
                if (token != null){
                    headers["Authorization"] = "Token $token"
                }
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}