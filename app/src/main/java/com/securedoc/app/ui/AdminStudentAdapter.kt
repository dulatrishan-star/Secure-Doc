package com.securedoc.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.securedoc.app.data.StudentEntity
import com.securedoc.app.databinding.ItemStudentBinding

class AdminStudentAdapter(
    private val onDelete: (StudentEntity) -> Unit
) : RecyclerView.Adapter<AdminStudentAdapter.StudentViewHolder>() {

    private val items = mutableListOf<StudentEntity>()

    fun submitList(newItems: List<StudentEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: StudentEntity) {
            binding.studentUsername.text = student.username
            binding.deleteStudentButton.setOnClickListener {
                onDelete(student)
            }
        }
    }
}
