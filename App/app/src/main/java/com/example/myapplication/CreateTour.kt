package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.UserData
import com.example.myapplication.view.UserAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.annotations.Nullable
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*


class CreateTour : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var finishBtn: FloatingActionButton
    private lateinit var recv:RecyclerView
    private lateinit var userList:ArrayList<UserData>
    private lateinit var stopList:MutableList<Stop>
    private lateinit var imageList:MutableList<String>
    private lateinit var userAdapter: UserAdapter
    private lateinit var finishTourName: String
    private lateinit var addStop: Button
    private lateinit var list: RecyclerView
    private var database: DatabaseReference = Firebase.database.reference
    var imageUri: Uri? = null
    var progressDialog: ProgressDialog? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tour)

        //addStop = findViewById(R.id.add_stop)


        userList = ArrayList()
        stopList = ArrayList()
        imageList = ArrayList()
        /**set find Id*/
        addsBtn = findViewById(R.id.addingBtn)
        finishBtn = findViewById(R.id.finishingBtn)
        recv = findViewById(R.id.mRecycler)
        /**set Adapter*/
        userAdapter = UserAdapter(this,userList)
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(this)
        recv.adapter = userAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addInfo() }
        finishBtn.setOnClickListener {
            addTour()
        }

        //addStop.setOnClickListener {
            //val intent = Intent(this, AddStop::class.java)
            //startActivity(intent)
        //}
    }

    private fun writeNewTour(name: String, stops: MutableList<Stop>, images: MutableList<String>) {
        val tour = Tour(name, stops, images)
        //database.child("tours").setValue(tour)
        database.child("tours").child(tour.name).setValue(tour)
    }

    private fun addTour() {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.add_tour_name,null)
        val tourName = v.findViewById<EditText>(R.id.tourName)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val tourname = tourName.text.toString()
            finishTourName = tourname
            for (e in userList) {
                val newStop = Stop (e.userName, e.userMb, e.s)
                stopList.add(newStop)
            }
            writeNewTour(finishTourName, stopList, imageList)
            finish()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()


    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.add_item,null)
        /**set view*/
        val userName = v.findViewById<EditText>(R.id.userName)
        val userNo = v.findViewById<EditText>(R.id.userNo)
        val description = v.findViewById<EditText>(R.id.description)
        val select = v.findViewById<Button>(R.id.select)
        val upload = v.findViewById<Button>(R.id.upload)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        select.setOnClickListener {
            selectImage()
        }
        upload.setOnClickListener {
            uploadImage()
        }
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val names = userName.text.toString()
            val number = userNo.text.toString()
            val desc = description.text.toString()
            userList.add(UserData("Name: $names","Address: $number", "Description: $desc"))
            userAdapter.notifyDataSetChanged()
            Toast.makeText(this,"Stop Added",Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    private fun uploadImage() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Uploading File....")
        progressDialog!!.show()
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val now = Date()
        val fileName: String = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        imageUri?.let {
            storageReference!!.putFile(it)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot?> {
                    Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT)
                        .show()
                    if (progressDialog!!.isShowing) progressDialog!!.dismiss()
                }).addOnFailureListener(OnFailureListener {
                    if (progressDialog!!.isShowing) progressDialog!!.dismiss()
                    Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()
                })
        }
        imageUri?.let { imageList.add(fileName) }
    }


    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null && data.data != null) {
            imageUri = data.data
        }
    }

}