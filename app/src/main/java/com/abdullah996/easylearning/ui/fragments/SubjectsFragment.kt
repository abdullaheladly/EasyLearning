package com.abdullah996.easylearning.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.adapters.SubjectsRowAdapter
import com.abdullah996.easylearning.databinding.FragmentSubjectsBinding
import com.abdullah996.easylearning.listeners.SubjectListeners
import com.abdullah996.easylearning.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SubjectsFragment() : Fragment(),SubjectListeners {
    private val mainViewModel:MainViewModel by viewModels()
    private  var _binding: FragmentSubjectsBinding?=null
    private val binding get() = _binding!!
    private val subjectsRowAdapter: SubjectsRowAdapter by lazy { SubjectsRowAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentSubjectsBinding.inflate(layoutInflater,container,false)
        lifecycleScope.launch (Dispatchers.Main){
            mainViewModel.getSubjectList()
            setupRecycleView()
        }

        return binding.root
    }

    private fun setupRecycleView() {
        binding.recycleView.adapter=subjectsRowAdapter
        binding.recycleView.layoutManager=LinearLayoutManager(requireContext())
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        subjectsRowAdapter.subjects=mainViewModel.subjectList

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onSubjectItemClick(subject: String) {
        val action=SubjectsFragmentDirections.actionSubjectsFragmentToPostsForSubjectFragment(subject)
        findNavController().navigate(action)
    }

}