package com.richard.pixlog.ui.screen.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.richard.pixlog.data.adapters.EndOfPaginationAdapter
import com.richard.pixlog.data.adapters.LoadingStateAdapter
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.databinding.FragmentHomeBinding
import com.richard.pixlog.ui.screen.map.MapsActivity
import kotlin.text.append

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeAdapter

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

        setupRecyclerView()
        
        val homeViewModel: HomeViewModel by viewModels {
            HomeViewModelFactory.getInstance(requireContext())
        }

        homeViewModel.getStory.observe(viewLifecycleOwner) { pagingData ->
            homeAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }

        setUpToLocation()
        binding.fabAdd.setOnClickListener {
            val toDashboard = HomeFragmentDirections.actionNavigationHomeToNavigationDashboard()
            view.findNavController().navigate(toDashboard)
        }
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter()
        val loadingStateAdapter = LoadingStateAdapter()
        val endOfPaginationAdapter = EndOfPaginationAdapter()
        val concatAdapter = ConcatAdapter(
            homeAdapter.withLoadStateFooter(loadingStateAdapter),
            endOfPaginationAdapter
        )
        binding.rvHome.adapter = concatAdapter

        homeAdapter.addLoadStateListener { loadState ->
            if (loadState.append.endOfPaginationReached && loadState.append is LoadState.NotLoading) {
                endOfPaginationAdapter.showEndMessage(true)
            } else {
                endOfPaginationAdapter.showEndMessage(false)
            }
        }

        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpToLocation(){
        binding.btnLocation.setOnClickListener {
            val toMapsActivity = HomeFragmentDirections.actionNavigationHomeToNavigationMap()
            view?.findNavController()?.navigate(toMapsActivity)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}