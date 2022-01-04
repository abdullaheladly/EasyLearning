package com.abdullah996.easylearning.model

import com.google.firebase.firestore.DocumentId
import com.google.gson.annotations.SerializedName

data class User(
    @DocumentId
    var id:String?,
    @SerializedName("email")
    var email:String?,
    @SerializedName("password")
    var password:String?,
    @SerializedName("image")
    var image:String?,
    @SerializedName("phone")
    var phone:String?,
    @SerializedName("name")
    var name:String?,
    @SerializedName("type")
    var type:String?,
    @SerializedName("subject")
    var subject:String?,

){
    constructor():this(null,null,null,null,null,null,null,null)
}