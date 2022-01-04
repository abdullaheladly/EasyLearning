package com.abdullah996.easylearning.ui.fragments


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentCompleteProfileBinding
import com.abdullah996.easylearning.model.User
import com.abdullah996.easylearning.ui.activities.MainActivity
import com.abdullah996.easylearning.ui.activities.StudentMainActivity
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

@AndroidEntryPoint
class CompleteProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding:FragmentCompleteProfileBinding
    private  var encodedImage:String?=null
    private lateinit var preferenceManger: PreferenceManger
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCompleteProfileBinding.inflate(layoutInflater,container,false)
        preferenceManger= PreferenceManger(requireContext())

        if (preferenceManger.getBoolean(Constants.IS_LOGGED_IN)) {
            if (preferenceManger.getString(Constants.KEY_USER_SUBJECT)== null) {
                makeToast("null")
            }else if (preferenceManger.getString(Constants.KEY_USER_SUBJECT)== "Student"){
                //move to student main activity
                    makeToast("student")
               startActivity(Intent(requireActivity(),StudentMainActivity::class.java))
            }
            else {
                makeToast("teacher")

                findNavController().navigate(R.id.action_completeProfileFragment_to_timeLineFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinners()
        setListeners()

    }

    private fun setListeners() {
        binding.layoutImage.setOnClickListener {
            val  intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }

        binding.button2.setOnClickListener {
            if (checkFields()){
                completeUserData()

            }
        }
    }
    private  fun completeUserData(){
        val user= User()
        user.email=preferenceManger.getString(Constants.KEY_USER_EMAIL)
        user.password=preferenceManger.getString(Constants.KEY_USER_PASSWORD)
        user.type=binding.Type.selectedItem.toString()
        user.name=binding.editTextTextPersonName.text.toString()
        user.phone=binding.editTextPhone.text.toString()
        user.image=encodedImage
        if (user.type=="Teacher"){
            user.subject=binding.subject.selectedItem.toString()
        }
        mainViewModel.updateUser(preferenceManger.getString(Constants.KEY_USER_ID)!!,user)
        preferenceManger.putBoolean(Constants.IS_LOGGED_IN,false)
        startActivity(Intent(requireActivity(),MainActivity::class.java))

    }

    private fun checkFields(): Boolean {
        when {
            binding.editTextTextPersonName.text.isEmpty()->{
                makeToast("Please enter your name")
                return false
            }
            binding.editTextPhone.text.isEmpty()->{
                makeToast("please enter your Phone number")
                return false
            }
            encodedImage==null ->{
                makeToast("please upload your image")
                return false
            }
            else ->{
                return true
            }
        }
    }

    private fun makeToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK){
            if (result.data != null){
                val imageUri= result.data!!.data
                try {
                    val inputStream= imageUri?.let { requireActivity().contentResolver.openInputStream(it) }
                    val bitmap= BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility=View.GONE
                    encodedImage=encodedImage(bitmap)
                }catch (e: FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }

    }

    private fun encodedImage(bitmap: Bitmap):String{
        val previewWidth=150
        val previewHeight:Int=bitmap.height*previewWidth/bitmap.width
        val previewBitmap= Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false)
        val byteArrayOutputStream= ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream)
        val bytes=byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun enableSubjectSpinner(){
        if (binding.Type.selectedItem.toString()=="Teacher")
        {
            binding.subjectSpinner.visibility=View.VISIBLE
        }else{
            binding.subjectSpinner.visibility=View.INVISIBLE
        }
    }

    private fun setSpinners() {
        ArrayAdapter.createFromResource(
            requireContext(), R.array.Type,android.R.layout.simple_list_item_1
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_list_item_1)
            binding.Type.adapter=it
        }
        ArrayAdapter.createFromResource(
            requireContext(), R.array.Subject,android.R.layout.simple_list_item_1
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_list_item_1)
            binding.subject.adapter=it
        }

        binding.Type.onItemSelectedListener=this
        binding.subject.onItemSelectedListener=this


    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        enableSubjectSpinner()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}