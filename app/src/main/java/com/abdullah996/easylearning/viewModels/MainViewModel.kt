package com.abdullah996.easylearning.viewModels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abdullah996.easylearning.data.Repository
import com.abdullah996.easylearning.model.Post
import com.abdullah996.easylearning.model.QuizModel
import com.abdullah996.easylearning.model.User
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
application: Application,
private val repository: Repository
):AndroidViewModel(application) {

    var users= mutableListOf<User>()
    var quiz=QuizModel()
    var quizId=""
    var teacherSubject=""
    var postImageUri:Uri?=null

    private var subject= listOf<String>()
    val subjectList:List<String> get() = subject

    private var _subjectPosts= mutableListOf<Post>()
    val subjectPost:List<Post> get() = _subjectPosts

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.remote.addUser(user)
        }
    }

    fun updateUser(userId:String,user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.remote.updateUser(userId,user)
        }
    }

    suspend fun getQuiz(subject:String,quizId:String){
       val job= viewModelScope.launch(Dispatchers.IO) {
            quiz=repository.remote.getQuiz(subject,quizId)
        }
        job.join()
    }

    suspend fun uploadQuiz(subject: String, quizModel: QuizModel){
      val job=  viewModelScope.launch(Dispatchers.IO){
           quizId=repository.remote.uploadQuiz(subject,quizModel)
        }
        job.join()
    }

    suspend fun getUsers(){
       val job= viewModelScope.launch(Dispatchers.IO) {
          users= repository.remote.getUsers().toMutableList()
        }
         job.join()

    }

    suspend fun putPost(post: Post){
        viewModelScope.launch(Dispatchers.IO) {
            repository.remote.putPost(post)
        }

    }

    suspend fun getTeacherSubject(teacherID:String){
       val job= viewModelScope.launch {
            teacherSubject= repository.remote.getTeacherSubject(teacherID)
        }
        job.join()
    }
    suspend fun putPostImage(uri: Uri,date:String): UploadTask {
        return repository.remote.putPostImage(uri, date)
    }

    suspend fun getSubjectList(){
       val job=viewModelScope.launch {
           subject = repository.remote.getSubjectList()
       }

       job.join()
    }

    suspend fun getPostsForSubject(subject: String){
        val job=viewModelScope.launch {
            _subjectPosts=repository.remote.getPostsForSubject(subject)
        }
        job.join()
    }




}