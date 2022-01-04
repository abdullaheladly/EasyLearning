package com.abdullah996.easylearning.model

import com.google.firebase.firestore.DocumentId

data class QuizModel(
    @DocumentId
    val quizId:String?=null,
    var quizName:String?=null,
    var quizTeacherID:String?=null,
    var Questions:ArrayList<QuestionsModel>?= arrayListOf()
)
