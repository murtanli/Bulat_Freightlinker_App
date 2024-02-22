package com.example.freightlinker_app.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.freightlinker_app.databinding.FragmentProfileBinding
import com.example.freightlinker_app.profile.driver.Create_profile_driver
import com.example.freightlinker_app.profile.driver.Profile_driver
import com.example.freightlinker_app.profile.user.Profile_user

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences = requireContext().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")
        val role = sharedPreferences.getString("role", "")

        if (profile_id != "0") {
            if (role == "Водитель"){
                val intent = Intent(requireContext(), Profile_driver::class.java)
                intent.putExtra("profile_id", profile_id)
                intent.putExtra("role", role)
                startActivity(intent)
            } else if (role == "Отправитель"){
                val intent = Intent(requireContext(), Profile_user::class.java)
                intent.putExtra("profile_id", profile_id)
                intent.putExtra("role", role)
                startActivity(intent)
            }
        } else {
            if (role == "Водитель"){
                val intent = Intent(requireContext(), Create_profile_driver::class.java)
                intent.putExtra("role", role)
                startActivity(intent)
            } else if (role == "Отправитель"){
                val intent = Intent(requireContext(), Profile_user::class.java)
                intent.putExtra("role", role)
                startActivity(intent)
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}