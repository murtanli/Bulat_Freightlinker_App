package com.example.freightlinker_app.Auth

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
import com.example.freightlinker_app.R
import com.example.freightlinker_app.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class sign_in : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var choice: String
    private lateinit var login: EditText
    private lateinit var password1: EditText
    private lateinit var password2: EditText
    private lateinit var error_text: TextView
    private lateinit var ButtonSign_in: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        spinner = findViewById(R.id.spinner)
        login = findViewById(R.id.editTextLogin)
        password1 = findViewById(R.id.editTextPassword)
        password2 = findViewById(R.id.editTextPassword2)
        ButtonSign_in = findViewById(R.id.buttonLogin)
        error_text = findViewById(R.id.textView6)

        supportActionBar?.hide()


        // Создание ArrayAdapter с использованием кастомного макета для элемента выпадающего списка
        val adapter = ArrayAdapter.createFromResource(this, R.array.choice, R.layout.custom_spinner_dropdown_item)

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

        ButtonSign_in.setOnClickListener {
            if (!login.text.isNullOrEmpty() && !password1.text.isNullOrEmpty() && !password2.text.isNullOrEmpty()) {

                val loginText = login?.text?.toString()
                val passwordText = password1?.text?.toString()
                GlobalScope.launch(Dispatchers.Main) {
                    try {

                        val data = api_resource()
                        val result = data.Sign_in(
                            loginText.toString(),
                            passwordText.toString(),
                            choice.toString())

                        if (result != null) {
                            val intent = Intent(this@sign_in, Login::class.java)
                            startActivity(intent)
                            error_text.text = result.message

                            val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_id", result.user_id.toString())
                            Log.e("445", result.user_id.toString())
                            editor.putString("login", loginText)
                            editor.putString("login", "false")


                            editor.apply()

                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            error_text.text = "Ошибка в процессе авторизации ${result.message}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        error_text.text = "Ошибка входа: Неправильный пароль или профиль уже существует"
                    }
                }
            } else {
                error_text.text = "Пустые поля ! либо пороли не совпадают"
            }
        }


    }
}