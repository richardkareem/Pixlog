package com.richard.pixlog.ui.screen.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.richard.pixlog.databinding.FragmentNotificationsBinding
import com.richard.pixlog.ui.screen.login.LoginActivity

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // call view model factory
        val notificationsViewModel = ViewModelProvider(this,
            NotificationViewModelFactory.getInstance(requireContext()))[NotificationsViewModel::class.java]

        binding.btnLogout.setOnClickListener {
            notificationsViewModel.logout()
        }

        notificationsViewModel.name.observe(viewLifecycleOwner) {value ->
            if(value != null){
             binding.name.text = value.toString()
            }
        }
        notificationsViewModel.isCanNavigate.observe(viewLifecycleOwner){ value ->
            if(value){
                toLoginScren()
            }
        }
    }

    private fun toLoginScren (){
        //clear entire stack avtivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}