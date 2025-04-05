package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginActivityViewModel by viewModels()

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connexionUI()

        val login = binding.login
        val loading = binding.loading

        login.setOnClickListener {
            lifecycleScope.launch {
                val isConnected = isConnected(binding.identifier.toString(), binding.password.toString())
                if (isConnected) {
                    loading.visibility = View.VISIBLE
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun connexionUI() {
        binding.identifier.doAfterTextChanged { isLoginReady() }
        binding.password.doAfterTextChanged { isLoginReady() }
    }

    private fun isLoginReady(): Unit {
        val isIdentifierReady: Boolean = binding.identifier.text.toString().isNotEmpty()
        val isPasswordReady: Boolean = binding.password.text.toString().isNotEmpty()
        binding.login.isEnabled = isIdentifierReady && isPasswordReady

    }

    private fun isConnected(id: String, password: String): Boolean {
        val response = viewModel.getAuraLogin(id, password)
        return response.isSuccessful && response.body() != null
    }
}
