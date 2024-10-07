package com.example.todoapp.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.todoapp.databinding.FragmentAddTodoBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storageMetadata

class AddTodoFragment : DialogFragment() {
    private lateinit var binding: FragmentAddTodoBinding
    private lateinit var listener: DialogNextListener
    private var TodoData :ToDoData?=null
    private var storageref= Firebase.storage
    private lateinit var uri: Uri
    private lateinit var image: ImageView
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
        uploadevents()
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
    private fun uploadevents() {
        Toast.makeText(context,"starting of upload events",Toast.LENGTH_SHORT).show()

        val gallerImage=registerForActivityResult(
            ActivityResultContracts.GetContent(),
            { binding.firebaseimage.setImageURI(it)
                if (it != null) {
                    uri=it
                }
            })
        binding.btnChoose.setOnClickListener {
            gallerImage.launch("image/*")
                }
            binding.btnUpload.setOnClickListener{
                val storageref=storageref.getReference("images")
                storageref.child(System.currentTimeMillis().toString()).putFile(uri).addOnCompleteListener{
                    val userId= FirebaseAuth.getInstance().currentUser!!.uid
                    val map= mapOf(
                        "url" to it.toString(),
                        "task" to "hero"
                    )
                    val databaseReference=FirebaseDatabase.getInstance().getReference("Tasks")
                    databaseReference.child(userId).child("url").push().setValue(map).addOnCompleteListener{
                        Toast.makeText(context,"Succes",Toast.LENGTH_SHORT).show()
                    }
                        .addOnFailureListener{
                            Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
                        }

            }

             }
    }

}
interface DialogNextListener{
    fun OnSaveTask(todoTask:String, todotext:TextInputEditText)
    fun OnUpdateTask(toDoData: ToDoData,toD0Text: TextInputEditText)
}

