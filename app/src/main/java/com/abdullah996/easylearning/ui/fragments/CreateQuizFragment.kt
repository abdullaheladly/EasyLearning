package com.abdullah996.easylearning.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentCreateQuizBinding
import com.abdullah996.easylearning.model.QuestionsModel
import com.abdullah996.easylearning.model.QuizModel
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.PreferenceManger
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class CreateQuizFragment : Fragment(),AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentCreateQuizBinding
    private lateinit var quizSubject:String
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var preferenceManger: PreferenceManger
    private  var numberOfQuestion:Int=0
    private  var numberOfAnswers:Int=0
    private lateinit var quizName:String
    private var questionNumber=1
    private val args by navArgs<CreateQuizFragmentArgs>()
    private  var quiz:ArrayList<QuestionsModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        preferenceManger= PreferenceManger(requireContext())
        binding= FragmentCreateQuizBinding.inflate(layoutInflater,container,false)
        numberOfQuestion=args.questionsNumber

        quizName=args.name
        quizSubject=args.possibleAnswers.toString()
        setSpinners()
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        binding.nextQuestion.setOnClickListener {
            setQuestionData()
        }
    }

    private fun setSpinners() {
        binding.QuizName.text=quizName
        binding.questionsNumber.text=questionNumber.toString()
        ArrayAdapter.createFromResource(
            requireContext(), R.array.Timer,android.R.layout.simple_list_item_1
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_list_item_1)
            binding.timerSpinner.adapter=it
        }


        binding.timerSpinner.onItemSelectedListener=this

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
    private fun setQuestionData(){
        val textQuestion= binding.edtQuestion.text.toString()
       val textOption1= binding.edtWrongAnswer1.text.toString()
       val textOption2= binding.edtWrongAnswer2.text.toString()
       val textOption3= binding.edtWrongAnswer3.text.toString()
        val textOption4=binding.edtWrongAnswer4.text.toString()
       val correctAnswer= binding.edtCorrectAnswer.text.toString()
        val timer=binding.timerSpinner.selectedItem.toString().toLong()
            if (checkNotNull()){
                setSingleQuestion(textQuestion,textOption1,textOption2,textOption3,correctAnswer,timer,textOption4)
            }
            else{
                makeToast("Please complete all the data ")
            }
    }

    private fun uploadQuiz() {
        makeToast("finish")
        val quizModel=QuizModel()
        val userId=preferenceManger.getString(Constants.KEY_USER_ID)
        quizModel.quizName=quizName
        quizModel.Questions=quiz
        quizModel.quizTeacherID=userId
        lifecycleScope.launch {
           mainViewModel.uploadQuiz(quizSubject,quizModel)
            val quizId=mainViewModel.quizId
            withContext(Dispatchers.Main){
                val action=CreateQuizFragmentDirections.actionCreateQuizFragmentToTakeQuizFragment(quizId,quizSubject)
                findNavController().navigate(action)
            }

        }



    }

    private fun checkNotNull(): Boolean {
        if (binding.edtQuestion.text.isEmpty()){
            return false
        }
        else if (binding.edtCorrectAnswer.text.isEmpty()){
            return false
        }
        else if (binding.edtWrongAnswer1.text.isEmpty()){
            return false
        }
        else if (binding.edtWrongAnswer2.text.isEmpty()){
            return false
        }
        else if (binding.edtWrongAnswer3.text.isEmpty()){
            return false
        }
        else{
            return true
        }
    }

    private fun setSingleQuestion(textQuestion: String, textOption1: String, textOption2: String, textOption3: String, correctAnswer: String, timer: Long,textOption4: String) {
        val questionsModel=QuestionsModel()
        questionsModel.question=textQuestion
        questionsModel.answer=correctAnswer
        questionsModel.option_a=textOption4
        questionsModel.option_b=textOption1
        questionsModel.option_c=textOption2
        questionsModel.option_d=textOption3
        questionsModel.timer=timer
        quiz.add(questionsModel)
        if(quiz.size<numberOfQuestion){
        clearEditText()}
        else{
            uploadQuiz()
        }
    }

    private fun clearEditText() {
        questionNumber+=1
        binding.apply {
            edtQuestion.text.clear()
            edtCorrectAnswer.text.clear()
            edtWrongAnswer1.text.clear()
            edtWrongAnswer2.text.clear()
            edtWrongAnswer3.text.clear()
            questionsNumber.text=questionNumber.toString()
        }

        if (quiz.size+1==numberOfQuestion){
            binding.nextQuestion.text="Finish"
        }
    }

    private fun makeToast(string: String){
       Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
   }


}