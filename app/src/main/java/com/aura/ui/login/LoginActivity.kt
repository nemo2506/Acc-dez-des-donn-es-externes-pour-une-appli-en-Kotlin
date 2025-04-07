package com.aura.ui.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.aura.R
import com.aura.domain.model.BalanceReportModel
import com.aura.domain.model.LoginReportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


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

            if (getBalance()) {
                binding.loading.visibility = View.VISIBLE
                homeLoader()
            }

        } else {
            binding.login.isEnabled = true
            toastMessage(connected.message)
        }
    }

    private fun homeLoader() {
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun isConnected(id: String, password: String): LoginReportModel {
        if (!isServerAsset()) return LoginReportModel(false, getString(R.string.network_error))
        return viewModel.getAuraLogin(id, password)
    }

    private suspend fun getBalance(): Boolean {
        val report: BalanceReportModel = viewModel.getAuraBalance()
        if (report.balance == null) {
            report.message?.let { toastMessage(it) }
            binding.login.text = getString(R.string.try_again)
            binding.login.isEnabled = true
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isInternetAccess(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private suspend fun isServerAccess(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val apiURL = context.getString(R.string.url_api)
                val url = URL(apiURL)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                val responseCode = connection.responseCode
                // Checking for HTTP 404 response
                responseCode == 404
            } catch (e: IOException) {
                false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun isServerAsset(): Boolean {
        val internetReady = isInternetAccess(this)
        val serverReady = isServerAccess(this)
        if (!internetReady) internetFailedMessage()
        if (!serverReady) serverFailedMessage()
        return internetReady && serverReady
    }

    private fun internetFailedMessage() {
        toastMessage(getString(R.string.internet_required))
    }

    private fun serverFailedMessage() {
        toastMessage(getString(R.string.server_required))
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
