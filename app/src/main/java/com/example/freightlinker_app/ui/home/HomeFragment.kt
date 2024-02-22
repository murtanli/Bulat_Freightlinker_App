package com.example.freightlinker_app.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.freightlinker_app.MainActivity
import com.example.freightlinker_app.R
import com.example.freightlinker_app.api.api_resource
import com.example.freightlinker_app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var Container: LinearLayout
    private lateinit var brand: String
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Container = binding.Container
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "")
        if (role == "Отправитель") {

            (activity as? MainActivity)?.act_bar()

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val data = api_resource()
                    val result = data.get_all_drivers()
                    if (result.isNotEmpty()) {
                        //вызов функции отрисовки блоков

                        for (driver_info in result) {
                            val brand = if (driver_info.transport?.brand.isNullOrEmpty()) {
                                " "
                            } else {
                                driver_info.transport!!.brand
                            }
                            val res_block = createUserPage(
                                driver_info.origin,
                                driver_info.destination,
                                driver_info.fio,
                                driver_info.number_phone,
                                driver_info.status,
                                brand,
                                driver_info.transport?.max_weight ?: 0, // По умолчанию 0, если значение null
                                driver_info.transport?.transport_type ?: "" // По умолчанию пустая строка, если значение null
                            )
                            Container.addView(res_block)
                        }
                    } else {
                        // Обработка случая, когда список пуст
                        Log.e("BusActivity", "Response failed - result is empty")

                        //val error = createBusEpty()
                        //BusesContainer.addView(error)
                    }
                } catch (e: Exception) {
                    // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                    Log.e("BusActivity", "Error during response", e)
                    e.printStackTrace()
                }
            }
        } else if (role == "Водитель") {
            (activity as? MainActivity)?.act_bar()
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val data = api_resource()
                    val result = data.get_all_cargoes()
                    if (result.isNotEmpty()) {
                        //вызов функции отрисовки блоков

                        for (cargo_info in result) {
                            val res_block = createDriverPage(
                                cargo_info.name_cargo,
                                cargo_info.departure_time,
                                cargo_info.arrival_time,
                                cargo_info.origin,
                                cargo_info.destination,
                                cargo_info.profile_info.fio,
                                cargo_info.profile_info.number_phone
                            )
                            Container.addView(res_block)
                        }
                    } else {
                        // Обработка случая, когда список пуст
                        Log.e("BusActivity", "Response failed - result is empty")

                        //val error = createBusEpty()
                        //BusesContainer.addView(error)
                    }
                } catch (e: Exception) {
                    // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                    Log.e("BusActivity", "Error during response", e)
                    e.printStackTrace()
                }
            }
        }



        return root
    }

    @SuppressLint("ResourceType")
    private fun createDriverPage(name_cargo: String, departure_time: String, arrival_time: String, origin: String, destination: String, fio: String, number_phone: String): LinearLayout {
        //общий блок
        val schedulePage = LinearLayout(requireContext())
        val padding_in_layout = 16

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            950
        )
        layoutParams.setMargins(padding_in_layout, padding_in_layout, padding_in_layout, padding_in_layout)
        layoutParams.bottomMargin = 100
        schedulePage.layoutParams = layoutParams
        schedulePage.orientation = LinearLayout.VERTICAL
        val backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background)
        schedulePage.background = backgroundDrawable

        // верхний блок
        val top_block_inf = LinearLayout(requireContext())
        val top_block_layout_inf = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            270
        )
        top_block_layout_inf.setMargins(padding_in_layout, 60, padding_in_layout, padding_in_layout)

        val background_top__block_inf = ContextCompat.getDrawable(requireContext(), R.drawable.back_time)
        top_block_inf.layoutParams = top_block_layout_inf
        top_block_inf.orientation = LinearLayout.VERTICAL
        top_block_inf.background = background_top__block_inf

        //блок время
        val status_text = TextView(requireContext())
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
        val origin_text = TextView(requireContext())
        if (!origin.isNullOrBlank() && !destination.isNullOrBlank()){
            origin_text.text = "Из ${origin} в ${destination}"
        } else{
            origin_text.text = ""
        }

        origin_text.setTextAppearance(R.style.PageTextBold)
        origin_text.setPadding(30, padding_in_layout, padding_in_layout, padding_in_layout)


        //Блок информация о транспорте
        val cargo_text = TextView(requireContext())
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

        //фио
        val fio_text = TextView(requireContext())
        fio_text.text = "Фио - ${fio}"
        fio_text.setTextAppearance(R.style.PageTitle)

        fio_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        fio_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_fio = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        fio_text.layoutParams = layoutParams_fio

        //номер телефона
        val number_phone_text = TextView(requireContext())
        number_phone_text.text = "Номер телефона - ${number_phone}"
        number_phone_text.setTextAppearance(R.style.PageTextBold)

        number_phone_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        number_phone_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_number_phone = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        number_phone_text.layoutParams = layoutParams_number_phone

        // блок кнопка
        val button_buy_ticket = Button(requireContext())
        button_buy_ticket.text = "Написать"
        button_buy_ticket.setPadding(padding_in_layout, padding_in_layout, padding_in_layout, padding_in_layout)

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
                .start()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=${number_phone}")
            context?.startActivity(intent)

        }

        top_block_inf.addView(origin_text)
        top_block_inf.addView(status_text)

        schedulePage.addView(top_block_inf)
        schedulePage.addView(fio_text)
        schedulePage.addView(cargo_text)
        schedulePage.addView(number_phone_text)
        schedulePage.addView(button_buy_ticket)


        return schedulePage
    }

    @SuppressLint("ResourceType")
    private fun createUserPage(origin: String, destination: String, fio: String, number_phone: String, status:String, brand: String, max_weight: Int, transport_type: String): LinearLayout {
        //общий блок
        val schedulePage = LinearLayout(requireContext())
        val padding_in_layout = 16

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            850
        )
        layoutParams.setMargins(padding_in_layout, padding_in_layout, padding_in_layout, padding_in_layout)
        layoutParams.bottomMargin = 100
        schedulePage.layoutParams = layoutParams
        schedulePage.orientation = LinearLayout.VERTICAL
        val backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background)
        schedulePage.background = backgroundDrawable

        // верхний блок
        val top_block_inf = LinearLayout(requireContext())
        val top_block_layout_inf = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        top_block_layout_inf.setMargins(padding_in_layout, 60, padding_in_layout, padding_in_layout)

        val background_top__block_inf = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_block)
        top_block_inf.layoutParams = top_block_layout_inf
        top_block_inf.orientation = LinearLayout.HORIZONTAL
        top_block_inf.background = background_top__block_inf

        //блок статус
        val status_text = TextView(requireContext())
        status_text.text = status
        status_text.setTextAppearance(R.style.PageTextBold)

        // Задаем фон с закругленными углами
        val backgroundText = ContextCompat.getDrawable(requireContext(), R.drawable.back_status)
        status_text.background = backgroundText

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
        val origin_text = TextView(requireContext())
        if (!origin.isNullOrBlank() && !destination.isNullOrBlank()){
            origin_text.text = "Едет из ${origin} в ${destination}"
        } else{
            origin_text.text = ""
        }

        origin_text.setTextAppearance(R.style.PageTitle)
        origin_text.setPadding(30, padding_in_layout, padding_in_layout, padding_in_layout)

        //блок с картинкой и текстом
        val block_about_truck = LinearLayout(requireContext())
        val block_about_layout_truck = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            150
        )
        block_about_layout_truck.setMargins(40, 60, padding_in_layout, padding_in_layout)

        val background_truck_inf = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_block)
        block_about_truck.layoutParams = block_about_layout_truck
        block_about_truck.orientation = LinearLayout.HORIZONTAL
        block_about_truck.background = background_truck_inf

        //Блок информация о транспорте
        val transport_text = TextView(requireContext())
        transport_text.text = "$transport_type марка - $brand Максимально перевозимый вес - ${max_weight}кг"
        transport_text.setTextAppearance(R.style.TextInf)

        transport_text.setPadding(0, 30, 100, 30)

        // Устанавливаем гравитацию текста по центру
        transport_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_transport = RelativeLayout.LayoutParams(
            900,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        transport_text.layoutParams = layoutParams_transport

        // Создание ImageView для изображения
        val transportImage = ImageView(requireContext())
        // Установка изображения
        transportImage.setImageResource(R.drawable.free_icon_font_truck_side_5074275)
        // Установка размера изображения (в данном случае ширина - 100dp, высота - wrap_content)
        val layoutParamsImage = RelativeLayout.LayoutParams(
            100,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        // Выравнивание изображения по центру
        layoutParamsImage.addRule(RelativeLayout.CENTER_HORIZONTAL)
        transportImage.layoutParams = layoutParamsImage


        //фио
        val fio_text = TextView(requireContext())
        fio_text.text = "Фио - ${fio}"
        fio_text.setTextAppearance(R.style.PageTitle)

        fio_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        fio_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_fio = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        fio_text.layoutParams = layoutParams_fio

        //номер телефона
        val number_phone_text = TextView(requireContext())
        number_phone_text.text = "Номер телефона - ${number_phone}"
        number_phone_text.setTextAppearance(R.style.PageTextBold)

        number_phone_text.setPadding(30, 30, 30, 30)

        // Устанавливаем гравитацию текста по центру
        number_phone_text.gravity = Gravity.CENTER

        // Создаем параметры макета и устанавливаем их для текстового поля
        val layoutParams_number_phone = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        number_phone_text.layoutParams = layoutParams_number_phone

        // блок кнопка
        val button_buy_ticket = Button(requireContext())
        button_buy_ticket.text = "Написать"
        button_buy_ticket.setPadding(padding_in_layout, padding_in_layout, padding_in_layout, padding_in_layout)

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
                .start()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=${number_phone}")
            context?.startActivity(intent)

        }

        top_block_inf.addView(status_text)
        top_block_inf.addView(origin_text)

        block_about_truck.addView(transportImage)
        block_about_truck.addView(transport_text)

        schedulePage.addView(top_block_inf)
        schedulePage.addView(block_about_truck)

        schedulePage.addView(fio_text)
        schedulePage.addView(number_phone_text)
        schedulePage.addView(button_buy_ticket)






        return schedulePage
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}