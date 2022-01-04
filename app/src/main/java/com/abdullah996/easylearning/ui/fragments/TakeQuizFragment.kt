package com.abdullah996.easylearning.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.FragmentTakeQuizBinding
import com.abdullah996.easylearning.model.QuestionsModel
import com.abdullah996.easylearning.model.QuizModel
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.security.auth.Subject

@AndroidEntryPoint
class TakeQuizFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentTakeQuizBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val args by navArgs<TakeQuizFragmentArgs>()
    private lateinit var quizId:String
    private lateinit var subject:String
    private lateinit var quiz:QuizModel
    private  var totalQuestionsNumber:Int=0


    private var allQuestionsList:MutableList<QuestionsModel>?= ArrayList<QuestionsModel>()
    private var questionsToAnswer:MutableList<QuestionsModel>?= ArrayList<QuestionsModel>()
    private var canAnswer=false
    private var currentQuestion:Int=0
    private lateinit var countDownTimer : CountDownTimer
    private var correctAnswers=0
    private var wrongAnswers=0
    private var notAnswered=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentTakeQuizBinding.inflate(layoutInflater,container,false)
         quizId=args.quizId
         subject=args.subject
       lifecycleScope.launch {
           getQuiz()
           totalQuestionsNumber= quiz.Questions!!.size
           allQuestionsList=quiz.Questions
           pickQuestions()
           loadUi()
           setListeners()
       }
        return binding.root
    }

    private fun setListeners() {
        binding.quizOptionOne.setOnClickListener(this)
        binding.quizOptionTwo.setOnClickListener(this)
        binding.quizOptionThree.setOnClickListener(this)
        binding.quizOptionFour.setOnClickListener(this)
        binding.quizNextBtn.setOnClickListener(this)
    }
    private fun makeToast(s:String){
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    private suspend fun getQuiz() {
      val job=  lifecycleScope.launch(Dispatchers.Main) {
           mainViewModel.getQuiz(subject,quizId)
            quiz=mainViewModel.quiz

        }
        job.join()
    }
    private fun pickQuestions() {
        for (i in 0 until totalQuestionsNumber) {
            val randomNumber = getRandomInt(0, allQuestionsList!!.size)
            questionsToAnswer?.add(allQuestionsList!![randomNumber])
            allQuestionsList!!.removeAt(randomNumber)
            Log.d("QUESTIONS LOG", "Question " + i + " : " + questionsToAnswer!![i.toInt()].question)
        }

    }
    private fun getRandomInt(min: Int, max: Int): Int {
        return (Math.random() * (max - min)).toInt() + min
    }
    private fun loadUi() {
        binding.quizTitle.text=quiz.quizName
        binding.quizQuestion.text="load First question"


        //Enable options Buttons
        enableOptions()

        //Load First Question
        loadQuestions(1)
    }
    private fun enableOptions() {
        //Show options buttons
        binding.quizOptionOne.visibility=View.VISIBLE
        binding.quizOptionTwo.visibility=View.VISIBLE
        binding.quizOptionThree.visibility=View.VISIBLE
        binding.quizOptionFour.visibility=View.VISIBLE
        // enable options button
        binding.quizOptionOne.isEnabled=true
        binding.quizOptionTwo.isEnabled=true
        binding.quizOptionThree.isEnabled=true
        binding.quizOptionFour.isEnabled=true
        //hide feedback and next button
        binding.quizQuestionFeedback.visibility=View.INVISIBLE
        binding.quizNextBtn.visibility=View.INVISIBLE
        binding.quizNextBtn.isEnabled=false
    }
    private fun loadQuestions(questionNumber: Int) {

        //set questions number
        binding.quizQuestionNumber.text=questionNumber.toString()

        //load Question text
        binding.quizQuestion.text= questionsToAnswer?.get(questionNumber-1)!!.question


        //load options
        binding.quizOptionOne.text= questionsToAnswer?.get(questionNumber-1)!!.option_a
        binding.quizOptionTwo.text= questionsToAnswer?.get(questionNumber-1)!!.option_b
        binding.quizOptionThree.text= questionsToAnswer?.get(questionNumber-1)!!.option_c
        binding.quizOptionFour.text= questionsToAnswer?.get(questionNumber-1)!!.option_d

        //question loaded set can answer to true
        canAnswer=true
        currentQuestion=questionNumber

        // start questions timer
        startTimer(questionNumber)

    }
    private fun startTimer(questionNumber: Int) {
        //set timer text
        val timeToAnswer= questionsToAnswer?.get(questionNumber-1)?.timer
        binding.quizQuestionTime.text=timeToAnswer.toString()
        binding.quizQuestionProgress.visibility=View.VISIBLE

        //Start CountDown
        countDownTimer = object : CountDownTimer(timeToAnswer!! * 1000, 10) {
            override fun onTick(millisUntilFinished: Long) {
                //Update Time
                binding.quizQuestionTime.text=((millisUntilFinished/ 1000).toString() + "")

                //Progress in percent
                val percent = millisUntilFinished / (timeToAnswer!! * 10)
                binding.quizQuestionProgress.progress=(percent.toInt())
            }

            override fun onFinish() {
                //Time Up, Cannot Answer Question Anymore
                canAnswer = false
                binding.quizQuestionFeedback.text="انتهى الوقت بدون اجابه"
                binding.quizQuestionFeedback.setTextColor(resources.getColor(R.color.red,null))
                notAnswered++
                showNextBtn()
            }
        }

        countDownTimer.start()

    }

    private fun showNextBtn() {
        if (currentQuestion==totalQuestionsNumber.toInt()){
            binding.quizNextBtn.text="اظهر النتيجه"
        }
        binding.quizQuestionFeedback.visibility=View.VISIBLE
        binding.quizNextBtn.visibility=View.VISIBLE
        binding.quizNextBtn.isEnabled=true

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.quiz_option_one -> answerSelected(binding.quizOptionOne)
            R.id.quiz_option_two -> answerSelected(binding.quizOptionTwo)
            R.id.quiz_option_three -> answerSelected(binding.quizOptionThree)
            R.id.quiz_option_four -> answerSelected(binding.quizOptionFour)
            R.id.quiz_next_btn ->{
                if (currentQuestion==totalQuestionsNumber.toInt()){
                    binding.quizNextBtn.text="اظهر النتيجه"
                }else{
                    currentQuestion++
                    loadQuestions(currentQuestion)
                    resetOptions()
                }
            }
        }
    }
    private fun answerSelected(selectedAnswer: Button) {
        selectedAnswer.setTextColor(resources.getColor(R.color.black))
        //Check Answer
        if (canAnswer) {
            if (questionsToAnswer!![currentQuestion-1].answer?.equals(selectedAnswer.text) == true) {
                //Correct Answer
                correctAnswers++
                selectedAnswer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
                binding.quizQuestionFeedback.text="اجابه صحيحه"
                binding.quizQuestionFeedback.setTextColor(resources.getColor( R.color.black))
            } else {
                //Wrong Answer
                wrongAnswers++
                selectedAnswer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                binding.quizQuestionFeedback.text= "اجابه خاطئه\n" +   "  الاختيار الصحيح هو  " + questionsToAnswer!!.get(currentQuestion-1).answer
                binding.quizQuestionFeedback.setTextColor(resources.getColor( R.color.red))


            }
            //Set Can answer to false
            canAnswer = false

            // stop the timer
            countDownTimer.cancel()


            // show next button
            showNextBtn()
        }
    }
    private fun resetOptions() {

        binding.quizOptionOne.background=(resources.getDrawable(R.color.purple_500,null))
        binding.quizOptionTwo.background=(resources.getDrawable(R.color.purple_500,null))
        binding.quizOptionThree.background=(resources.getDrawable(R.color.purple_500,null))
        binding.quizOptionFour.background=(resources.getDrawable(R.color.purple_500,null))

        binding.quizOptionOne.setTextColor(resources.getColor(R.color.white, null))
        binding.quizOptionTwo.setTextColor(resources.getColor(R.color.white, null))
        binding.quizOptionThree.setTextColor(resources.getColor(R.color.white, null))
        binding.quizOptionFour.setTextColor(resources.getColor(R.color.white, null))

        binding.quizQuestionFeedback.visibility=View.INVISIBLE
        binding.quizNextBtn.visibility=View.INVISIBLE
        binding.quizNextBtn.isEnabled=false
    }

}