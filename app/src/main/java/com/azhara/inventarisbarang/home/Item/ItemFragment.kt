package com.azhara.inventarisbarang.home.Item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.entity.Product
import com.azhara.inventarisbarang.home.Item.viewmodel.ItemViewModel
import com.azhara.inventarisbarang.home.product.adapter.ProductAdapter
import kotlinx.android.synthetic.main.fragment_item.*

class ItemFragment : Fragment() {

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ItemViewModel::class.java]
        productAdapter = ProductAdapter()
        loadData()
    }

    private fun loadData(){
        loading(true)
        itemViewModel.getDataProduct()

        itemViewModel.productData().observe(viewLifecycleOwner, Observer { data ->
            if (data.isNotEmpty()){
                loading(false)
                emptyState(false)
                setData(data)
            }else{
                loading(false)
                emptyState(true)
            }
        })

    }

    private fun setData(data: List<Product>) {
        with(rv_item){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
        productAdapter.submitList(data)
    }

    private fun loading(state: Boolean){
        if (state){
            loading_product_item.visibility = View.VISIBLE
        }else{
            loading_product_item.visibility = View.INVISIBLE
        }
    }

    private fun emptyState(state: Boolean){
        if(state){
            layout_empty_product_item.visibility = View.VISIBLE
        }else{
            layout_empty_product_item.visibility = View.INVISIBLE
        }
    }

}