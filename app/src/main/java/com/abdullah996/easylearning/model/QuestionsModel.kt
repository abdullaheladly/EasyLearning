package com.abdullah996.easylearning.model

import com.google.firebase.firestore.DocumentId

data class QuestionsModel(
    @DocumentId
    var question_id: String?=null,
    var question:String?=null,
    var option_a:String?=null,
    var option_b:String?=null,
    var option_c :String?=null,
    var option_d :String?=null,
    var answer: String?=null,
    var timer:Long?=null
)
