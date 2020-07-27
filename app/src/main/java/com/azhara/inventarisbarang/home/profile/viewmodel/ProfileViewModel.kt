package com.azhara.inventarisbarang.home.profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.azhara.inventarisbarang.entity.User

class ProfileViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid
    private val tag = ProfileViewModel::class.java.simpleName
    private val dataUser = MutableLiveData<User>()

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

}