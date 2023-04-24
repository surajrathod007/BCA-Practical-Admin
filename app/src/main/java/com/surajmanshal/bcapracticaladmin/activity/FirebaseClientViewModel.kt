package com.surajmanshal.bcapracticaladmin.activity

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

open class FirebaseClientViewModel : ViewModel() {
    protected var db : FirebaseFirestore = FirebaseFirestore.getInstance()
}