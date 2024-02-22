package com.example.freightlinker_app.profile.driver

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.freightlinker_app.MainActivity
import com.example.freightlinker_app.R
import com.example.freightlinker_app.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Create_profile_driver : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var choice: String
    private var transport_id: Int? = 0
    private lateinit var fio_text: EditText
    private lateinit var number_phone_text: EditText
    private lateinit var button_create: Button
    private lateinit var textView6_error: TextView
    private lateinit var transport_text: TextView
    private lateinit var about_me: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile_driver)

        spinner = findViewById(R.id.spinner)
        fio_text = findViewById(R.id.fio_text)
        number_phone_text = findViewById(R.id.number_phone_text)
        button_create = findViewById(R.id.button_create)
        textView6_error = findViewById(R.id.textView6)
        transport_text = findViewById(R.id.transport_text)
        about_me = findViewById(R.id.about_me)

        val adapter = ArrayAdapter.createFromResource(this, R.array.status, R.layout.custom_spinner_dropdown_item)

        supportActionBar?.hide()

        // Установка адаптера для Spinner
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Получаем текст выбранного элемента
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // Делаем что-то с выбранным текстом, например, выводим его в лог
                choice = selectedItem
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Добавьте данные о транспорте")

        // Создаем пользовательский макет для AlertDialog
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

        // Находим поля ввода в пользовательском макете
        val editText1 = view.findViewById<EditText>(R.id.editText1)
        val editText2 = view.findViewById<EditText>(R.id.editText2)
        val spinnerTransportType = view.findViewById<Spinner>(R.id.spinner_transport_type)
        val editText3 = spinnerTransportType.selectedItem.toString()


        editText1.setHint("бренд транспорта")
        editText2.setHint("Максимально перевозимый вес")

        // Устанавливаем пользовательский макет для AlertDialog
        builder.setView(view)

        // Устанавливаем кнопку "OK" и ее обработчик
        builder.setPositiveButton("OK") { dialog, which ->
            val text1 = editText1.text.toString()
            val text2 = editText2.text.toString()


            // Сохраняем введенные данные в переменные
            val variable1: String = text1
            val variable2: String = text2
            val variable3: String = editText3
            transport_text.text = "Транспорт $text1  $text2  $editText3"
            // Добавьте здесь ваш код для обработки введенных данных

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val transportData = Transport_create(
                        brand = text1,
                        max_weight = text2.toIntOrNull(),
                        transport_type = editText3
                    )
                    val data = api_resource()
                    transport_id = data.createTransport(transportData)
                    if (transport_id != null) {
                        // Обработка успешного создания транспорта
                        Log.d(
                            "CreateTransport",
                            "Transport created successfully. ID: $transport_id"
                        )
                    } else {
                        // Обработка ошибки при создании транспорта
                        Log.e("CreateTransport", "Failed to create transport")
                    }
                } catch (e: Exception) {
                    // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                    Log.e("CreateTransport", "Error creating transport", e)
                    e.printStackTrace()
                }
            }
        }

        // Показываем AlertDialog
        builder.show()



        button_create.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val user_id = sharedPreferences.getString("user_id", "")
            val id = user_id?.toIntOrNull()
            Log.e("555", "$user_id ${transport_id}")
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val profileData = DriversCreate(
                        origin = "",
                        destination = "",
                        fio = fio_text.text.toString(),
                        number_phone = number_phone_text.text.toString(),
                        status = choice,
                        about_me = about_me.text.toString(),
                        transport = transport_id,
                        user_id = id
                    )
                    val data = api_resource()
                    val profileId = data.createDriverProfile(profileData)
                    if (profileId != null) {
                        // Профиль успешно создан, profileId содержит ID созданного профиля
                        Log.d("CreateProfile", "Profile created successfully. Profile ID: $profileId")
                        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("profile_id", profileId.toString())
                        editor.apply()
                        val intent = Intent(this@Create_profile_driver, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // Произошла ошибка при создании профиля
                        Log.e("CreateProfile", "Failed to create profile")
                    }
                } catch (e: Exception) {
                    // Ловим и обрабатываем исключения
                    Log.e("CreateProfile", "Error creating profile", e)
                    e.printStackTrace()
                }
            }

        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}