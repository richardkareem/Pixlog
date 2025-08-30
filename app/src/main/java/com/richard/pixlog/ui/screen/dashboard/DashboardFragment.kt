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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
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
                        dashboardViewModel.uploadStory(multipartyBody, descRequestBody)
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
    }



    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }


}