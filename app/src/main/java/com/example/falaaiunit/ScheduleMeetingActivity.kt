package com.example.falaaiunit

import NavigationUtils
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.*

class ScheduleMeetingActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnBack: Button
    private lateinit var tvUsuarioInfo: TextView
    private lateinit var etDescription: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private var startTime: Calendar = Calendar.getInstance()
    private var endTime: Calendar = Calendar.getInstance()
    private lateinit var btnMarcarReuniao: Button
    private lateinit var tvMensagemErro: TextView


    private var guestUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)

        etEmail = findViewById(R.id.etEmail)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnBack = findViewById(R.id.btnBack)
        tvUsuarioInfo = findViewById(R.id.tvUsuarioInfo)
        etDescription = findViewById(R.id.etDescription)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnMarcarReuniao = findViewById(R.id.btnMarcarReuniao)
        tvMensagemErro = findViewById(R.id.tvMensagemErro)

        btnBuscar.setOnClickListener { buscarUsuario() }
        btnBack.setOnClickListener { NavigationUtils.backToMenu(this) }
        btnMarcarReuniao.setOnClickListener { marcarReuniao() }
        etStartTime.setOnClickListener {
            showDateTimePicker(startTime, etStartTime)
        }
        etEndTime.setOnClickListener {
            showDateTimePicker(endTime, etEndTime)
        }
    }

    private fun showDateTimePicker(calendar: Calendar, editText: EditText) {
        // Primeiro, exibir o DatePickerDialog
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Atualizar o calendário com a data escolhida
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Após escolher a data, exibir o TimePickerDialog
                showTimePicker(calendar, editText)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun showTimePicker(calendar: Calendar, editText: EditText) {
        val timePicker = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                // Atualizar o calendário com o horário escolhido
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Exibir a data e hora selecionadas no formato desejado
                updateDateTimeField(calendar, editText)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // formato 24 horas
        )

        timePicker.show()
    }

    private fun updateDateTimeField(calendar: Calendar, editText: EditText) {
        // Formatar a data no formato desejado: DD/MM/YYYY HH:mm:ss
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        editText.setText(format.format(calendar.time))
    }

    private fun buscarUsuario() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Informe o email", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://10.0.2.2:8000/api/accounts/user/search_by_email/?email=$email"

        val request = object : JsonObjectRequest(Method.GET, url, null,
            { response ->
                val userId = response.optInt("id", -1)
                if (userId != -1) {
                    guestUserId = userId
                }
                val firstName = response.getString("first_name")
                val lastName = response.getString("last_name")

                tvUsuarioInfo.text = "Usuário: $firstName $lastName"
                tvUsuarioInfo.visibility = View.VISIBLE
                etDescription.visibility = View.VISIBLE
                etStartTime.visibility = View.VISIBLE
                etEndTime.visibility = View.VISIBLE
                btnMarcarReuniao.visibility = View.VISIBLE
                tvMensagemErro.visibility = View.GONE
            },
            { error ->
                tvUsuarioInfo.visibility = View.GONE
                btnMarcarReuniao.visibility = View.GONE
                tvMensagemErro.visibility = View.VISIBLE
            }
        ){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = NavigationUtils.getToken(this@ScheduleMeetingActivity)
                if (token != null){
                    headers["Authorization"] = "Token $token"
                }
                return headers
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun marcarReuniao() {
        guestUserId?.let {
            val url = "http://10.0.2.2:8000/api/meetings/meeting/"

            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val description = etDescription.text.toString().trim()

            val jsonBody = JSONObject().apply {
                put("guest", it)
                put("description", description)
                put("start_time", startTime)
                put("end_time", endTime)
            }

            val request = object : JsonObjectRequest(
                Method.POST, url, jsonBody,
                { response ->
                    Toast.makeText(this, "Reunião marcada com sucesso!", Toast.LENGTH_SHORT).show()
                    NavigationUtils.backToMenu(this)
                },
                { error ->
                    Toast.makeText(this, "Erro ao marcar reunião", Toast.LENGTH_SHORT).show()
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    val token = NavigationUtils.getToken(this@ScheduleMeetingActivity)
                    if (token != null){
                        headers["Authorization"] = "Token $token"
                    }
                    return headers
                }
            }

            Volley.newRequestQueue(this).add(request)
        } ?: run {
            Toast.makeText(this, "Nenhum usuário selecionado", Toast.LENGTH_SHORT).show()
        }
    }
}
