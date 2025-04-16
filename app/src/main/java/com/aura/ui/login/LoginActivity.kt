package com.aura.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
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
import com.aura.R

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
        val identifier = binding.identifier
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        login.isEnabled = false
        loginUiManage(identifier, password, login)

        login.setOnClickListener {
            viewModel.getAuraLogin(identifier.text.toString(), password.text.toString())
        }

        lifecycleScope.launch {
            viewModel.uiState.collect {

                loading.isVisible = it.isViewLoading
                login.isEnabled = it.logged == false

                if (it.logged == true) {
                    homeLoader(identifier)
                    toastMessage(getString(R.string.login_success))
                } else if (it.logged == false)
                    toastMessage(getString(R.string.login_failed))

                if (it.errorMessage?.isNotBlank() == true) toastMessage(it.errorMessage)
            }
        }
    }

    private fun loginUiManage(identifier: EditText, password: EditText, login: Button) {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isIdentifierNotEmpty = identifier.text?.isNotEmpty() == true
                val isPasswordNotEmpty = password.text?.isNotEmpty() == true
                login.isEnabled = isIdentifierNotEmpty && isPasswordNotEmpty
            }
        }
        identifier.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)
    }

    private fun homeLoader(currentId: EditText) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("currentId", currentId.text.toString())
        }
        startActivity(intent)
        finish()
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
