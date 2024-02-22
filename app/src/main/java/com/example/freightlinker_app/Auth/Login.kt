package com.example.freightlinker_app.Auth


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.freightlinker_app.MainActivity
import com.example.freightlinker_app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.freightlinker_app.api.api_resource

class Login : AppCompatActivity() {

    private lateinit var but_sing: TextView
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var errorText : TextView
    private lateinit var buttonLogin: Button
    private lateinit var spinner: Spinner
    private lateinit var choice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        but_sing = findViewById(R.id.textView2)
        spinner = findViewById(R.id.spinner)
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextLogin = findViewById(R.id.editTextLogin)
        errorText = findViewById(R.id.textView3)


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

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val login_save = sharedPreferences.getString("login", "")


        if (login_save == "true"){
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
        }


        buttonLogin.setOnClickListener {
            val loginText = editTextLogin?.text?.toString()
            val passwordText = editTextPassword?.text?.toString()


            if (loginText.isNullOrBlank() || passwordText.isNullOrBlank()) {
                errorText.text = "Введите данные в поля"
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        // Вызываем функцию logIn для выполнения запроса
                        val data = api_resource()
                        val result = data.log_in(loginText, passwordText, choice)

                        if (result != null) {
                            if (result.message.toString() != "Неправильный логин, пароль или роль") {
                                // Если успешно авторизованы, выводим сообщение об успешной авторизации и обрабатываем данные
                                Log.d("LoginActivity", "Login successful")
                                //Log.d("LoginActivity", "User ID: ${result.user_data.user_id}")
                                errorText.text = result.message

                                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                try {
                                    editor.putString("profile_id", result.profile_id.toString())
                                    editor.putString("user_id", result.user_id.toString())
                                    Log.e("555", "${result.user_id}  ${result.profile_id} ${result.message}")
                                    editor.putString("role", choice)
                                    editor.putString("login", "true")
                                    editor.apply()
                                } catch(e: Exception) {
                                    editor.putString("profile_id", result.profile_id.toString())
                                    editor.putString("user_id", result.user_id.toString())
                                    editor.putString("role", choice)
                                    editor.putString("login", "true")
                                    editor.apply()
                                }

                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                //ErrorText.setTextColor(R.color.blue)

                            } else {
                                // Если произошла ошибка, выводим сообщение об ошибке
                                Log.e("LoginActivity", "Login failed")
                                errorText.text = result.message
                                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("login", "false")
                                editor.apply()
                            }
                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            errorText.text = "Ошибка в процессе авторизации ${result.message}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        errorText.text = "Ошибка входа: Неправильный пароль или профиль не найден"
                        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("login", "false")
                        editor.apply()
                    }
                }
            }
        }



        but_sing.setOnClickListener {
            val intent = Intent(this, sign_in::class.java)
            startActivity(intent)
        }
        supportActionBar?.hide()
    }

    override fun onBackPressed() {

    }
}