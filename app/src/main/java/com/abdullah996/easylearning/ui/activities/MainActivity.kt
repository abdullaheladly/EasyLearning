package com.abdullah996.easylearning.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.abdullah996.easylearning.adapters.PagerAdapter
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.databinding.ActivityMainBinding
import com.abdullah996.easylearning.ui.fragments.LoginFragment
import com.abdullah996.easylearning.ui.fragments.SignUpFragment
import com.abdullah996.easylearning.utill.Constants
import com.abdullah996.easylearning.utill.Constants.Companion.IS_LOGGED_IN
import com.abdullah996.easylearning.utill.PreferenceManger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManger: PreferenceManger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        preferenceManger= PreferenceManger(this)
        if (preferenceManger.getBoolean(IS_LOGGED_IN)){
            startActivity(Intent(this, HomeActivity::class.java))
        }
        lifecycleScope.launch {
            delay(500)
            setContentView(binding.root)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val fragments = ArrayList<Fragment>()
            fragments.add(LoginFragment())
            fragments.add(SignUpFragment())
            val titles = ArrayList<String>()
            titles.add("Login")
            titles.add("Sign Up")

            val adapter = PagerAdapter(
                fragments,
                titles,
                supportFragmentManager
            )
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }

    }

}