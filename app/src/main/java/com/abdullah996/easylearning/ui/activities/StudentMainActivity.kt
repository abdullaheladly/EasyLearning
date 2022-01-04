package com.abdullah996.easylearning.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abdullah996.easylearning.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
    }
}