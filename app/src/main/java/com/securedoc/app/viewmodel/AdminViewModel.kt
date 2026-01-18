package com.securedoc.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securedoc.app.data.AppDatabase
import com.securedoc.app.data.StudentEntity
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val studentDao = AppDatabase.getInstance().studentDao()

    private val _students = MutableLiveData<List<StudentEntity>>(emptyList())
    val students: LiveData<List<StudentEntity>> = _students

    fun loadStudents() {
        viewModelScope.launch {
            _students.value = studentDao.getAll()
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            studentDao.delete(student)
            _students.value = studentDao.getAll()
        }
    }
}
