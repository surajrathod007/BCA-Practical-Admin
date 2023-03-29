package com.surajmanshal.bcapracticaladmin.programs_management

import android.content.ContentValues.TAG
import android.util.Log
import com.surajmanshal.bcapracticaladmin.activity.FirebaseClientViewModel
import com.surajmanshal.bcapracticaladmin.model.Program
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class ProgramsViewModel : FirebaseClientViewModel() {
    val semesters = listOf(
        "Sem 1",
        "Sem 2",
        "Sem 3",
        "Sem 4",
        "Sem 5",
        "Sem 6",
    )
    val units = listOf(
        "Unit 1",
        "Unit 2",
        "Unit 3",
        "Unit 4",
    )
    private val sem1Subjects = listOf("IPLC", "HTML")
    private val sem2Subjects = listOf("ACP", "DBMS 1", "HTML/JS")
    private val sem3Subjects = listOf("OOCP", "DS_ALGO")
    private val sem4Subjects = listOf("CJ", "DBMS 2", "WPC#")
    private val sem5Subjects = listOf("PYTHON", "ASP.NET")
    private val sem6Subjects = listOf("WEB APP DEV","SPP")
    val subjects = listOf(sem1Subjects,sem2Subjects,sem3Subjects,sem4Subjects,sem5Subjects,sem6Subjects)

    val programTitle = MutableStateFlow("")
    val programCode = MutableStateFlow("")
    val programSem = MutableStateFlow(semesters[0])
    val programSubject = MutableStateFlow(sem1Subjects[0])
    val programUnit = MutableStateFlow(units[0])
    val semSubjects = MutableStateFlow(subjects.first())
    private val programsCollection = db.collection("newPrograms")
    suspend fun addProgram(success : (Boolean) -> Unit) {
        val programId = generateId()
        programsCollection.document()
            .set(
                Program(
                    programId,
                    title = programTitle.value,
                    content = programCode.value,
                    sem = programSem.value,
                    sub = programSubject.value,
                    unit = programUnit.value
                )
            )
            .addOnSuccessListener {
                success(true)
            }.addOnFailureListener {
                Log.e(TAG, "Failed:$it", )
                success(false)
            }
    }

    private suspend fun generateId(): Int {
        return programsCollection.get().await().documents.size + 1
    }

    fun setProgramSemester(sem : String){
        programSem.value = sem
    }
    fun setProgramSubject(subject : String){
        programSubject.value = subject
    }
    fun setProgramUnit(unit : String){
        programUnit.value = unit
    }
}