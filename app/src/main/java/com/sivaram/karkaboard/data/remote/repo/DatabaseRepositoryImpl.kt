package com.sivaram.karkaboard.data.remote.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState
import kotlinx.coroutines.tasks.await

class DatabaseRepositoryImpl: DatabaseRepository  {
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun getRoles(): LiveData<List<RolesData>> {
        val rolesData = MutableLiveData<List<RolesData>>(emptyList())
        firebaseFireStore.collection(DbConstants.ROLES_TABLE)
            .orderBy("content", Query.Direction.DESCENDING)
            .addSnapshotListener {
                snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val tempList = snapshot.documents.mapNotNull { it.toObject(RolesData::class.java) }
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
            firebaseFireStore.collection(DbConstants.USER_TABLE).whereEqualTo("uid", currentUser?.uid)
                .addSnapshotListener {
                    snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList = snapshot.documents.mapNotNull { it.toObject(UserData::class.java) }
                        Log.d("LogData", "Data -> $tempList")
                        if(tempList.isNotEmpty())
                            userData.value = tempList[0]
                    }
                }
            Log.d("LogData", "UserData -> ${userData.value}")
        }
        catch (e: Exception) {
            userData.value = null
        }
        Log.d("LogData", "UserData -> ${userData.value}")
        return userData
    }
}