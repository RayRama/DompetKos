package com.dompetkos.app.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dompetkos.app.databinding.FragmentSettingsBinding
import com.dompetkos.app.viewmodels.MainViewModel


class SettingsFragment : Fragment() {

    var binding: FragmentSettingsBinding? = null

    private lateinit var lastBackup: String

    var viewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding  = FragmentSettingsBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding!!.backupDriveButton.setOnClickListener { c: View? ->
            viewModel!!.backup(requireActivity().application)
//            viewModel!!.checkIsSignedIn(requireActivity().application)
        }

        viewModel!!.getLastBackup()

        viewModel!!.lastBackupDate.observe(viewLifecycleOwner) { lastBackupDate ->
            lastBackup = lastBackupDate
            binding!!.lastBackupDateText.text = lastBackup
        }


        return binding!!.root
    }

}