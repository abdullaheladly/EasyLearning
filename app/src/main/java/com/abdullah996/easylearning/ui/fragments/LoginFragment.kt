package com.abdullah996.easylearning.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentLoginBinding
import com.abdullah996.easylearning.model.User
import com.abdullah996.easylearning.ui.activities.HomeActivity
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var users:List<User>
    private  var emails= arrayListOf<String>()
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var preferenceManger: PreferenceManger

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         binding=FragmentLoginBinding.inflate(layoutInflater,container,false)
        preferenceManger= PreferenceManger(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
    }

    private fun setListeners() {
       binding.button.setOnClickListener {
           lifecycleScope.launch {
               val job = lifecycleScope.launch(Dispatchers.Main) {
                   mainViewModel.getUsers()
                   users = mainViewModel.users


               }
               job.join()
               lifecycleScope.launch{
                   users.forEach {
                       emails.add(it.email!!)
                   }
                   login()
               }
           }
       }
    }

    private fun login() {
        val user=User()
        val vUser=User()
        user.email=binding.editTextTextEmailAddress.text.toString()
        user.password=binding.editTextTextPassword.text.toString()
        if (emails.contains(user.email)){
            val u=users.find {
                it.email==user.email
            }
            if (u!!.password==user.password){
               startActivity(Intent(requireActivity(),HomeActivity::class.java))
                preferenceManger.putString(Constants.KEY_USER_ID,u.id!!)
                preferenceManger.putString(Constants.KEY_USER_SUBJECT,u.subject?:"Student")
                preferenceManger.putBoolean(Constants.IS_LOGGED_IN,true)

            }
            else{
                makeToast("password error")
            }
        }
        else{
            makeToast("try to make sure that you didn't write any space before email")
        }
    }

    private fun makeToast(text:String){
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


}