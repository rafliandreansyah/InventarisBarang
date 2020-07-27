package com.azhara.inventarisbarang.home.profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.azhara.inventarisbarang.entity.User
import com.google.firebase.auth.EmailAuthProvider

class ProfileViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val user = auth.currentUser
    private val userId = user?.uid
    private val tag = ProfileViewModel::class.java.simpleName
    private val dataUser = MutableLiveData<User>()
    var changePasswordMsgError: String? = null
    private val changePasswordState = MutableLiveData<Boolean?>()

    fun loadDataUser(){
        val userDb = db.collection("users").document("$userId")
        userDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error load data user: ${error.message}")
            }
            if (value != null && value.exists()){
                val data = value.toObject(User::class.java)
                dataUser.postValue(data)
            }
        }
    }

    fun dataUser(): LiveData<User> = dataUser

    fun changePassword(oldPassword: String, newPassword: String) {
        changePasswordMsgError = null
        val credential = user?.email?.let { EmailAuthProvider.getCredential(it, oldPassword) }
        if (credential != null) {
            user?.reauthenticate(credential)?.addOnSuccessListener {
                Log.d(tag, "email: ${user.email} Success authenticated")
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "email:${user.email} Success Update password")
                        changePasswordState.postValue(true)
                    } else {
                        Log.e(
                            tag,
                            "email: ${user.email}, changePassword, exception: ${task.exception?.message}"
                        )
                        changePasswordMsgError = task.exception?.message
                        changePasswordState.postValue(false)
                    }
                }

            }?.addOnFailureListener { e ->
                Log.e(tag, "email: ${user.email}, changePassword, exception: ${e.message}")
                if (e.message == "The password is invalid or the user does not have a password.") {
                    changePasswordMsgError = "Password lama salah"
                    changePasswordState.postValue(false)
                } else if (e.message == "A network error (such as timeout, interrupted connection or unreachable host) has occurred.") {
                    changePasswordMsgError = "Kesalahan jaringan, silahkan cek jaringan anda!"
                    changePasswordState.postValue(false)
                } else {
                    changePasswordMsgError = e.message
                    changePasswordState.postValue(false)
                }
            }
        }
    }

    fun changePasswordState(): LiveData<Boolean?> = changePasswordState

}