package com.abdullah996.easylearning.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentTimeLineBinding
import dagger.hilt.android.AndroidEntryPoint
import android.content.DialogInterface

import android.text.Editable
import android.widget.Button

import dagger.hilt.android.qualifiers.ActivityContext

import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.abdullah996.easylearning.databinding.AlertDialogLayoutBinding


@AndroidEntryPoint
class TimeLineFragment : Fragment() {
    private lateinit var binding: FragmentTimeLineBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTimeLineBinding.inflate(layoutInflater,container,false)
        setListeners()
        return binding.root
    }


    @SuppressLint("InflateParams")
    private fun setListeners() {
        binding.apply {

            /**
             * create alert dialog to ask the teacher how many questions
             * will be there in the quiz and how many  possible answers to every questions */
            addQuiz.setOnClickListener {
                val mDialog=LayoutInflater.from(requireContext()).inflate(R.layout.alert_dialog_layout,null)
                val mBilder=AlertDialog.Builder(requireContext())
                    .setView(mDialog)
                    .setTitle("Login Form")
                val  mAlertDialog = mBilder.show()
                val button=mDialog.findViewById<Button>(R.id.done)
                val quizName=mDialog.findViewById<EditText>(R.id.Quiz_name)
                val questions=mDialog.findViewById<EditText>(R.id.questions_number)
                val possibleAnswers=mDialog.findViewById<EditText>(R.id.answers_number)
                button.setOnClickListener {
                    mAlertDialog.dismiss()
                    if (quizName.text.isNotEmpty()&&questions.text.isNotEmpty()&&possibleAnswers.text.isNotEmpty()){
                    val action=TimeLineFragmentDirections.actionTimeLineFragmentToCreateQuizFragment(quizName.text.toString(),questions.text.toString().toInt(),possibleAnswers.text.toString().toInt())
                    findNavController().navigate(action)}
                    else{
                        makeToast("fill all the rquired data firest")
                    }

                }
            }
            addPdf.setOnClickListener {
                    findNavController().navigate(R.id.action_timeLineFragment_to_addPdfFragment)
            }
            addPost.setOnClickListener {
                    findNavController().navigate(R.id.action_timeLineFragment_to_createPostsFragment)
            }
            bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId){
                    R.id.home->{
                        makeToast("go  to home")
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.profile->{
                        makeToast("go to profile")
                         return@setOnNavigationItemSelectedListener  true
                    }
                    R.id.someThingElse->{
                        makeToast("go to others")
                        return@setOnNavigationItemSelectedListener true
                    }

                    else -> return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }
    private fun makeToast(s:String){
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }
}