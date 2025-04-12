package com.aura.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.domain.model.BalanceReportModel
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            binding.login.isEnabled = false
            lifecycleScope.launch {
                val id = binding.identifier.text.toString()
                val password = binding.password.text.toString()
                viewModel.getAuraLogin(id,password)
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collect {
                        binding.loading.isVisible = it.isViewLoading
                        if(it.logged == true){
                            viewModel.getAuraBalance()
                            homeLoader()
                        }

                        if (it.errorMessage?.isNotBlank() == true) {
                            TODO()
                            toastMessage(it.errorMessage)
                        }
                    }
                }
//                manageIntentUI(isConnected)
            }
        }
    }


//    private suspend fun manageIntentUI(connected: LoginReportModel) {
//        if (connected.granted) {
//
//            if (getBalance()) {
//                binding.loading.visibility = View.VISIBLE
//                homeLoader()
//            }
//
//        } else {
//            binding.login.isEnabled = true
//            toastMessage(connected.message)
//        }
//    }

    private fun homeLoader() {
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    }

//    private suspend fun getBalance(): Boolean {
//        val report: BalanceReportModel = viewModel.getAuraBalance()
//        if (report.balance == null) {
//            report.message?.let { toastMessage(it) }
//            binding.login.text = getString(R.string.try_again)
//            binding.login.isEnabled = true
//            return false
//        }
//        return true
//    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
