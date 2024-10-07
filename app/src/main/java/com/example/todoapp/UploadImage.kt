package com.example.todoapp
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ImageUploadBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.net.URI

class UploadImage : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var btnChoose:Button
    private lateinit var btnUpload:Button
    private var storageref=Firebase.storage
    private lateinit var uri:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_upload)
      storageref= FirebaseStorage.getInstance()
      image=findViewById(R.id.firebaseimage)
      btnChoose=findViewById(R.id.btnChoose)
      btnUpload=findViewById(R.id.btnUpload)
      val gallerImage=registerForActivityResult(
          ActivityResultContracts.GetContent(),
          {image.setImageURI(it)
              if (it != null) {
                  uri=it
              }
          })

     btnChoose.setOnClickListener{
         gallerImage.launch("image/*")
     }
        btnUpload.setOnClickListener{
            storageref.getReference("images").child(System.currentTimeMillis().toString()).putFile(uri).addOnCompleteListener{
                val userId= FirebaseAuth.getInstance().currentUser!!.uid
                val map= mapOf(
                    "url" to it.toString()
                )
                val databaseReference=FirebaseDatabase.getInstance().getReference("userImages")
                databaseReference.child(userId).setValue(map).addOnCompleteListener{
                    Toast.makeText(this,"Succes",Toast.LENGTH_SHORT).show()
                }
                    .addOnFailureListener{
                        Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}