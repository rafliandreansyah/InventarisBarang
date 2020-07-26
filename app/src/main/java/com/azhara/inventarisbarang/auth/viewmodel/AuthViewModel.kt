package com.azhara.inventarisbarang.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val loginState = MutableLiveData<Boolean>()
    private val tag = AuthViewModel::class.java.simpleName
    var loginMessage: String? = null

    fun login(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                loginState.postValue(true)
            }else{
                Log.e(tag, "Error login: ${task.exception}")
                loginMessage = "${task.exception?.message}"
                loginState.postValue(false)
            }
        }
    }

    fun loginState(): LiveData<Boolean> = loginState
}

