package com.richard.pixlog.ui.screen.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.richard.pixlog.R
import com.richard.pixlog.data.repository.Result
import com.richard.pixlog.databinding.FragmentDashboardBinding
import com.richard.pixlog.utils.getImageUri
import com.richard.pixlog.utils.reduceFileImage
import com.richard.pixlog.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private var currentImageUrl : Uri? = null

    private var lat : Double? = null
    private var lon : Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requiredPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if(isGranted){
                Toast.makeText(requireContext(), "Permission Request Granted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(), "Permission Request Denied", Toast.LENGTH_LONG).show()
            }
        }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dashboardViewModelFactory = DashboardViewModelFactory.getInstance(requireContext())
        val dashboardViewModel = ViewModelProvider(this, dashboardViewModelFactory)[DashboardViewModel::class.java]

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Setup toolbar back click
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if(!allPermissionGranted()){
            requiredPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        binding.ctGallery.setOnClickListener {startGallery()}
        binding.ctCamera.setOnClickListener {startCamera()}

        binding.btnReset.setOnClickListener {
            resetImg()
        }

        binding.btnPost.setOnClickListener {
            val description = binding.edDescription.text.toString()
            if(currentImageUrl != null && description != ""){
                currentImageUrl.let {
                    if(it != null){
                        val imageFile = uriToFile(it, requireContext()).reduceFileImage()
                        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                        val descRequestBody = description.toRequestBody("text/plain".toMediaType())
                        val multipartyBody = MultipartBody.Part.createFormData(
                            name = "photo",
                            filename = imageFile.name,
                            requestFile
                        )
                        dashboardViewModel.uploadStory(multipartyBody, descRequestBody, lat, lon)
                    }

                }
            }else{
                Toast.makeText(requireContext(), "Please Insert Image and Description", Toast.LENGTH_SHORT).show()
            }
        }

        dashboardViewModel.result.observe(viewLifecycleOwner){result ->
            if(result != null){
                when(result){
                    is Result.Loading ->{
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), result.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Log.d("DashboardFragment", "Upload success, preparing to navigate")
                        Toast.makeText(requireContext(), result.data.message, Toast.LENGTH_SHORT).show()
                        // Navigate to Home and clear navigation stack

                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.navigation_home, true).build()
                        val action = DashboardFragmentDirections.actionNavigationDashboardToNavigationHome()
                        findNavController().navigate(action, navOptions)

                    }
                }
            }
        }

                    //checkbox - Enable/disable location tracking
        binding.checkBox.setOnClickListener {
            if(binding.checkBox.isChecked){
                // Request location permission and get current location
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    getCurrentLocation()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                // Clear location data when checkbox is unchecked
                lat = null
                lon = null
            }
        }

    }


    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri : Uri? ->
        if(uri != null){
            showImage(uri)
            binding.btnReset.visibility = View.VISIBLE
        }else{
            Toast.makeText(requireContext(), "No Media Selected", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCamera(){
        currentImageUrl = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUrl!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSuccess ->
        if(isSuccess){
            showImage(currentImageUrl!!)
            binding.btnReset.visibility = View.VISIBLE
        }else{
            currentImageUrl = null
        }
    }

    private fun showImage(uri: Uri){
        currentImageUrl = uri
        binding.ivGallery.setImageURI(uri)
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION,
        ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun showLoading(isLoading : Boolean){
        if(isLoading){
            binding.progressLoading.visibility = View.VISIBLE

        }else{
            binding.progressLoading.visibility = View.INVISIBLE
        }
    }

    private fun resetImg(){
        binding.ivGallery.setImageResource(R.drawable.ic_gallery)
        binding.btnReset.visibility = View.GONE
        currentImageUrl = null
        binding.edDescription.text?.clear()
        
        // Reset location if checkbox is unchecked
        if (!binding.checkBox.isChecked) {
            lat = null
            lon = null
        }
    }



    // permission location
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // If checkbox is checked, get current location coordinates
                if (binding.checkBox.isChecked) {
                    getCurrentLocation()
                }
            }
        }

    // Get current location coordinates
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        lat = it.latitude
                        lon = it.longitude
                        
                        // Show location info to user
                        val locationText = "Location: can get location"
                        Toast.makeText(requireContext(), locationText, Toast.LENGTH_LONG).show()
                        
                        // Log for debugging
                        Log.d("DashboardFragment", "Location obtained: lat=$lat, lon=$lon")
                    } ?: run {
                        Toast.makeText(requireContext(), "Unable to get location. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Log.e("DashboardFragment", "Error getting location: ${e.message}")
                    Toast.makeText(requireContext(), "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DashboardFragment", "Exception getting location: ${e.message}")
                Toast.makeText(requireContext(), "Exception getting location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }


}