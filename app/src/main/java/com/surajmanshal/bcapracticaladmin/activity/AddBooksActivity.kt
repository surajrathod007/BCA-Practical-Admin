package com.surajmanshal.bcapracticaladmin.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar.LayoutParams
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.surajmanshal.bcapracticaladmin.R
import com.surajmanshal.bcapracticaladmin.adapter.BookDemoImagesAdapter
import com.surajmanshal.bcapracticaladmin.databinding.ActivityAddBooksBinding
import com.surajmanshal.bcapracticaladmin.model.Book
import com.surajmanshal.bcapracticaladmin.viewmodel.BooksViewModel
import java.util.jar.Attributes

class AddBooksActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBooksBinding
    lateinit var vm : BooksViewModel
    val db = FirebaseFirestore.getInstance()
    val storage = Firebase.storage.reference
    val CHOOSE_IMAGE_REQ_CODE = 101
    val READ_EXTERNAL_STORAGE_REQ_CODE = 102
    val CHOOSE_BOOK_POSTER = 103
    var posterUrl = ""
    var urls = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(BooksViewModel::class.java)

        storage.child("images/1682227795508Image.jpg").downloadUrl.addOnSuccessListener {
            Toast.makeText(this@AddBooksActivity, "url : $it", Toast.LENGTH_LONG).show()
        }

        setUpObservers()
        setupSpinners()
        setupClickListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==Activity.RESULT_OK && requestCode==CHOOSE_IMAGE_REQ_CODE){
            if(data!=null){
                try {
                    val p = ProgressDialog(this)
                    p.setTitle("Please wait...")
                    p.show()
                    val imgUri = data.data
                    val filename = System.currentTimeMillis().toString()+"Image.jpg"
                    val uploadTask = imgUri?.let { storage.child("images/$filename").putFile(it) }
                    uploadTask?.addOnSuccessListener {
                        Toast.makeText(this@AddBooksActivity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                        storage.child("images/$filename").downloadUrl.addOnSuccessListener {
                            Toast.makeText(this@AddBooksActivity, "url : $it", Toast.LENGTH_LONG).show()
                            vm.addImage(it.toString())
                            p.dismiss()
                        }
                    }?.addOnFailureListener {
                        Toast.makeText(this@AddBooksActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                    }

                }catch (e : Exception){
                    Toast.makeText(this@AddBooksActivity, " Choose Image : ${e.message}", Toast.LENGTH_SHORT).show()

                }
            }
        }else if(resultCode==Activity.RESULT_OK && requestCode==CHOOSE_BOOK_POSTER){
            if(data != null){
                try{
                    val p = ProgressDialog(this)
                    p.setTitle("Please wait...")
                    p.show()
                    val imgUri = data.data
                    val filename = System.currentTimeMillis().toString()+"Image.jpg"
                    val uploadTask = imgUri?.let { storage.child("images/$filename").putFile(it) }

                    uploadTask?.addOnSuccessListener {
                        Toast.makeText(this@AddBooksActivity, "Image Uploaded", Toast.LENGTH_SHORT).show()
                        storage.child("images/$filename").downloadUrl.addOnSuccessListener {
                            Toast.makeText(this@AddBooksActivity, "Poster url : $it", Toast.LENGTH_LONG).show()
                            Glide.with(this@AddBooksActivity).load(it).into(binding.imgBookPoster)
                            posterUrl = it.toString()
                            p.dismiss()
                        }
                    }?.addOnFailureListener {
                        Toast.makeText(this@AddBooksActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                        p.dismiss()
                    }

                }catch (e : Exception){

                }
            }
        }
    }

    private fun setUpObservers() {
        vm.demoImages.observe(this){
            binding.rvDemoImages.adapter = BookDemoImagesAdapter(vm){
                Toast.makeText(this@AddBooksActivity, "Choose Image", Toast.LENGTH_SHORT).show()
                chooseImage()
            }
        }
        vm.demoImagesUrl.observe(this){
            Toast.makeText(this@AddBooksActivity, "$it", Toast.LENGTH_SHORT).show()
        }
    }

    fun chooseImage(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val storageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // IT IS DEPRECATED FIND LATEST METHOD FOR IT
            startActivityForResult(storageIntent, CHOOSE_IMAGE_REQ_CODE)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),READ_EXTERNAL_STORAGE_REQ_CODE)
        }
    }

    fun choosePoster(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val storageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // IT IS DEPRECATED FIND LATEST METHOD FOR IT
            startActivityForResult(storageIntent, CHOOSE_BOOK_POSTER)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),READ_EXTERNAL_STORAGE_REQ_CODE)
        }
    }

    private fun setupClickListeners() {

        binding.imgBookPoster.setOnClickListener {
            choosePoster()
        }

        binding.btnAddDemoImage.setOnClickListener {
            val layout = binding.llDemoImageContainer
            val count = layout.childCount
            val edDemoImage = EditText(this)
            edDemoImage.hint = "Enter demo image url for ${count + 1}st image..."
            edDemoImage.id = View.generateViewId()
            edDemoImage.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            layout.addView(edDemoImage)
        }

        binding.btnUploadBook.setOnClickListener {
            //validation
            binding.llMainBookLayout.forEach {
                when (it) {
                    is EditText -> {
                        if (it.text.toString() == "") {
                            Toast.makeText(
                                this,
                                "Please add some value in edit text",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }
                    }
                }
            }

            //make the book

            var demoImages = mutableListOf<String>()
            binding.llDemoImageContainer.forEach {
                demoImages.add((it as EditText).text.toString())
            }

            val tags = binding.edBookTags.text.toString()
            var tagsList = tags.split(", ").toMutableList().also {
                it.add(0,binding.spSubject.selectedItem.toString())
            }
            val dLink = binding.spFileType.selectedItem.toString() + "^" + binding.edBookDownloadLink.text.toString()
            val newUrls = vm.demoImages.value?.apply {
                remove(last())
            }
            val book = Book(
                title = binding.edBookTitle.text.toString(),
                description = binding.edBookDescription.text.toString(),
                imageUrl = posterUrl,
                sem = binding.spSemester.selectedItem.toString(),
                sub = binding.spSubject.selectedItem.toString(),
                downloadLink = dLink,
                demoImages = newUrls,
                tags = tagsList
            )
            val collection = db.collection("books")
            val newBook = collection.document()
            newBook.set(book).addOnSuccessListener {
                Toast.makeText(this, "Book added successfully ${dLink.substringBefore("^")}", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun setupSpinners() {
        val sem = arrayOf<String>("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6")
        val semAdapter = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem
        )

        val sem1Subject = arrayOf(
            "CC-101 Introduction to Computer and Emerging Technologies",
            "CC-103 INTERNET AND HTML",
            "CC-104 Fundamental Mathematical Concepts",
            "CC-105 C Practical",
            "CC-106 INTERNET AND HTML - PRACTICALS",
            "CC-107 Open Source Office Automation (Practicals)"
        )
        val sem1Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem1Subject
        )

        val sem2Subject = arrayOf(
            "CC-108 Advanced C Programming",
            "CC-111 Discrete Mathematics",
            "CC-112 Advance C Practical",
            "CC-113 DYNAMIC HTML & Advance Technology- PRACTICALS",
            "CC-114 Database Practical"
        )
        val sem2Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem2Subject
        )

        val sem3Subject = arrayOf(
            "CC-201 Computer Organization",
            "CC-202 Data Structures",
            "CC-203 Object Oriented Concepts and Programming",
            "CC-204 Fundamentals of Operating System",
            "CC-205 STATISTICAL METHODS",
            "CC-206 Data Structures Practicals",
            "CC-207 C++ Practicals"
        )
        val sem3Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem3Subject
        )

        val sem4Subject = arrayOf(
            "CC-208 Database Management System - II",
            "CC-209 System Analysis, QA and Testing",
            "CC-210 CORE JAVA",
            "CC-211 E-COMMERCE",
            "CC-212 DATABASE MANAGEMENT SYSTEM-II PRACTICAL",
            "CC-213 WINDOWS PROGRAMMING PRACTICAL USING C#",
            "CC-214 CORE JAVA PRACTICAL"
        )
        val sem4Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem4Subject
        )

        val sem5Subject = arrayOf(
            "CC-301 Web Applications Development – I Using C#",
            "CC-302 Python Programming",
            "CC-303 Computer Networks",
            "CC-304 Web Application Development – I Using C# Practical",
            "CC-305 Python Programming Practicals",
            "CC-306 Software Development Project - I"
        )
        val sem5Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem5Subject
        )

        val sem6Subject = arrayOf(
            "CC-307 Software Testing",
            "CC-308 Introduction to Data Mining and Data Ware housing",
            "CC-309 Introduction to Artificial Intelligence and Machine Learning",
            "CC-310 Web Application Development – II Practical",
            "CC-311 Shell Programming Practical",
            "CC-312 Software Development Project –II"
        )
        val sem6Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem6Subject
        )

        binding.spSemester.adapter = semAdapter
        binding.spSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (position) {
                    0 -> {
                        binding.spSubject.adapter = sem1Sub
                    }

                    1 -> {
                        binding.spSubject.adapter = sem2Sub
                    }

                    2 -> {
                        binding.spSubject.adapter = sem3Sub
                    }

                    3 -> {
                        binding.spSubject.adapter = sem4Sub
                    }

                    4 -> {
                        binding.spSubject.adapter = sem5Sub
                    }

                    5 -> {
                        binding.spSubject.adapter = sem6Sub
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        //load file type
        val fileTypes = arrayOf("PDF", "ZIP", "JPG", "PNG", "JPEG")
        val fileAdp = ArrayAdapter(
            this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            fileTypes
        )
        binding.spFileType.adapter = fileAdp
    }
}