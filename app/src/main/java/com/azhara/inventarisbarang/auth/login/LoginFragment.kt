package com.azhara.inventarisbarang.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.azhara.inventarisbarang.R
import com.azhara.inventarisbarang.auth.viewmodel.AuthViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AuthViewModel::class.java]
        btn_login.setOnClickListener(this)
        btn_to_register.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)
    }

    private fun login(email: String, password: String){
        authViewModel.login(email, password)

        authViewModel.loginState().observe(viewLifecycleOwner, Observer { loginState ->
            loading(false)
            if (loginState == true){
                context?.let { Toasty.success(it, "Success Login", Toasty.LENGTH_LONG, true).show() }
            }else{
                context?.let { Toasty.error(it, "${authViewModel.loginMessage}", Toast.LENGTH_LONG, true).show() }
            }
        })
    }

    private fun checkInput(){
        loading(true)
        val email = edt_email_login.text.toString().trim()
        val password = edt_password_login.text.toString().trim()

        if (email.isEmpty()){
            loading(false)
            input_layout_email_login.error = "Email tidak boleh kosong!"
            return
        }

        if (password.isEmpty()){
            loading(false)
            input_layout_password_login.error = "Password tidak boleh kosong!"
            return
        }

        if (email.isNotEmpty() && password.isNotEmpty()){
            login(email, password)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_login -> {
                input_layout_email_login.error = null
                input_layout_password_login.error = null
                checkInput()
            }
            R.id.btn_to_register -> {
                view?.findNavController()?.navigate(R.id.action_navigation_login_fragment_to_navigation_register_fragment)
            }
            R.id.tv_forgot_password -> {
                view?.findNavController()?.navigate(R.id.action_navigation_login_fragment_to_navigation_reset_fragment)
            }
        }
    }

    private fun loading(state: Boolean){
        if (state){
            loading_login.visibility = View.VISIBLE
        }else{
            loading_login.visibility = View.INVISIBLE
        }
    }

}