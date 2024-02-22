package com.example.freightlinker_app.profile.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.freightlinker_app.Auth.Login
import com.example.freightlinker_app.MainActivity
import com.example.freightlinker_app.R
import com.example.freightlinker_app.api.api_resource
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Profile_user : AppCompatActivity() {

    private lateinit var container_lb: LinearLayout
    private lateinit var fio_text_user: TextView
    private lateinit var number_phone_text_user: TextView
    private lateinit var button_exit_user: Button
    private lateinit var add_cargo: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user)

        supportActionBar?.hide()

        container_lb = findViewById(R.id.Container_user)
        fio_text_user = findViewById(R.id.fio_text_user)
        number_phone_text_user = findViewById(R.id.number_phone_text_user)
        button_exit_user = findViewById(R.id.button_exit_user)
        add_cargo = findViewById(R.id.add_cargo)

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")
        val id = profile_id?.toIntOrNull()
        Log.e("666", profile_id.toString())
        if (profile_id == "0"){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Добавьте данные профиля")

            // Создаем пользовательский макет для AlertDialog
            val view = layoutInflater.inflate(R.layout.custom_dialog_create_profile, null)

            // Находим поля ввода в пользовательском макете
            val editText1 = view.findViewById<EditText>(R.id.editText1_pr)
            val editText4 = view.findViewById<EditText>(R.id.editText5_pr)



            // Устанавливаем пользовательский макет для AlertDialog
            builder.setView(view)

            // Устанавливаем кнопку "OK" и ее обработчик
            builder.setPositiveButton("OK") { dialog, which ->
                val text1 = editText1.text.toString()
                val text4 = editText4.text.toString()
                Log.e("#424", "$text1  $text4")
                // Сохраняем введенные данные в переменные
                val variable1: String = text1
                val variable4: String = text4

                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                val profile_id = sharedPreferences.getString("profile_id", "")
                val user_id = sharedPreferences.getString("user_id", "")
                val id = profile_id?.toIntOrNull()
                val us_id = user_id?.toIntOrNull()
                Log.e("666", "$text1 $text4 ")
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val data = api_resource()
                        val result = data.create_user_profile(variable1, variable4, us_id)

                        if (result.profile_id != null) {
                            Log.e("666", result.profile_id.toString())
                            val editor = sharedPreferences.edit()
                            editor.putString("profile_id", result.profile_id.toString())
                            editor.apply()
                            val intent = Intent(this@Profile_user, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.e("ProfileActivity", "Response failed - result is empty")
                            // Действия при пустом результате
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileActivity", "Error during response", e)
                        e.printStackTrace()
                        // Обработка ошибок
                    }
                }
            }
            builder.show()
        }

        button_exit_user.setOnClickListener {
            val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.remove("profile_id")
            editor.remove("role")
            editor.putString("login", "false")
            editor.apply()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


        GlobalScope.launch(Dispatchers.Main) {
            try {
                val data = api_resource()
                val result = data.get_user(id)
                fio_text_user.text = "Фио - ${result.profile_inf.fio}"
                number_phone_text_user.text = "Номер телефона - ${result.profile_inf.number_phone}"

                if (result.cargo_inf.isNotEmpty()) {
                    for (cargo_info in result.cargo_inf) {
                        val res_block = createCargoBlock(
                            cargo_info.name_cargo,
                            cargo_info.departure_time,
                            cargo_info.arrival_time,
                            cargo_info.origin,
                            cargo_info.destination,
                            cargo_info.pk,
                        )
                        container_lb.addView(res_block)
                    }
                } else {
                    Log.e("ProfileActivity", "Response failed - result is empty")
                    // Действия при пустом результате
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error during response", e)
                e.printStackTrace()
                // Обработка ошибок
            }
        }


        add_cargo.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Добавьте данные о транспорте")

            // Создаем пользовательский макет для AlertDialog
            val view = layoutInflater.inflate(R.layout.custom_alert_dialog_user, null)

            // Находим поля ввода в пользовательском макете
            val editText1 = view.findViewById<EditText>(R.id.editText1)
            val editText4 = view.findViewById<EditText>(R.id.editText4)
            val editText5 = view.findViewById<EditText>(R.id.editText5)

            val but_arrive = view.findViewById<MaterialButton>(R.id.but_data_arrive)
            val but_departure = view.findViewById<MaterialButton>(R.id.but_data_destination)


            but_departure.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this@Profile_user)

                datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                    // Здесь можно обработать выбранную пользователем дату
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Устанавливаем выбранную дату в поле ввода для даты
                    but_departure.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                }

                // Показываем DatePickerDialog
                datePickerDialog.show()
            }

            but_arrive.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this@Profile_user)

                datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
                    // Здесь можно обработать выбранную пользователем дату
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Устанавливаем выбранную дату в поле ввода для даты
                    but_arrive.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

                }

                // Показываем DatePickerDialog
                datePickerDialog.show()
            }

            // Устанавливаем пользовательский макет для AlertDialog
            builder.setView(view)

            // Устанавливаем кнопку "OK" и ее обработчик
            builder.setPositiveButton("OK") { dialog, which ->
                val text1 = editText1.text.toString()
                val text4 = editText4.text.toString()
                val text5 = editText5.text.toString()
                val data_arriv = but_arrive.text
                var data_dep = but_arrive.text

                // Сохраняем введенные данные в переменные
                val variable1: String = text1
                val variable2: String = data_arriv.toString()
                val variable3: String = data_dep.toString()
                val variable4: String = text4
                val variable5: String = text5

                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                val profile_id = sharedPreferences.getString("profile_id", "")
                val id = profile_id?.toIntOrNull()
                Log.e("666", "$text1  $data_arriv $data_dep $text4 $text5 id -  $id")
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val data = api_resource()
                        val result = data.create_cargo(id, variable1, variable2, variable3, variable4, variable5)

                        if (result.message != null && result.message.isNotEmpty()) {
                            Log.e("666", result.message)
                        } else {
                            Log.e("ProfileActivity", "Response failed - result is empty")
                            // Действия при пустом результате
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileActivity", "Error during response", e)
                        e.printStackTrace()
                        // Обработка ошибок
                    }
                }
            }
            builder.show()
        }
    }

    fun createCargoBlock(name_cargo: String, departure_time: String, arrival_time: String, origin: String, destination: String, pk: Int): LinearLayout {
        //общий блок
        val schedulePage = LinearLayout(this)
        val padding_in_layout = 16

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            750
        )
        layoutParams.setMargins(
            padding_in_layout,
            padding_in_layout,
            padding_in_layout,
            padding_in_layout
        )
        layoutParams.bottomMargin = 100
        schedulePage.layoutParams = layoutParams
        schedulePage.orientation = LinearLayout.VERTICAL
        val backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.rounded_background)
        schedulePage.background = backgroundDrawable

        // верхний блок
        val top_block_inf = LinearLayout(this)
        val top_block_layout_inf = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            270
        )
        top_block_layout_inf.setMargins(padding_in_layout, 60, padding_in_layout, padding_in_layout)

        val background_top__block_inf = ContextCompat.getDrawable(this, R.drawable.back_time)
        top_block_inf.layoutParams = top_block_layout_inf
        top_block_inf.orientation = LinearLayout.VERTICAL
        top_block_inf.background = background_top__block_inf

        //блок время
        val status_text = TextView(this)
        status_text.text = "${departure_time} - ${arrival_time}"
        status_text.setTextAppearance(R.style.PageTextBold)


        // Устанавливаем отступы слева, справа, сверху и снизу (в пикселях)

        status_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        status_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams2 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        status_text.layoutParams = layoutParams2

        //откуда и куда
        val origin_text = TextView(this)
        if (!origin.isNullOrBlank() && !destination.isNullOrBlank()) {
            origin_text.text = "Из ${origin} в ${destination}"
        } else {
            origin_text.text = ""
        }

        origin_text.setTextAppearance(R.style.PageTextBold)
        origin_text.setPadding(30, padding_in_layout, padding_in_layout, padding_in_layout)


        //Блок информация о транспорте
        val cargo_text = TextView(this)
        cargo_text.text = "Груз - ${name_cargo}"
        cargo_text.setTextAppearance(R.style.PageTitle)

        cargo_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        cargo_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_cargo = RelativeLayout.LayoutParams(
            600,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        cargo_text.layoutParams = layoutParams_cargo


        // блок кнопка
        val button_buy_ticket = Button(this)
        button_buy_ticket.text = "Удалить"
        button_buy_ticket.setPadding(
            padding_in_layout,
            padding_in_layout,
            padding_in_layout,
            padding_in_layout
        )

        button_buy_ticket.setOnClickListener {
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(300)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .start()
                }
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val data = api_resource()
                    val result = data.del_cargo(pk)

                    if (result.message.isNotEmpty()) {
                        Log.e("666", result.message)
                    } else {
                        Log.e("ProfileActivity", "Response failed - result is empty")
                        // Действия при пустом результате
                    }
                } catch (e: Exception) {
                    Log.e("ProfileActivity", "Error during response", e)
                    e.printStackTrace()
                    // Обработка ошибок
                }
            }
            val intent = Intent(this@Profile_user, MainActivity::class.java)
            startActivity(intent)
        }

        top_block_inf.addView(origin_text)
        top_block_inf.addView(status_text)

        schedulePage.addView(top_block_inf)
        schedulePage.addView(cargo_text)
        schedulePage.addView(button_buy_ticket)

        return schedulePage

    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
