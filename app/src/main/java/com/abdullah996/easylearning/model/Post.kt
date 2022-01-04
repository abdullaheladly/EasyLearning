package com.abdullah996.easylearning.model

import android.net.Uri
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId
    var postId:String?=null,
    var postText: String?=null,
    var postImage: String?=null,
    var postPdf: String?=null,
    var posterId: String?=null,
    var postSubject:String?=null,
    var postDate:String?=null
)
