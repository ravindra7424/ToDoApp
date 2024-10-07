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
import com.bumptech.glide.Glide
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
    private var uri: Uri=Uri.EMPTY
    private lateinit var image: ImageView
    private var flag:Int = 0
    fun setListener(listener: HomeFragment){
        this.listener=listener
    }
    companion object{
     const val TAG="AddToDoFragment"
     @JvmStatic
     fun newInstance(taskid :String,task: String,image_url: String)=AddTodoFragment().apply {
         arguments=Bundle().apply {
             putString("taskid",taskid)
             putString("task",task)
             putString("image_uri",image_url)
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
            TodoData=ToDoData(arguments?.getString("taskid").toString(),arguments?.getString("task").toString(),arguments?.getString("image_uri").toString())
        binding.todotext.setText(TodoData?.task)
            Glide.with(requireContext()).load(Uri.parse(TodoData!!.image_uri)).into(binding.firebaseimage)
            uri=Uri.parse(TodoData!!.image_uri)
        }
        registerEvents()
        uploadevents()
    }
    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener{
            val Todotask=binding.todotext.text.toString()
            if(Todotask.isNotEmpty() || flag==1)
            {

                if(TodoData==null){
                    listener.OnSaveTask(Todotask,binding.todotext,uri)
                    dismiss()
                }
                else
                {
                    TodoData!!.task=Todotask
                   listener.OnUpdateTask(TodoData!!,binding.todotext,uri)
                    dismiss()}
            }
            else
            {
                Toast.makeText(context,"task is empty",Toast.LENGTH_SHORT).show()
            }

        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }
    private fun uploadevents() {
        Toast.makeText(context,"starting of upload events",Toast.LENGTH_SHORT).show()

        val gallerImage=registerForActivityResult(
            ActivityResultContracts.GetContent(),
            {
                binding.firebaseimage.setImageURI(it)
                if (it != null) {
                    uri=it
                    binding.firebaseimage.setImageURI(it)
                    if(TodoData!=null)
                        TodoData!!.image_uri=uri.toString()
                }
            })
        binding.btnChoose.setOnClickListener {
            gallerImage.launch("image/*")
                }
            binding.btnUpload.setOnClickListener{
                if(uri.equals(Uri.EMPTY)){
                    Toast.makeText(context,"Image not Selected",Toast.LENGTH_SHORT).show()
                }else
                { val storageref=storageref.getReference("images")
              val img_upload= storageref.child(System.currentTimeMillis().toString()).putFile(uri)
                img_upload.addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                    Log.d(TAG, "Upload is $progress% done")
                }.addOnPausedListener {
                    Log.d(TAG, "Upload is paused")
                }.addOnSuccessListener { task ->
                    task.storage.downloadUrl.addOnSuccessListener {
                        uri=it
                        flag=1
                       //Glide.with(requireContext()).load(it).into(binding.todoClose)
                        Toast.makeText(context,"Image Uploaded Successfully",Toast.LENGTH_SHORT).show()
                    }
                }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            }
                    //.addOnCompleteListener{
//                    val userId= FirebaseAuth.getInstance().currentUser!!.uid
//                    val map= mapOf(
//                        "url" to it.toString(),
//                        "task" to "hero"
//                    )
//                    val databaseReference=FirebaseDatabase.getInstance().getReference("ToDoApp")
//                    databaseReference.child(userId).child("url").push().setValue(map).addOnCompleteListener{
//                        Toast.makeText(context,"Succes",Toast.LENGTH_SHORT).show()
//                    }
//                        .addOnFailureListener{
//                            Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
//                        }


         //   }

             }
    }
interface DialogNextListener{
    fun OnSaveTask(todoTask:String, todotext:TextInputEditText,image_url: Uri)
    fun OnUpdateTask(toDoData: ToDoData,toD0Text: TextInputEditText,uri:Uri)
}

