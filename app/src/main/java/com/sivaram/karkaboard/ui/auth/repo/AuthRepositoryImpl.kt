package com.sivaram.karkaboard.ui.auth.repo

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.appconstants.OtherConstants
import com.sivaram.karkaboard.data.dto.StudentData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.LogoutState
import com.sivaram.karkaboard.ui.auth.state.VerifyState
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl : AuthRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun checkIfUserExists(
        email: String,
        mobileNumber: String,
        onResult: (Boolean, String) -> Unit
    ) {
        return try {
            if (isEmailAlreadyInUse(email)) {
                return onResult(true, "User Already Exist !")
            }

            val mobileDocs = firestore.collection(DbConstants.USER_TABLE)
                .whereEqualTo("mobile", mobileNumber)
                .get()
                .await()

            if (!mobileDocs.isEmpty) {
                onResult(true, "User Already Exist !")
            } else {
                onResult(false, "User not Exist !")
            }
        } catch (e: Exception) {
            onResult(false, e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun getOtp(
        mobileNumber: String,
        context: Context,
        onTimeOut: (AuthFlowState.OtpTimeout) -> Unit
    ): AuthFlowState = suspendCoroutine { continuation ->
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("phoneBook", "verification completed")
                // If auto-retrieval works, we can directly sign in
//                continuation.resume(AuthFlowState.VerificationCompleted(credential))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("phoneBook", "verification failed: ${e.localizedMessage}")
                continuation.resume(
                    AuthFlowState.Error(
                        e.localizedMessage ?: "Verification failed"
                    )
                )
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("phoneBook", "code sent: $verificationId")
                continuation.resume(
                    AuthFlowState.OtpSent(verificationId, token.toString())
                )
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                Log.d("OTP timeout", "onCodeAutoRetrievalTimeOut: $verificationId")
                onTimeOut(AuthFlowState.OtpTimeout("Please Try Again!"))
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(mobileNumber)
                .setTimeout(OtherConstants.TIMER_SECONDS, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(callback)
                .build()
            Log.d("phoneBook", "options: $options")
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.d("phoneBook", "error: ${e.localizedMessage}")
            continuation.resume(AuthFlowState.Error(e.localizedMessage ?: "Check failed"))
        }
    }

    override suspend fun verifyPhoneCredential(
        otp: String,
        verificationId: String,
        mobile: String,
        email: String,
        password: String
    ): VerifyState {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            Log.d("phoneBook", "user: $user")
            if (user != null) {
                return try {
                    auth.currentUser?.linkWithCredential(
                        EmailAuthProvider.getCredential(email, password)
                    )?.await()
                    VerifyState.Success("Verification successful", user.uid)
                } catch (e: Exception) {
                    Log.e("phoneBook", "Linking email failed", e)
                    VerifyState.Error("Verification failed while linking email")
                }
            } else {
                VerifyState.Error("Verification failed")
            }
        } catch (e: Exception) {
            Log.e("phoneBook", "Verification exception", e)
            VerifyState.Error("Verification failed")
        }
    }

    override suspend fun registerStudentDetails(
        studentsData: UserData,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection(DbConstants.USER_TABLE).add(studentsData)
            .addOnSuccessListener {
                Log.d("phoneBook", "DocumentSnapshot added with ID: ${it.id}")
                val stu = StudentData(
                    uid = studentsData.uId
                )
                val stuDocs = firestore.collection(DbConstants.STUDENT_TABLE).document()
                stu.docId = stuDocs.id
                stuDocs.set(stu)
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener {
                        onResult(false)
                    }
            }
            .addOnFailureListener {
                Log.d("phoneBook", "Error adding document", it)
                onResult(false)
            }

    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginState {
        return try{

            val userDocs = firestore.collection(DbConstants.USER_TABLE)
                .whereEqualTo("email", email).get().await()
            if(userDocs.isEmpty){
                return LoginState.Error("Check your MailId and Password")
            }
            else {
                val userData = userDocs.documents[0].toObject(UserData::class.java)
                if(userData?.isDisable == true ){
                    LoginState.Error("Your account is disabled")
                }
                else {
                    val authResult = auth.signInWithEmailAndPassword(email, password).await()
                    val user = authResult.user
                    if (user != null) {
                        LoginState.Success("Login Successful", user.uid)
                    } else {
                        LoginState.Error("Check your MailId and Password")
                    }
                }
            }
        }
        catch (e: Exception){
            LoginState.Error("Check your MailId and Password")
        }
    }

    override suspend fun getMobileNoByMail(
        email: String,
        onResult: (Boolean, String, String) -> Unit
    ){
        return try {
            if(isEmailAlreadyInUse(email)){
                val userDocs = firestore.collection(DbConstants.USER_TABLE)
                    .whereEqualTo("email", email)
                    .get()
                    .await().documents[0]
                val mobileNumber = userDocs.get("mobile").toString()
                val countryCode = userDocs.get("countryCode").toString()
                Log.d("phoneBook", "emailDocs: mobile no: $countryCode $mobileNumber")
                onResult(true, mobileNumber, countryCode)
            } else {
                Log.d("phoneBook", "emailDocs: mobile no null")
                onResult(false, "null", "")
            }
        }
        catch (e: Exception){
            Log.d("phoneBook", e.localizedMessage ?: "Check failed", e)
            onResult(false, "null","")
        }
    }

    override suspend fun resetPassword(
        newPassword: String,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        return try{
            auth.currentUser?.updatePassword(newPassword)?.await()
            onResult(true)
        }
        catch (e: Exception){
            onResult(false)
        }
    }

    override suspend fun signOut(context: Context): LogoutState {

        return try {
            auth.signOut()
            Log.d("phoneBook", "signOut: successfully")
            LogoutState.Success
        }
        catch (e: Exception){
            Log.d("phoneBook", e.localizedMessage ?: "Check failed", e)
            LogoutState.Error("Logout failed")
        }
    }

    override suspend fun getAuth(): FirebaseAuth {
        return auth
    }

    private suspend fun isEmailAlreadyInUse(email: String): Boolean {
        val emailDocs = firestore.collection(DbConstants.USER_TABLE)
            .whereEqualTo("email", email)
            .get()
            .await()

        return !emailDocs.isEmpty
    }
}