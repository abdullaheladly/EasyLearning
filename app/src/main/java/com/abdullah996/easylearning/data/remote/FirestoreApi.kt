package com.abdullah996.easylearning.data.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abdullah996.easylearning.model.Post
import com.abdullah996.easylearning.model.QuizModel
import com.abdullah996.easylearning.utill.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.math.log
import com.abdullah996.easylearning.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.security.auth.Subject

class FirestoreApi @Inject constructor(
    private val firestore:FirebaseFirestore,
    private val storage: FirebaseStorage
) {


    suspend fun addUser(user: User){
        firestore.collection(Constants.KEY_COLLECTIONS_USERS).add(user).await()
    }
    suspend fun getUsers():List<User>{
        var users= listOf<User>()
        val query=firestore.collection(Constants.KEY_COLLECTIONS_USERS).get().await() //{
        if (!query.isEmpty){
          users=query.toObjects(User::class.java)
            Log.d("abdullah50", "getUsers: ${users.size}")
        }
        return users
    }
    suspend fun updateUserData(userid:String,user: User){
        firestore.collection(Constants.KEY_COLLECTIONS_USERS).document(userid).set(user).await()
    }
    suspend fun uploadQuiz(subject: String,quizModel: QuizModel):String{
      val job=  firestore.collection(Constants.KEY_COLLECTIONS_QUIZZES).document(subject).collection(Constants.KEY_COLLECTIONS_MyQUIZZES).add(quizModel).await()
        return job.id
    }
    suspend fun getQuiz(subject:String,quizId:String):QuizModel{
        var quizModel=QuizModel()
        val query=firestore.collection(Constants.KEY_COLLECTIONS_QUIZZES).document(subject).collection(Constants.KEY_COLLECTIONS_MyQUIZZES).document(quizId).get().await()
            if (query.exists()){
                quizModel= query.toObject(QuizModel::class.java)!!
            }

        return quizModel
    }
    suspend fun putPost(post: Post){
        firestore.collection(Constants.KEY_COLLECTIONS_POSTS).add(post).await()
    }

    suspend fun getTeacherSubject(teacherId:String):String{
       val query= firestore.collection(Constants.KEY_COLLECTIONS_USERS).document(teacherId).get().await()
       val user= query.toObject(User::class.java)
        return user!!.subject!!
    }
     fun putPostImage(uri: Uri,date:String): UploadTask {

        val query = storage.reference.child(Constants.KEY_STORAGE_IMAGE + date).putFile(uri)


        return query
    }

    suspend fun getSubjectList():List<String>{
        val list= mutableListOf<String>()
       val query= firestore.collection(Constants.KEY_COLLECTIONS_SUBJECTS).get().await()
        if (!query.isEmpty){
            for (item in query){
                list.add(item["name"].toString())
            }
        }
        return list
    }

    suspend fun getPostForSubject(subject: String): MutableList<Post> {
       val query= firestore.collection(Constants.KEY_COLLECTIONS_POSTS).whereEqualTo(Constants.KEY_POST_SUBJECT,subject).get().await()
       return query.toObjects(Post::class.java)
    }





}