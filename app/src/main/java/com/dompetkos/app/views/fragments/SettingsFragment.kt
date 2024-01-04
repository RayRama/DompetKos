package com.dompetkos.app.views.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dompetkos.app.databinding.FragmentSettingsBinding
import com.dompetkos.app.viewmodels.MainViewModel
import com.dompetkos.app.views.activites.AuthActivity


class SettingsFragment : Fragment() {

    var binding: FragmentSettingsBinding? = null

    private lateinit var lastBackup: String

    var viewModel: MainViewModel? = null

    private lateinit var sharedPreferences: SharedPreferences
    private var isSignedIn: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding  = FragmentSettingsBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        sharedPreferences = requireActivity().getSharedPreferences("com.dompetkos.app", AppCompatActivity.MODE_PRIVATE)
        isSignedIn = sharedPreferences.getBoolean("isSignedIn", false)

        binding!!.backupDriveButton.setOnClickListener { c: View? ->
//            viewModel!!.backup(requireActivity().application)
//            viewModel!!.checkIsSignedIn(requireActivity().application)
            if (isSignedIn) {
                viewModel!!.backup(requireActivity().application)
            } else {
                Toast.makeText(requireActivity(), "Please sign in", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "onCreateView: " + isSignedIn.toString())
                val inten = Intent(requireActivity(), AuthActivity::class.java)
                startActivity(inten)
            }
        }

        viewModel!!.getLastBackup()

        viewModel!!.lastBackupDate.observe(viewLifecycleOwner) { lastBackupDate ->
            lastBackup = lastBackupDate
            binding!!.lastBackupDateText.text = lastBackup
        }


        return binding!!.root
    }

}