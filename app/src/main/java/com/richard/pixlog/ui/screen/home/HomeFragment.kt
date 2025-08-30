package com.richard.pixlog.ui.screen.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.paging.PagingData
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.data.repository.Result
import com.richard.pixlog.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel: HomeViewModel by viewModels {
            HomeViewModelFactory.getInstance(requireContext())
        }

        homeViewModel.getStory.observe(viewLifecycleOwner) { pagingData ->
            setDataRecyclerView(pagingData)
        }

        binding.fabAdd.setOnClickListener {
            val toDashboard = HomeFragmentDirections.actionNavigationHomeToNavigationDashboard()
            view.findNavController().navigate(toDashboard)
        }
    }

    private fun setDataRecyclerView(pagingData: PagingData<ListStory>) {
        val homeAdapter = HomeAdapter()
        binding.rvHome.adapter = homeAdapter
        binding.rvHome.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        homeAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressLoading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}