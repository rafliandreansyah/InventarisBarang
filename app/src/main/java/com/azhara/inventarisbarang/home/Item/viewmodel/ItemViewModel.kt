package com.azhara.inventarisbarang.home.Item.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.Product
import com.google.firebase.firestore.FirebaseFirestore

class ItemViewModel : ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val tag = ItemViewModel::class.java.simpleName
    private val productData = MutableLiveData<List<Product>>()

    fun getDataProduct(){
        val productDb = db.collection("product")

        productDb.addSnapshotListener { value, error ->
            if (error != null){
                Log.e(tag, "Error load data product: ${error.message}")
            }

            if (value != null){
                val listProduct = ArrayList<Product>()
                val data = value.documents
                data.forEach {
                    val product = it.toObject(Product::class.java)
                    if (product != null){
                        product.productId = it.id
                        listProduct.add(product)
                    }
                }
                productData.postValue(listProduct)
                Log.d("product", "$listProduct")
            }
        }
    }

    fun productData() : LiveData<List<Product>> = productData
}

