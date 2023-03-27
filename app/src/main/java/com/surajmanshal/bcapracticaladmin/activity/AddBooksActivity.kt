package com.surajmanshal.bcapracticaladmin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar.LayoutParams
import androidx.core.view.forEach
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.surajmanshal.bcapracticaladmin.R
import com.surajmanshal.bcapracticaladmin.databinding.ActivityAddBooksBinding
import com.surajmanshal.bcapracticaladmin.model.Book
import java.util.jar.Attributes

class AddBooksActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBooksBinding
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupSpinners()
        setupClickListeners()
    }

    private fun setupClickListeners() {

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
                when(it){
                    is EditText ->{
                        if(it.text.toString() == ""){
                            Toast.makeText(this, "Please add some value in edit text", Toast.LENGTH_SHORT).show()
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
            val tagsList = tags.split(", ")
            val book = Book(
                title = binding.edBookTitle.text.toString(),
                description = binding.edBookDescription.text.toString(),
                imageUrl = binding.edBookPosterImage.text.toString(),
                sem = binding.spSemester.selectedItem.toString(),
                sub = binding.spSubject.selectedItem.toString(),
                downloadLink = binding.edBookDownloadLink.text.toString(),
                demoImages = demoImages,
                tags = tagsList
            )
            val collection = db.collection("books")
            val newBook = collection.document()
            newBook.set(book).addOnSuccessListener {
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinners() {
        val sem = arrayOf<String>("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6")
        val semAdapter = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem
        )

        val sem1Subject = arrayOf("IPLC", "HTML")
        val sem1Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem1Subject
        )

        val sem2Subject = arrayOf("ACP", "DBMS 1", "HTML/JS")
        val sem2Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem2Subject
        )

        val sem3Subject = arrayOf("OOCP", "DS_ALGO")
        val sem3Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem3Subject
        )

        val sem4Subject = arrayOf("CJ", "DBMS 2", "WPC#")
        val sem4Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem4Subject
        )

        val sem5Subject = arrayOf("PYTHON", "ASP.NET")
        val sem5Sub = ArrayAdapter<String>(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            sem5Subject
        )

        val sem6Subject = arrayOf("WEB APP DEV")
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
    }
}