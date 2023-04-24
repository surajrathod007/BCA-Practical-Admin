package com.surajmanshal.bcapracticaladmin.model

data class Book(
    val title : String,
    val description : String,
    val imageUrl : String,
    val sem : String,
    val sub : String,
    val downloadLink : String,
    val demoImages : MutableList<String>? = null,
    val tags : List<String>? = null
)
