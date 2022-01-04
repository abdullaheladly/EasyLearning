package com.abdullah996.easylearning.utill

import android.content.Context
import android.content.SharedPreferences

class PreferenceManger(context: Context) {
    lateinit  var sharedPreferences:SharedPreferences

    init {
        sharedPreferences=context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE)
    }
    fun putBoolean(key:String,value:Boolean){
        var editor=sharedPreferences.edit()
        editor.putBoolean(key,value)
        editor.apply()

    }
    fun getBoolean(key: String):Boolean{
        return sharedPreferences.getBoolean(key,false)
    }
    fun putString(key: String,value: String){
        var editor=sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getString(key: String): String? {
        return sharedPreferences.getString(key,null)
    }

    fun clear(){
        val editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}