package com.abdullah996.easylearning.ui.fragments

import android.content.Intent
import android.graphics.BitmapFactory
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
import com.abdullah996.easylearning.databinding.FragmentCreatePostsBinding
import com.abdullah996.easylearning.model.Post
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.Subject


@AndroidEntryPoint
class CreatePostsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCreatePostsBinding
    private lateinit var preferenceManger: PreferenceManger
    private lateinit var userId:String
    private lateinit var teacherSubject: String
    private lateinit var imageUri: Uri
    private var withImage:Boolean=false
    private val mainViewModel: MainViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCreatePostsBinding.inflate(layoutInflater,container,false)
        preferenceManger= PreferenceManger(requireContext())
        userId= preferenceManger.getString(Constants.KEY_USER_ID)!!
        lifecycleScope.launch(Dispatchers.Main) {
            getSubject()
            setListeners()
        }

        return binding.root
    }
    private suspend fun getSubject(){
        mainViewModel.getTeacherSubject(userId)
        teacherSubject=mainViewModel.teacherSubject
    }

    private  fun setListeners() {
        binding.post.setOnClickListener {
            val post=Post()
            post.postText=binding.edtPost.text.toString()
            post.posterId=userId
            post.postSubject=teacherSubject
            post.postDate=getReadableDateTime(Date())
            binding.prgressBar.visibility=View.VISIBLE
            binding.prgressBar.animate()
            lifecycleScope.launch(Dispatchers.IO) {
                /**
                 * if the post have image*/
                if (withImage){
                    val query=mainViewModel.putPostImage(imageUri,getReadableDateTime(Date()))
                    val job=lifecycleScope.launch {  query.addOnCompleteListener() {
                        it.addOnSuccessListener {
                            it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                                imageUri=it
                                post.postImage= imageUri.toString()
                                lifecycleScope.launch {
                                mainViewModel.putPost(post)
                                withContext(Dispatchers.Main) {
                                    findNavController().navigate(R.id.action_createPostsFragment_to_timeLineFragment)
                                }
                                }
                            }
                        }
                    }
                    }
                    job.join()
                }
                /**
                 * if the post only contains text*/
                else{
                mainViewModel.putPost(post)
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_createPostsFragment_to_timeLineFragment)
                }
                }
            }

        }
        binding.addImage.setOnClickListener {
            val params=binding.edtPost.layoutParams
            params.height=ViewGroup.LayoutParams.WRAP_CONTENT
            binding.edtPost.layoutParams=params
            binding.image.visibility=View.VISIBLE
            val  intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)

        }
    }
    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            if (result.data != null){
                withImage=true
                imageUri= result.data!!.data!!
                lifecycleScope.launch {
                }
                binding.image.setImageURI(imageUri)
            }
        }
    }
    private fun getReadableDateTime(date: Date):String{
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }


}