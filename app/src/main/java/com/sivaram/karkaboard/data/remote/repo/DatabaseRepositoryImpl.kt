package com.sivaram.karkaboard.data.remote.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository

class DatabaseRepositoryImpl: DatabaseRepository  {
    private val firebaseFireStore = FirebaseFirestore.getInstance()

    override fun getRoles(): LiveData<List<RolesData>> {
        val rolesData = MutableLiveData<List<RolesData>>(emptyList())
        firebaseFireStore.collection("roles")
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

}