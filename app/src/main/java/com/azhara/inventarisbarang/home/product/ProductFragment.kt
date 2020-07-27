package com.azhara.inventarisbarang.home.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.Product
import com.azhara.inventarisbarang.home.product.adapter.ProductAdapter
import com.azhara.inventarisbarang.home.product.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_product.*

class ProductFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ProductViewModel::class.java]
        getDataProduct()
    }

    private fun getDataProduct(){
        loading(true)
        productViewModel.getDataProduct()

        productViewModel.productData().observe(viewLifecycleOwner, Observer { data ->
            if (data.isNotEmpty()){
                loading(false)
                emptyState(false)
                setDataItem(data)
                Log.d("data", "$data")
            }else{
                loading(false)
                emptyState(true)
            }
        })
    }

    private fun setDataItem(data: List<Product>) {
        productAdapter = ProductAdapter()
        with(rv_product){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
        productAdapter.submitList(data)
    }

    private fun emptyState(state: Boolean){
        if(state){
            layout_empty_product.visibility = View.VISIBLE
        }else{
            layout_empty_product.visibility = View.INVISIBLE
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_product.visibility = View.VISIBLE
        }else{
            loading_product.visibility = View.INVISIBLE
        }
    }

}