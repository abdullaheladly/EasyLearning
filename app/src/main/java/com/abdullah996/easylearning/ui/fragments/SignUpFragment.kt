package com.abdullah996.easylearning.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentSignUpBinding
import com.abdullah996.easylearning.model.User
import com.abdullah996.easylearning.ui.activities.HomeActivity
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow


@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var prefrenceManger: PreferenceManger
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var users:List<User>
    private  var emails= arrayListOf<String>()
    private val mainViewModel:MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSignUpBinding.inflate(layoutInflater,container,false)
        prefrenceManger= PreferenceManger(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setListeners()
        }

    }

    private  suspend fun setListeners() {
        binding.button.setOnClickListener {
            lifecycleScope.launch {
                val job = lifecycleScope.launch(Dispatchers.Main) {
                    mainViewModel.getUsers()
                    users = mainViewModel.users

                    users.forEach {
                        emails.add(it.email!!)
                    }

                }
                job.join()
                if (checkFields()) {
                    val user = User()
                    user.email = binding.editTextTextEmailAddress.text.toString()
                    user.password = binding.editTextTextPassword.text.toString()
                    mainViewModel.addUser(user)

                    lifecycleScope.launch(Dispatchers.Main) {
                        mainViewModel.getUsers()
                        users = mainViewModel.users

                        users.forEach {
                            emails.add(it.email!!)
                        }

                        prefrenceManger.putString(Constants.KEY_USER_EMAIL, user.email!!)
                        prefrenceManger.putString(Constants.KEY_USER_PASSWORD, user.password!!)
                        prefrenceManger.putBoolean(Constants.IS_LOGGED_IN, false)
                        val u = users.find {
                            it.email == user.email
                        }
                        if (u != null) {
                            prefrenceManger.putString(Constants.KEY_USER_ID, u.id!!)
                        }
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                    }

                }
            }
        }
    }

    private fun checkFields():Boolean {
        when {
            binding.editTextTextEmailAddress.text.isEmpty() -> {
                makeToast("please Enter your Email ")
                return false
            }
            binding.editTextTextPassword.text.isEmpty() -> {
                makeToast("please Enter your password ")
                return false
            }
            binding.editTextTextPassword2.text.isEmpty() -> {
                makeToast("please Enter your password  again ")
                return false
            }
            binding.editTextTextPassword.text.toString()!=binding.editTextTextPassword2.text.toString() -> {
                makeToast("password should be the same ")
                return false
            }
            emails.contains(binding.editTextTextEmailAddress.text.toString())->{
               makeToast("email already exist")
               return false
           }
            else -> {
                return true
            }
        }
    }

    private fun makeToast(text:String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


}