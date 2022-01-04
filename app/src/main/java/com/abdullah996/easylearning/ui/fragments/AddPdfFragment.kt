package com.abdullah996.easylearning.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentAddPdfBinding
import com.abdullah996.easylearning.model.Post
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddPdfFragment : BottomSheetDialogFragment() {
    private lateinit var preferenceManger: PreferenceManger
    private val mainViewModel:MainViewModel by viewModels()
    private  var _binding:FragmentAddPdfBinding?=null
    private val binding get() = _binding!!
    private lateinit var  pdfUri:Uri
    private var currentUserId:String?=null
    private var teacherSubject:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding= FragmentAddPdfBinding.inflate(layoutInflater,container,false)
        preferenceManger= PreferenceManger(requireContext())

        currentUserId=preferenceManger.getString(Constants.KEY_USER_ID)


        lifecycleScope.launch{
            mainViewModel.getTeacherSubject(currentUserId!!)
            teacherSubject=mainViewModel.teacherSubject
        }
        setListeners()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        binding.pickPdf.setOnClickListener {
            binding.progressBar.visibility=View.VISIBLE
            val  intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="application/pdf"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickPdf.launch(intent)
            binding.done.isEnabled=true
        }
        binding.done.setOnClickListener {
            if (binding.pdfName.text.isEmpty()){

            }
            else{
                it.isEnabled=false
                binding.pickPdf.text="Please wait until we upload your pdf"
                val post= Post()
                post.postDate=getReadableDateTime(Date())
                post.posterId=currentUserId
                post.postSubject=teacherSubject
                post.postText=binding.pdfName.text.toString()
                lifecycleScope.launch {
                    val query = mainViewModel.putPostImage(pdfUri, getReadableDateTime(Date()))
                    val job = lifecycleScope.launch {
                        query.addOnCompleteListener() {
                            it.addOnSuccessListener {
                                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                    pdfUri = it
                                    Toast.makeText(requireContext(), pdfUri.toString(), Toast.LENGTH_SHORT)
                                        .show()
                                    binding.progressBar.visibility=View.INVISIBLE
                                    binding.pickPdf.apply {
                                        isEnabled=true
                                        text="Done"
                                    }
                                    post.postPdf=pdfUri.toString()
                                    lifecycleScope.launch {
                                        mainViewModel.putPost(post)
                                        withContext(Dispatchers.Main) {
                                            findNavController().navigate(R.id.action_addPdfFragment_to_timeLineFragment)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
    @SuppressLint("SetTextI18n")
    private val pickPdf: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {

                pdfUri = result.data!!.data!!
            }
        }
    }
    private fun getReadableDateTime(date: Date):String{
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }



    override fun onDestroyView() {
        _binding=null
        super.onDestroyView()
    }


}