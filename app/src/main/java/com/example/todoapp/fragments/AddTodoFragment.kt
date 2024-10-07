package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoapp.databinding.FragmentAddTodoBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText

class AddTodoFragment : DialogFragment() {
    private lateinit var binding: FragmentAddTodoBinding
    private lateinit var listener: DialogNextListener
    private var TodoData :ToDoData?=null
    fun setListener(listener: HomeFragment){
        this.listener=listener
    }
    companion object{
     const val TAG="AddToDoFragment"
     @JvmStatic
     fun newInstance(taskid :String,task: String)=AddTodoFragment().apply {
         arguments=Bundle().apply {
             putString("taskid",taskid)
             putString("task",task)
         }
     }

     }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentAddTodoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments!=null) {
            TodoData=ToDoData(arguments?.getString("taskid").toString(),arguments?.getString("task").toString())
        binding.todotext.setText(TodoData?.task)

        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener{
            val Todotask=binding.todotext.text.toString()
            if(Todotask.isNotEmpty())
            {

                if(TodoData==null){
                    listener.OnSaveTask(Todotask,binding.todotext)
                }
                else
                {
                    TodoData!!.task=Todotask
                   listener.OnUpdateTask(TodoData!!,binding.todotext)}
            }
            else
            {
                Toast.makeText(context,"task is empty",Toast.LENGTH_SHORT).show()
            }
               dismiss()
        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }

}
interface DialogNextListener{
    fun OnSaveTask(todoTask:String, todotext:TextInputEditText)
    fun OnUpdateTask(toDoData: ToDoData,toD0Text: TextInputEditText)
}

