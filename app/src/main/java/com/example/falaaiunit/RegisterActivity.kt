package com.example.falaaiunit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterFirstName: EditText
    private lateinit var etRegisterLastName: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var etRegisterConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBackToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterFirstName = findViewById(R.id.etRegisterFirstName)
        etRegisterLastName = findViewById(R.id.etRegisterLastName)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)

        btnRegister.setOnClickListener { register() }
        btnBackToLogin.setOnClickListener { finish() }
    }

    private fun register() {
        val email = etRegisterEmail.text.toString().trim()
        val firstName = etRegisterFirstName.text.toString().trim()
        val lastName = etRegisterLastName.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()
        val confirmPassword = etRegisterConfirmPassword.text.toString().trim()

        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonBody = JSONObject().apply {
            put("email", email)
            put("first_name", firstName)
            put("last_name", lastName)
            put("password", password)
        }

        val url = "http://10.0.2.2:8000/api/accounts/user/"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                val success = response.optInt("id")
                if (success != null) {
                    Toast.makeText(this, "Registro bem-sucedido", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Falha no registro", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val errorMessage = error.message ?: "Erro de conexão"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }

        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}