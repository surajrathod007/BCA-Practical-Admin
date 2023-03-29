package com.surajmanshal.bcapracticaladmin.programs_management

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surajmanshal.bcapracticaladmin.activity.ui.theme.BCAPracticalAdminTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgramsManagementActivity : ComponentActivity() {
    private val vm by lazy { viewModels<ProgramsViewModel>() }
    private val fullWidthVerticalArrangement = Modifier
        .fillMaxWidth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCAPracticalAdminTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    with(vm.value) {
                        val etTitle by programTitle.collectAsState()
                        val etCode by programCode.collectAsState()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Row (modifier = Modifier.fillMaxWidth()
                                , horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){

                                    val semSubject by semSubjects.collectAsState()
                                    val selectedSem by programSem.collectAsState()
                                    val selectedSub by programSubject.collectAsState()
                                    val selectedUnit by programUnit.collectAsState()

                                    dropDown(items = semesters, selectedItem = selectedSem){ sem ->
                                        setProgramSemester(sem)
                                        semSubjects.value = subjects[semesters.indexOf(sem)]
//                                    setProgramSubject(semSubject.first())
                                    }
                                    dropDown(items = semSubject , selectedSub){
                                        setProgramSubject(it)
                                    }
                                    dropDown(items = units , selectedUnit){
                                        setProgramUnit(it)
                                    }
                                }
                                textBox(etTitle,"Title") { programTitle.value = it }
                                textBox(etCode, "Program Code") { programCode.value = it }
                            }
                            Button(modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .align(Alignment.BottomCenter)
                                ,
                                shape = RoundedCornerShape(28.dp),

                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch{
                                        addProgram(){
                                            Toast.makeText(this@ProgramsManagementActivity,
                                                "Program ${if (!it) "Not" else ""} Uploaded",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    Toast.makeText(
                                        this@ProgramsManagementActivity,
                                        "Adding",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                Text(text = "Add Program" , fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun dropDown(items: List<String>,selectedItem : String, onChange: @Composable ((String) -> Unit)? = null ) {
        var mExpanded by remember {
            mutableStateOf(false)
        }
        var selectedItem by remember {
            mutableStateOf(selectedItem)
        }
        Box() {
            Column {
                OutlinedTextField(
                    value = selectedItem,
                    onValueChange = {
                        selectedItem = it
                                    },
                    modifier = Modifier.width(108.dp).clickable(false){})
                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                ) {
                    items.forEach {
                        DropdownMenuItem(onClick = {
                            mExpanded = false
                            selectedItem = it
                        }) {
                            Text(text = it, modifier = Modifier
                                .padding(16.dp, 2.dp))
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable(
                        onClick = { mExpanded = true }
                    )
            )
        }
        onChange?.invoke(selectedItem)
    }

    @Composable
    fun textBox(txt: String, label : String,  onChange: (String) -> Unit) {
        TextField(
            value = txt,
            onValueChange = { onChange(it) },
            modifier = fullWidthVerticalArrangement,
            label = { Text(text = label)}
        )
    }
}

