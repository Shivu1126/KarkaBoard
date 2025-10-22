package com.sivaram.karkaboard.data.remote.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.InterviewHistoryData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StudentData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.AcceptState
import com.sivaram.karkaboard.ui.interviewmanagement.state.ApplicationState
import com.sivaram.karkaboard.ui.interviewmanagement.state.DeclineState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    override suspend fun getStudentData(uid: String): LiveData<StudentData?> {
        val stuData = MutableLiveData<StudentData?>()
        Log.d("LogData", "getStudentData() -> $uid")
        try {
            firebaseFireStore.collection(DbConstants.STUDENT_TABLE).whereEqualTo("uid", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList =
                            snapshot.documents.mapNotNull { it.toObject(StudentData::class.java) }
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

    override fun getAppliedStudentDetail(
        batchId: String,
        filterId: Int
    ): LiveData<List<AppliedStudentData>> {
        var appliedStudentData = MutableLiveData<List<AppliedStudentData>>()
        Log.d("LogData", "getAppliedStudentDetail(): batchId-> $batchId filterId -> $filterId")

        firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
            .whereEqualTo("batchId", batchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    appliedStudentData.value = emptyList()
                    return@addSnapshotListener
                }
                if (snapshot == null || snapshot.isEmpty) {
                    appliedStudentData.value = emptyList()
                    return@addSnapshotListener
                }
                val applicationDataList =
                    snapshot.documents.mapNotNull { it.toObject(ApplicationData::class.java) }

                val filteredApplications = when (filterId) {
                    0 -> applicationDataList  // All
                    1 -> applicationDataList.filter { it.processId == 1 }  // In Review
                    2 -> applicationDataList.filter { it.processId == 2 }  // In Interview
                    3 -> applicationDataList.filter { it.processId == 3 }  // Selected
                    4 -> applicationDataList.filter { it.processId == 6 || it.processId == 7 }  // Rejected
                    else -> applicationDataList
                }
                CoroutineScope(Dispatchers.IO).launch {
                    // This code runs on a BACKGROUND THREAD optimized for I/O operations
                    // (like network requests, database queries, file operations)
                    val resultList = mutableListOf<AppliedStudentData>()
                    for (application in filteredApplications) {
                        try {
                            val studentSnapshot =
                                firebaseFireStore.collection(DbConstants.STUDENT_TABLE)
                                    .whereEqualTo("uid", application.studentId)
                                    .get()
                                    .await()

                            val studentData = studentSnapshot.documents.firstOrNull()
                                ?.toObject(StudentData::class.java)

                            if (studentData != null) {
                                // Fetch user data (one-time, not snapshot)
                                val userSnapshot =
                                    firebaseFireStore.collection(DbConstants.USER_TABLE)
                                        .whereEqualTo("uid", studentData.uid)
                                        .get()
                                        .await()
                                val userData = userSnapshot.documents.firstOrNull()
                                    ?.toObject(UserData::class.java)
                                if (userData != null) {
                                    resultList.add(
                                        AppliedStudentData(
                                            userData = userData,
                                            applicationData = application,
                                            studentData = studentData
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            appliedStudentData.value = emptyList()
                        }
                    }
                    // Update LiveData on main thread
                    withContext(Dispatchers.Main) {
                        appliedStudentData.value = resultList
                    }
//                        Dispatchers.IO = Background thread for network/database operations
//                        Dispatchers.Main = UI thread for updating LiveData/UI
//                        CoroutineScope.launch = Start a new coroutine
//                        withContext = Switch to a different thread temporarily
//                        await() = Suspend function that waits without blocking the thread
                }
            }
        return appliedStudentData
    }

    override suspend fun shortlistForInterview(applicationId: String, processId: Int): AcceptState {
        Log.d("LogData", "moveToNextProcess(): applicationId -> $applicationId")
        return try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).document(applicationId)
                .update("processId", processId + 1).await()
            AcceptState.Success("Success")
        } catch (e: Exception) {
            AcceptState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun declineForInterview(applicationData: ApplicationData): DeclineState {
        Log.d("LogData", "declineForInterview(): applicationId -> ${applicationData.docId}")
        return try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).document(applicationData.docId)
                .update(
                    mapOf(
                        "processId" to applicationData.processId + 5,
                        "feedback" to applicationData.feedback,
                        "performanceRating" to applicationData.performanceRating
                    )
                ).await()
            val batchObj = firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(applicationData.batchId)
                .get().await().toObject(BatchData::class.java)
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(applicationData.batchId).update(
                "rejectedCount", batchObj?.rejectedCount?.plus(1)
            ).await()
            DeclineState.Success("Success")
        } catch (e: Exception) {
            DeclineState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun selectedForTraining(
        applicationData: ApplicationData,
        studentDocId: String
    ): ApplicationState {
        return try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
                .document(applicationData.docId)
                .update(
                    mapOf(
                        "processId" to applicationData.processId + 1,
                        "feedback" to applicationData.feedback,
                        "performanceRating" to applicationData.performanceRating
                    )
                ).await()
            val batchObj = firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                .document(applicationData.batchId)
                .get().await().toObject(BatchData::class.java)
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                .document(applicationData.batchId).update(
                    "selectedCount", batchObj?.selectedCount?.plus(1)
                ).await()
            firebaseFireStore.collection(DbConstants.STUDENT_TABLE).document(studentDocId).update(
                "selected", true
            ).await()
            ApplicationState.Success("Success", true)
        } catch (e: Exception) {
            ApplicationState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun rejectedFromInterview(
        applicationData: ApplicationData
    ): ApplicationState {
        Log.d("LogData", "rejectedFromInterview(): applicationId -> ${applicationData.docId}")
        return try {
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
                .document(applicationData.docId)
                .update(
                    mapOf(
                        "processId" to applicationData.processId + 5,
                        "feedback" to applicationData.feedback,
                        "performanceRating" to applicationData.performanceRating
                    )
                ).await()
            val batchObj = firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                .document(applicationData.batchId)
                .get().await().toObject(BatchData::class.java)
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE)
                .document(applicationData.batchId).update(
                    "rejectedCount", batchObj?.rejectedCount?.plus(1)
                ).await()
            ApplicationState.Success("Success", false)
        } catch (e: Exception) {
            ApplicationState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun getInterviewHistory(studentId: String): LiveData<List<InterviewHistoryData>> {
        Log.d("LogData", "studentId -> $studentId")
        val interviewHistory = MutableLiveData<List<InterviewHistoryData>>()
        try{
            firebaseFireStore.collection("applications")
                .whereEqualTo("studentId", studentId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot==null) {
                        return@addSnapshotListener
                    }
                    Log.d("LogData", "getInterviewHistory() -> ${snapshot.documents}")
                    val result = mutableListOf<InterviewHistoryData>()
                    snapshot.documents.forEach { appDoc ->
                        val appObj = appDoc.toObject(ApplicationData::class.java)
                        appObj?.let { applicationData ->
                            firebaseFireStore.collection("batches")
                                .document(applicationData.batchId).get()
                                .addOnSuccessListener { batchDoc ->
                                    val batchObj = batchDoc.toObject(BatchData::class.java)
                                    Log.d("LogData", "getInterviewHistory() AppObj -> $appObj")
                                    Log.d("LogData", "getInterviewHistory() BatchObj -> $batchObj")
                                    if (batchObj != null) {
                                        result.add(
                                            InterviewHistoryData(
                                                applicationData = appObj,
                                                batchData = batchObj
                                            )
                                        )
                                        interviewHistory.value = result
                                    }
                                }
                        }
                    }
                }
        }
        catch (e: Exception){
            interviewHistory.value = emptyList()
        }
        Log.d("LogData", "getInterviewHistory() -> ${interviewHistory.value}")
        return interviewHistory
    }
}