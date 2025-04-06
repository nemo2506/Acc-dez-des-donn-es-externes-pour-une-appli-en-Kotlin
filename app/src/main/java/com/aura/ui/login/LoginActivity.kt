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
import com.aura.domain.model.AccountsReportModel
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

    private suspend fun manageIntentUI(connected: LoginReportModel) {
        if (connected.granted) {

            if (getAccount()) {
                binding.loading.visibility = View.VISIBLE
                homeLoader()
            }

        } else {
            binding.login.isEnabled = true
            Toast.makeText(this, connected.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun homeLoader() {
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    }

    private suspend fun isConnected(id: String, password: String): LoginReportModel {
        return viewModel.getAuraLogin(id, password)
    }

    private suspend fun getAccount(): Boolean {
        val report: AccountsReportModel = viewModel.getAuraAccount()
        if (report.balance == null) {
            Toast.makeText(this, report.message, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}
