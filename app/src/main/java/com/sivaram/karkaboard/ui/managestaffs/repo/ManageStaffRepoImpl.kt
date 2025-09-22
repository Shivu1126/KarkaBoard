package com.sivaram.karkaboard.ui.managestaffs.repo

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ManageStaffRepoImpl: ManageStaffRepo {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore = FirebaseFirestore.getInstance()

    override suspend fun createStaff(
        userData: UserData,
        personalEmail: String,
        rolesData: RolesData,
        password: String,
        context: Context
    ): AddStaffState {
        return try{
            Log.d("phoneBook", "currentUser: ${auth.currentUser?.email} ")
            val jsonObj  = addStaff(userData.email, password, context)
            Log.d("phoneBook", "json: $jsonObj")
            Log.d("phoneBook", "currentUser: ${auth.currentUser?.email} ")
            if (jsonObj.has("error")) {
                val errorObj = jsonObj.getJSONObject("error")
                val errorMessage = errorObj.getString("message")
                AddStaffState.Error("Oops! $errorMessage")
            } else {
                // Success case
                val localId = jsonObj.getString("localId")

                userData.uId = localId
                val staffData = StaffData(
                    name = userData.name,
                    uId = localId,
                    email = personalEmail,
                    companyMail = userData.email,
                    roleId = rolesData.docId,
                    isDisable = false
                )
                if(addToDb(userData, staffData))
                    AddStaffState.Success("Staff Created Successfully")
                else
                    AddStaffState.Error("Something went wrong")
            }
        }
        catch (e: Exception){
            AddStaffState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun checkStaffExist(
        email: String,
        rolesData: RolesData,
        onResult: (String, Boolean) -> Unit
    ) {
        return try{
            val emailDocs = firebaseFireStore.collection(DbConstants.STAFF_TABLE)
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!emailDocs.isEmpty) {
                onResult("Staff found", false)
            } else {
                val newEmail = email.split("@")[0]+rolesData.content
                Log.d("phoneBook", "newEmail: $newEmail")
                val staffDocs = firebaseFireStore.collection(DbConstants.STAFF_TABLE)
                    .whereEqualTo("companyMail", newEmail)
                    .get()
                    .await()
                if(staffDocs.isEmpty){
                    onResult(newEmail,true)
                }
                else{
                    onResult("Staff found", false)
                }
            }
        }
        catch (e: Exception){
            onResult(e.localizedMessage ?: "Check failed", false)
        }
    }

    override suspend fun getAllStaff(): LiveData<List<StaffData>> {
        val allStaffData = MutableLiveData<List<StaffData>>(emptyList())
        try{
            firebaseFireStore.collection(DbConstants.STAFF_TABLE)
                .addSnapshotListener {
                        snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val staffList =
                            snapshot.documents.mapNotNull { it.toObject(StaffData::class.java) }
                        val tempList = staffList.filter { !it.isDisable && it.uId!=auth.uid }
                        Log.d("LogData", "AllStaffData -> $tempList")
                        allStaffData.value = tempList
                    }
                }
        }
        catch (e: Exception){
            allStaffData.value = emptyList()
        }
        return allStaffData
    }

    override suspend fun getStaffData(uid: String): LiveData<StaffData?> {
        val staffData = MutableLiveData<StaffData?>()
        try{
            firebaseFireStore.collection(DbConstants.STAFF_TABLE).whereEqualTo("uid", uid)
                .addSnapshotListener {
                        snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList = snapshot.documents.mapNotNull { it.toObject(StaffData::class.java) }
                        Log.d("LogData", "Data -> $tempList")
                        staffData.value = tempList[0]
                    }
                }
        }
        catch (e: Exception){
            staffData.value = null
        }
        Log.d("LogData", "StaffData -> ${staffData.value}")
        return staffData
    }

    override suspend fun getStaffRole(roleId: String?): LiveData<String> {
        val staffRole = MutableLiveData("")
        try{
            Log.d("LogData", "RoleId -> $roleId")
            firebaseFireStore.collection(DbConstants.ROLES_TABLE).whereEqualTo("docId", roleId)
                .addSnapshotListener {
                        snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tempList =
                            snapshot.documents.mapNotNull { it.toObject(RolesData::class.java) }
                        Log.d("LogData", "Data -> $tempList")
                        staffRole.value = tempList[0].role
                    }
                }
        }
        catch (e: Exception){
            staffRole.value = null
        }
        return staffRole
    }

    override suspend fun removeStaff(uid: String): RemoveStaffState {
        return try {
            val staffDocs =
                firebaseFireStore.collection(DbConstants.STAFF_TABLE).whereEqualTo("uid", uid)
                    .get().await()
            if (!staffDocs.isEmpty) {
                firebaseFireStore.collection(DbConstants.STAFF_TABLE)
                    .document(staffDocs.documents[0].id)
                    .update("disable", true).await()
            }
            val userDocs =
                firebaseFireStore.collection(DbConstants.USER_TABLE).whereEqualTo("uid", uid)
                    .get().await()
            if (!userDocs.isEmpty) {
                firebaseFireStore.collection(DbConstants.USER_TABLE)
                    .document(userDocs.documents[0].id)
                    .update("disable", true).await()
            }
            RemoveStaffState.Success("Staff Removed Successfully")
        }
        catch (e: Exception){
            RemoveStaffState.Error("Something went wrong: ${e.message}")
        }
    }

    private suspend fun addStaff(
        email: String,
        password: String,
        context: Context
    ): JSONObject = withContext(Dispatchers.IO) {
        val apiKey = context.getString(R.string.google_api_key)
        val url = OtherConstants.CREATE_STAFF_URL+apiKey
        val payload = JSONObject().apply{
            put("email", email)
            put("password", password)
            put("returnSecureToken", false)
        }.toString()

        val body = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val req = Request.Builder().url(url).post(body).build()
        OkHttpClient().newCall(req).execute().use {
            response ->
            val respText = response.body?.string().orEmpty()
            Log.d("httpCall", "addStaff: $respText")
            JSONObject(respText)
        }
    }

    private suspend fun addToDb(userData: UserData, staffData: StaffData): Boolean{
        return try {
            val userDoc = firebaseFireStore.collection(DbConstants.USER_TABLE).add(userData).await()
            val staffDoc = firebaseFireStore.collection(DbConstants.STAFF_TABLE).add(staffData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}