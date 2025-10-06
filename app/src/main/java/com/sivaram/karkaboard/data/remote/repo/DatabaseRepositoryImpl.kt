package com.sivaram.karkaboard.data.remote.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.SelectState
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class DatabaseRepositoryImpl : DatabaseRepository {
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun getRoles(): LiveData<List<RolesData>> {
        val rolesData = MutableLiveData<List<RolesData>>(emptyList())
        firebaseFireStore.collection(DbConstants.ROLES_TABLE)
            .orderBy("content", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tempList =
                        snapshot.documents.mapNotNull { it.toObject(RolesData::class.java) }
                    Log.d("LogData", "Data -> $tempList")
                    rolesData.value = tempList
                }
            }
        return rolesData
    }

    override fun getCurrentUser(): LiveData<UserData?> {
        val currentUser = firebaseAuth.currentUser
        val userData = MutableLiveData<UserData?>()
        try {
            Log.d("LogData", "CurrentUser -> ${currentUser?.uid}")
            firebaseFireStore.collection(DbConstants.USER_TABLE)
                .whereEqualTo("uid", currentUser?.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList =
                            snapshot.documents.mapNotNull { it.toObject(UserData::class.java) }
                        Log.d("LogData", "Data -> $tempList")
                        if (tempList.isNotEmpty())
                            userData.value = tempList[0]
                    }
                }
            Log.d("LogData", "UserData -> ${userData.value}")
        } catch (e: Exception) {
            userData.value = null
        }
        Log.d("LogData", "UserData -> ${userData.value}")
        return userData
    }

    override suspend fun getAllBatches(): LiveData<List<BatchData>> {
        val allBatchesData = MutableLiveData<List<BatchData>>(emptyList())
        try {
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val batchList =
                            snapshot.documents.mapNotNull { it.toObject(BatchData::class.java) }
                        Log.d("ManageBatchesRepoImpl", "getAllBatches: $batchList")
                        allBatchesData.value = batchList
                    }
                }
        } catch (e: Exception) {
            allBatchesData.value = emptyList()
        }
        return allBatchesData
    }

    override suspend fun getStudentData(uid: String): LiveData<StudentsData?> {
        val stuData = MutableLiveData<StudentsData?>()
        Log.d("LogData", "getStudentData() -> $uid")
        try {
            firebaseFireStore.collection(DbConstants.STUDENT_TABLE).whereEqualTo("uid", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList =
                            snapshot.documents.mapNotNull { it.toObject(StudentsData::class.java) }
                        Log.d("LogData", "Data -> $tempList")
                        stuData.value = tempList[0]
                    }
                }
        } catch (e: Exception) {
            stuData.value = null
        }
        Log.d("LogData", "getStudentData() -> ${stuData.value}")
        return stuData
    }

    override suspend fun getApplicationPortalData(studentId: String): LiveData<List<ApplicationPortalData>> {
        val applicationPortalData = MutableLiveData<List<ApplicationPortalData>>(emptyList())
        Log.d("LogData", "getApplicationPortalData() -> $studentId")
        try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        Log.d("getApplicationPortalData ", "applications -> ${snapshot.documents}")
                        val appliedBatchId = snapshot.documents.map { it.get("batchId") }.toSet()
                        Log.d("getApplicationPortalData ", "applicationId -> $appliedBatchId")
                        firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                            .whereEqualTo("open", true)
                            .addSnapshotListener { batchSnapshot, error ->
                                if (error != null) {
                                    return@addSnapshotListener
                                }
                                if (batchSnapshot != null) {
                                    val result = batchSnapshot.documents.map {
                                        val batchId = it.id
                                        val isApplied = appliedBatchId.contains(batchId)
                                        ApplicationPortalData(
                                            batchData = it.toObject(BatchData::class.java),
                                            isApplied = isApplied
                                        )
                                    }
                                    applicationPortalData.value = result
                                }
                            }
                    }
                }
        } catch (e: Exception) {
            applicationPortalData.value = emptyList()
        }
        Log.d("LogData", "getApplicationPortalData() -> ${applicationPortalData.value}")
        return applicationPortalData
    }

    override suspend fun getAppliedStudentDetail(batchId: String): LiveData<List<AppliedStudentData>> {
        var appliedStudentData = MutableLiveData<List<AppliedStudentData>>(emptyList())
        Log.d("LogData", "getAppliedStudentDetail(): batchId-> $batchId")
        try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
                .whereEqualTo("batchId", batchId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val applicationData =
                            snapshot.documents.mapNotNull { it.toObject(ApplicationData::class.java) }
                        Log.d("Application", "getAppliedStudentDetail(): applicationData -> $applicationData")
                        if (applicationData.isNotEmpty()) {
                            for (application in applicationData) {
                                firebaseFireStore.collection(DbConstants.STUDENT_TABLE)
                                    .whereEqualTo("uid", application.studentId)
                                    .addSnapshotListener { studentSnapshot, error ->
                                        if (error != null) {
                                            return@addSnapshotListener
                                        }
                                        if (studentSnapshot != null) {
                                            val studentData = studentSnapshot.documents.mapNotNull {
                                                it.toObject(StudentsData::class.java)
                                            }
                                            Log.d(
                                                "Application",
                                                "getAppliedStudentDetail():studentsData -> $studentData"
                                            )
                                            for(student in studentData) {
                                                firebaseFireStore.collection(DbConstants.USER_TABLE)
                                                    .whereEqualTo("uid", student.uid)
                                                    .addSnapshotListener { userSnapshot, error ->
                                                        if (error != null) {
                                                            return@addSnapshotListener
                                                        }
                                                        if (userSnapshot != null) {
                                                            val userData =
                                                                userSnapshot.documents.mapNotNull {
                                                                    it.toObject(UserData::class.java)
                                                                }
                                                            Log.d(
                                                                "Application",
                                                                "getAppliedStudentDetail() -> $userData"
                                                            )
                                                            if (userData.isNotEmpty()) {
                                                                val currentList = appliedStudentData.value ?.toMutableList() ?: mutableListOf()
                                                                currentList.add(
                                                                    AppliedStudentData(
                                                                        userData = userData[0],
                                                                        applicationData = application,
                                                                        studentsData = student
                                                                    )
                                                                )
                                                                appliedStudentData.value = currentList
                                                            }
                                                        }
                                                    }
                                            }
                                            Log.d(
                                                "Application",
                                                "getAppliedStudentDetail(): All -> ${appliedStudentData.value}"
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            appliedStudentData.value = emptyList()
        }
        return appliedStudentData
    }

    override suspend fun moveToNextProcess(applicationId: String, processId: Int): SelectState {
        Log.d("LogData","moveToNextProcess(): applicationId -> $applicationId")
        return try{
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).document(applicationId)
                .update("processId", processId+1).await()
            SelectState.Success("Success")
        }catch (e: Exception){
            SelectState.Error(e.localizedMessage ?: "Check failed")
        }
    }
}