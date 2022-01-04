package com.abdullah996.easylearning.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.abdullah996.easylearning.databinding.FragmentPostsForSubjectBinding
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PostsForSubjectFragment : Fragment() {
    private val mainViewModel:MainViewModel by viewModels()

    private  var _binding: FragmentPostsForSubjectBinding?=null
    private val binding get() = _binding!!

    private val args by navArgs<PostsForSubjectFragmentArgs>()

    private lateinit var subject:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentPostsForSubjectBinding.inflate(layoutInflater,container,false)
        subject=args.subject
        lifecycleScope.launch {
            mainViewModel.getPostsForSubject(subject)
            Toast.makeText(requireContext(), mainViewModel.subjectPost.size.toString(), Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


}