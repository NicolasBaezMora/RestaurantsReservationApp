package com.example.restaurantreservation.fragments

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantreservation.R
import com.example.restaurantreservation.databinding.FragmentUpdateRestaurantBinding
import com.example.restaurantreservation.rest.responseModels.CityResponse
import com.example.restaurantreservation.rest.responseModels.RestaurantResponse
import com.example.restaurantreservation.viewmodels.AddRestaurantViewModel
import com.example.restaurantreservation.viewmodels.UpdateRestaurantViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.File

class UpdateRestaurantFragment : Fragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentUpdateRestaurantBinding
    private lateinit var dataItemRestaurant: RestaurantResponse
    private var imageUri: Uri? = null

    private lateinit var cities: List<CityResponse>
    private lateinit var idCity: String

    private val navController by lazy { findNavController() }

    private val vm by lazy { ViewModelProvider(this).get(UpdateRestaurantViewModel::class.java) }
    private val codePermissionMediaStore by lazy { 102 }
    private val codeActivityResultMediaStore by lazy { 103 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateRestaurantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataItemRestaurant = arguments?.getSerializable("dataRestaurant") as RestaurantResponse

        setupLayout()
    }

    private fun setupLayout() {

        Picasso.get().load(dataItemRestaurant.image).into(binding.imageViewRestaurant)
        binding.textFieldName.editText?.setText(dataItemRestaurant.name)
        binding.textFieldDescription.editText?.setText(dataItemRestaurant.description)
        binding.textFieldAddress.editText?.setText(dataItemRestaurant.address)

        vm.getCities().observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                val dataResponse = it.body()
                if (dataResponse?.ok!!) {
                    val adapterSpinner = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        dataResponse.body.map { cityObject -> cityObject.name }
                    )
                    binding.spinnerCity.adapter = adapterSpinner
                    binding.spinnerCity.onItemSelectedListener = this
                    cities = dataResponse.body
                }
            }
        })

        vm.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.layoutView.visibility = View.GONE
                binding.layoutLoad.visibility = View.VISIBLE
            }
            else binding.layoutLoad.visibility = View.GONE
        })

        binding.btnChangeImage.setOnClickListener(this)
        binding.btnUpdate.setOnClickListener(this)
    }

    private fun checkMediaStorePermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(
                    requireContext(),
                    "Concede los permisos directamente en la configuración de la app",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    codePermissionMediaStore
                )
            }
        }else{
            openMediaStore()
        }
    }

    private fun openMediaStore() {
        val intentMediaStore = Intent(Intent.ACTION_PICK)
        intentMediaStore.type = "image/*"
        try {
            startActivityForResult(intentMediaStore, codeActivityResultMediaStore)
        }catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            binding.btnChangeImage.id -> {
                checkMediaStorePermission()
            }
            binding.btnUpdate.id -> {
                addRestaurant()
            }
        }
    }

    private fun addRestaurant() {
        val nameRestaurant = binding.textFieldName.editText?.text.toString().trim()
        val descriptionRestaurant = binding.textFieldDescription.editText?.text.toString().trim()
        val addressRestaurant = binding.textFieldAddress.editText?.text.toString().trim()
        if (checkFields(nameRestaurant, descriptionRestaurant, addressRestaurant)) {
            vm.updateRestaurant(
                dataItemRestaurant.id,
                nameRestaurant,
                descriptionRestaurant,
                addressRestaurant,
                if (imageUri != null )File(getRealPathFromURI(requireContext(), imageUri!!)) else null,
                idCity
            ).observe(viewLifecycleOwner, Observer {
                if (it.isSuccessful) {
                    val dataResponse = it.body()
                    if (dataResponse?.ok!!) {
                        Toast.makeText(requireContext(), "Se actualizo el restaurante", Toast.LENGTH_SHORT).show()
                        navController.navigate(R.id.action_updateRestaurantFragment_to_restaurantsFragment)
                    } else {
                        Toast.makeText(requireContext(), "Hubo un error en la petición", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No fue posible realizar la operación", Toast.LENGTH_SHORT).show()
                }
            })
        } else Toast.makeText(requireContext(), "Los campos estan incompletos", Toast.LENGTH_SHORT).show()
    }

    private fun checkFields(nameRestaurant: String, descriptionRestaurant: String, addressRestaurant: String): Boolean {
        return !(nameRestaurant.isEmpty() || descriptionRestaurant.isEmpty() || addressRestaurant.isEmpty() || idCity.isEmpty())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            codePermissionMediaStore -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) openMediaStore()
                else Snackbar.make(requireView(), "Al rechazar los permisos deberas confirmarlos manualmente", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            codeActivityResultMediaStore -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = data?.data!!
                    binding.imageViewRestaurant.setImageURI(imageUri)
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        idCity = cities[p2].id
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        when {
            // DocumentProvider
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    // ExternalStorageProvider
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        // This is for checking Main Memory
                        return if ("primary".equals(type, ignoreCase = true)) {
                            if (split.size > 1) {
                                Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                            } else {
                                Environment.getExternalStorageDirectory().toString() + "/"
                            }
                            // This is for checking SD Card
                        } else {
                            "storage" + "/" + docId.replace(":", "/")
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val fileName = getFilePath(context, uri)
                        if (fileName != null) {
                            return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                        }
                        var id = DocumentsContract.getDocumentId(uri)
                        if (id.startsWith("raw:")) {
                            id = id.replaceFirst("raw:".toRegex(), "")
                            val file = File(id)
                            if (file.exists()) return id
                        }
                        val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                        return getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> {
                                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            "video" -> {
                                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            }
                            "audio" -> {
                                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }
        }
        return null
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,
                null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


    fun getFilePath(context: Context, uri: Uri?): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        try {
            if (uri == null) return null
            cursor = context.contentResolver.query(uri, projection, null, null,
                null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


}