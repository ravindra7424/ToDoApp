package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoAdapter
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), DialogNextListener, ToDoAdapter.ToDoAdapterInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentHomeBinding
    private lateinit var navControl: NavController
    private lateinit var dataref:DatabaseReference
    private var Todofragment:AddTodoFragment?=null
    private lateinit var adapter: ToDoAdapter
    private lateinit var ToDoList:MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getData()
        registerEvents()
        }

    private fun getData() {
        dataref.child("tasks").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ToDoList.clear()
                for(task in snapshot.children){
                    val todoTask=task.key?.let {
                        ToDoData(it,task.value.toString())
                    }
                    if(todoTask!=null)
                        ToDoList.add(todoTask)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener{
            if(Todofragment!=null)
                childFragmentManager.beginTransaction().remove(Todofragment!!).commit()
            Todofragment= AddTodoFragment()
            Todofragment!!.setListener(this)
            Todofragment!!.show(childFragmentManager,AddTodoFragment.TAG)
        }
        binding.btnsout.setOnClickListener(){
            auth.signOut()
            findNavController().navigate(
                R.id.signInFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
            )

        }
    }

    private fun init(view: View) {
        navControl= Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        dataref=FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerview1.setHasFixedSize(true)
        binding.recyclerview1.layoutManager=LinearLayoutManager(context)
        ToDoList= mutableListOf()
        adapter=ToDoAdapter(ToDoList)
        adapter.setListener(this)
        binding.recyclerview1.adapter=adapter
    }

    override fun OnSaveTask(todoTask: String, todotext: TextInputEditText) {
        dataref.child("tasks").push().setValue(todoTask).addOnCompleteListener {
            if (it.isSuccessful) {
                    Toast.makeText(context, "$todoTask", Toast.LENGTH_SHORT).show()
                    todotext.text = null
                }
            else {
                context?.let { ctx ->
                    Toast.makeText(ctx, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
            Todofragment?.dismiss()
        }

    }
    override fun OnUpdateTask(toDoData: ToDoData, toD0Text: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskid] = toDoData.task
        dataref.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            Todofragment!!.dismiss()
        }
    }

    override fun ondeletebtnclicked(toDoData: ToDoData) {
            dataref.child(toDoData.taskid).removeValue().addOnCompleteListener{
                if(it.isSuccessful)
                {
                 Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context,it.exception.toString(),Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun oneditTaskbtnclicked(toDoData: ToDoData) {
        if(Todofragment!=null)
             childFragmentManager.beginTransaction().remove(Todofragment!!).commit()
        Todofragment=AddTodoFragment.newInstance(toDoData.taskid,toDoData.task)
        Todofragment!!.setListener(this)
        Todofragment!!.show(childFragmentManager,AddTodoFragment.TAG)
    }
}


