package com.example.freightlinker_app.profile.driver

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.freightlinker_app.R
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.freightlinker_app.Auth.Login
import com.example.freightlinker_app.MainActivity
import com.example.freightlinker_app.api.api_resource
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.freightlinker_app.api.*

class Profile_driver : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var choice: String
    private lateinit var button_exit: Button
    private lateinit var fio_text: TextView
    private lateinit var number_phone_text: TextView
    private lateinit var about_me: TextView
    private lateinit var origin_text: TextInputEditText
    private lateinit var destination_text: TextInputEditText
    private lateinit var button_save: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_driver)

        supportActionBar?.hide()

        fio_text = findViewById(R.id.fio_text)
        origin_text = findViewById(R.id.origin_text)
        button_exit = findViewById(R.id.button_exit)
        number_phone_text = findViewById(R.id.number_phone_text)
        about_me = findViewById(R.id.about_me)
        destination_text = findViewById(R.id.destination_text)
        button_save = findViewById(R.id.button_save)

        spinner = findViewById(R.id.spinner)

        val adapter = ArrayAdapter.createFromResource(this, R.array.status, R.layout.custom_spinner_dropdown_item)

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

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")
        val id = profile_id?.toIntOrNull()

        GlobalScope.launch(Dispatchers.Main) {
            try {

                val data = api_resource()
                val result = data.get_profile(id)

                if (result != null) {
                    fio_text.text = "Фио - ${result.profile_inf.fio}"
                    number_phone_text.text = " Номер телефона - ${result.profile_inf.number_phone}"
                    about_me.text = "О себе - ${result.profile_inf.about_me}"
                    val index = adapter.getPosition(result.profile_inf.status)
                    spinner.setSelection(index)
                    origin_text.setText(result.profile_inf.origin)
                    destination_text.setText(result.profile_inf.destination)

                } else {
                    // Обработка случая, когда result равен null
                    Log.e("LoginActivity", "Login failed - result is null")
                }
            } catch (e: Exception) {
                // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                Log.e("LoginActivity", "Error during login", e)
                e.printStackTrace()
            }
        }

        button_save.setOnClickListener {
            val data = api_resource()
            val profileData = ProfileData(profile_id = profile_id, origin = origin_text.text.toString(), destination = destination_text.text.toString(), status = spinner.selectedItem.toString())

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    data.put_profile(profileData)
                    Log.d("ProfileUpdate", "Profile updated successfully")
                } catch (e: Exception) {
                    Log.e("ProfileUpdate", "Error updating profile", e)
                }
            }
        }

        button_exit.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.remove("profile_id")
            editor.remove("role")
            editor.putString("login", "false")
            editor.apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }



        /*
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите данные")

        // Создаем пользовательский макет для AlertDialog
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)

        // Находим поля ввода в пользовательском макете
        val editText1 = view.findViewById<EditText>(R.id.editText1)
        val editText2 = view.findViewById<EditText>(R.id.editText2)
        val editText3 = view.findViewById<EditText>(R.id.editText3)

        // Устанавливаем пользовательский макет для AlertDialog
        builder.setView(view)

        // Устанавливаем кнопку "OK" и ее обработчик
        builder.setPositiveButton("OK") { dialog, which ->
            val text1 = editText1.text.toString()
            val text2 = editText2.text.toString()
            val text3 = editText3.text.toString()

            // Сохраняем введенные данные в переменные
            val variable1: String = text1
            val variable2: String = text2
            val variable3: String = text3

            // Добавьте здесь ваш код для обработки введенных данных
        }

        // Устанавливаем кнопку "Отмена" и ее обработчик
        builder.setNegativeButton("Отмена") { dialog, which ->
            dialog.dismiss()
        }

        // Показываем AlertDialog
        builder.show()*/
    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}