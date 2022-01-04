package com.abdullah996.easylearning.data

import android.net.Uri
import android.util.Log
import com.abdullah996.easylearning.model.User
import com.abdullah996.easylearning.data.remote.FirestoreApi
import com.abdullah996.easylearning.model.Post
import com.abdullah996.easylearning.model.QuizModel
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class RemoteDataSource @Inject constructor (
private val firestoreApi: FirestoreApi
) {
    suspend fun addUser(user: User){
        firestoreApi.addUser(user)
    }
    suspend fun getUsers():List<User>{
       return firestoreApi.getUsers()
    }
    suspend fun updateUser(userId:String,user: User){
        firestoreApi.updateUserData(userId,user)
    }
    suspend fun uploadQuiz(subject: String,quizModel: QuizModel):String{
       return firestoreApi.uploadQuiz(subject,quizModel)
    }
    suspend fun getQuiz(subject:String,quizId:String):QuizModel{
        return firestoreApi.getQuiz(subject,quizId)
    }
    suspend fun putPost(post: Post){
        firestoreApi.putPost(post)
    }
    suspend fun getTeacherSubject(teacherId:String):String{
       return firestoreApi.getTeacherSubject(teacherId)
    }
    suspend fun putPostImage(uri: Uri,date:String): UploadTask {
        return firestoreApi.putPostImage(uri,date)
    }
    suspend fun getSubjectList(): List<String>{
        return firestoreApi.getSubjectList()
    }
    suspend fun getPostsForSubject(subject: String): MutableList<Post> {
        return firestoreApi.getPostForSubject(subject)
    }

}