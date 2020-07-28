package com.azhara.inventarisbarang.home.product.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.azhara.inventarisbarang.entity.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProductViewModel: ViewModel(){

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val tag = ProductViewModel::class.java.simpleName

    private val productData = MutableLiveData<List<Product>>()
    private val productState = MutableLiveData<Boolean>()
    private val editProductState = MutableLiveData<Boolean>()

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

    fun addProductWithOutImage(productName: String?, totalItem: Int?){
        val product = Product(null, productName, totalItem)
        val productDb = db.collection("product")

        productDb.add(product).addOnCompleteListener { task ->
            if (task.isSuccessful){
                productState.postValue(true)
            }else{
                Log.e(tag, "Error add product: ${task.exception?.message}")
                productState.postValue(false)
            }
        }
    }

    private fun addProductDb(productName: String?, totalItem: Int?, imgUrl: String?){
        val product = Product(imgUrl, productName, totalItem)
        val productDb = db.collection("product")

        productDb.add(product).addOnCompleteListener { task ->
            if (task.isSuccessful){
                productState.postValue(true)
            }else{
                productState.postValue(false)
                Log.e(tag, "Error add product to database ${task.exception?.message}")
            }
        }
    }

    fun addProductWithImage(productName: String?, totalItem: Int?, byteArrayImg: ByteArray?){
        val imgStorage = storage.reference.child("product")
            .child("$productName").child("$productName")

        val uploadTask = byteArrayImg?.let { imgStorage.putBytes(it) }
        uploadTask?.addOnSuccessListener { taskSnapshot ->
            (uploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Error upload image ${task.exception?.message}"
                    )
                }
                imgStorage.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlImg = task.result
                    addProductDb(productName, totalItem, urlImg.toString())
                } else {
                    Log.d(tag, "Error upload image ${task.exception?.message}")
                }
            }
        }?.addOnFailureListener {
            Log.e(tag, "Error upload image ${it.message}")
        }
    }

    fun addProductState(): LiveData<Boolean> = productState

    fun editProductWithoutImage(name: String?, totalItem: Int?, productId: String?){
        val productDb = db.collection("product").document("$productId")
        val data = hashMapOf<String?, Any?>(
            "productName" to name,
            "totalItem" to totalItem
        )
        productDb.update(data).addOnCompleteListener { task ->
            if (task.isSuccessful){
                editProductState.postValue(true)
            }else{
                Log.e(tag, "Error edit product: ${task.exception?.message}")
                editProductState.postValue(false)
            }
        }
    }

    private fun editProductDb(name: String?, totalItem: Int?, productId: String?, imgUrl: String?){
        val productDb = db.collection("product").document("$productId")

        val data = hashMapOf<String?, Any?>(
            "productName" to name,
            "totalItem" to totalItem,
            "imgUrl" to imgUrl
        )

        productDb.update(data).addOnCompleteListener { task ->
            if (task.isSuccessful){
                editProductState.postValue(true)
            }else{
                Log.e(tag, "Error edit product: ${task.exception?.message}")
                editProductState.postValue(false)
            }
        }
    }

    fun editProductWithImage(name: String?, totalItem: Int?, productId: String?, byteArrayImg: ByteArray?){
        val imgStorage = storage.reference.child("product")
            .child("$name").child("$name")

        val uploadTask = byteArrayImg?.let { imgStorage.putBytes(it) }
        uploadTask?.addOnSuccessListener { taskSnapshot ->
            (uploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        tag,
                        "Error upload photo edit product: ${task.exception?.message}"
                    )
                }
                imgStorage.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val urlImg = task.result
                    editProductDb(name, totalItem, productId, urlImg.toString())
                } else {
                    Log.d(tag, "Error upload photo edit product: ${task.exception?.message}")
                }
            }
        }
    }

    fun editProductState(): LiveData<Boolean> = editProductState
}