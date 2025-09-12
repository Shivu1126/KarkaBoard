package com.sivaram.karkaboard.ui.managestaffs.repo

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.R
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
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
    private val firestore = FirebaseFirestore.getInstance()

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
                    roleId = rolesData.docId
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
            val emailDocs = firestore.collection(DbConstants.STAFF_TABLE)
                .whereEqualTo("email", email)
                .get()
                .await()
            if (!emailDocs.isEmpty) {
                onResult("Staff found", false)
            } else {
                val newEmail = email.split("@")[0]+rolesData.content
                Log.d("phoneBook", "newEmail: $newEmail")
                val staffDocs = firestore.collection(DbConstants.STAFF_TABLE)
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
            val userDoc = firestore.collection(DbConstants.USER_TABLE).add(userData).await()
            val staffDoc = firestore.collection(DbConstants.STAFF_TABLE).add(staffData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}