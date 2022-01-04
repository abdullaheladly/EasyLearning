package com.abdullah996.easylearning.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abdullah996.easylearning.R
import com.abdullah996.easylearning.listeners.SubjectListeners

class SubjectsRowAdapter(val subjectListeners: SubjectListeners):
    RecyclerView.Adapter<SubjectsRowAdapter.SubjectsViewHolder>() {

    class SubjectsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)



    private val diffUtil=object:DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
          return  oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }
    }

    private val recycleListDiffer=AsyncListDiffer(this,diffUtil)

      var subjects: List<String>
     get() =recycleListDiffer.currentList
         set(value) =recycleListDiffer.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectsViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.subject_row,parent,false)
        return SubjectsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectsViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.subject_name).text=subjects[position]
        holder.itemView.setOnClickListener {
           subjectListeners.onSubjectItemClick(subjects[position])
        }
    }

    override fun getItemCount(): Int {
        return subjects.size
    }


}