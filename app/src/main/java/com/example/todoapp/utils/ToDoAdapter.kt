package com.example.todoapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.EachToItemBinding
import com.example.todoapp.fragments.HomeFragment
class ToDoAdapter(private val list:MutableList<ToDoData>) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {
   private var listener:ToDoAdapterInterface?=null
    fun setListener(listener: ToDoAdapterInterface)
    {
        this.listener=listener
    }
  inner class  ToDoViewHolder(val binding :EachToItemBinding):RecyclerView.ViewHolder(binding.root) {}
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
          val binding =
              EachToItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
          return ToDoViewHolder(binding)
      }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
      with(holder){
          with(list[position]){
              binding.todoTask.text=this.task
              binding.deleteTask.setOnClickListener{
                listener!!.ondeletebtnclicked(this)
              }
              binding.editTask.setOnClickListener{
                  listener!!.oneditTaskbtnclicked(this)
              }
          }
      }
    }

    interface ToDoAdapterInterface{
        fun ondeletebtnclicked(toDoData: ToDoData)
        fun oneditTaskbtnclicked(toDoData: ToDoData)
    }
}
