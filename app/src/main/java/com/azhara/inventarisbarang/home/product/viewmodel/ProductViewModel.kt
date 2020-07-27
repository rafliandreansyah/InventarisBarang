package com.azhara.inventarisbarang.home.product.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductViewModel: ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val tag = ProductViewModel::class.java.simpleName

    private val productData = MutableLiveData<List<Product>>()

    fun getDataProduct(){
        val productDb = db.collection("product")

        productDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error load data product: ${error.message}")
            }

            if (value != null){
                val data = value.toObjects(Product::class.java)
                productData.postValue(data)
            }
        }
    }

    fun productData() : LiveData<List<Product>> = productData
}