package com.aura.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import com.aura.domain.model.LoginReportModel

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
    private lateinit var loading: ActivityLoginBinding
    lateinit var login: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            binding.login.isEnabled = false
            lifecycleScope.launch {
                val isConnected = isConnected(
                    binding.identifier.text.toString(),
                    binding.password.text.toString()
                )
                manageIntentUI(isConnected)
            }
        }
    }

    private fun manageIntentUI(connected: LoginReportModel) {
        if (connected.granted) {
            binding.loading.visibility = View.VISIBLE
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            binding.login.isEnabled = true
            Toast.makeText(this, connected.message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun isConnected(id: String, password: String): LoginReportModel {
        val response = viewModel.getAuraLogin(id, password)
        Log.d("##### MARC #####", "isConnected: $response")
        return viewModel.getAuraLogin(id, password)
    }
}
