package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.activity.viewModels
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val identifier = binding.identifier
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        /**
         * Identifier and Password is verified to ensure the data is not empty
         */
        identifier.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        /**
         * When button Login is clicked viewModel get authentication
         */
        login.setOnClickListener {
            viewModel.getAuraLogin(identifier.text.toString(), password.text.toString())
        }

        /**
         * Scope to viewModel to interactivity
         */
        lifecycleScope.launch {
            viewModel.uiState.collect {

                /**
                 * Interface Loader visibility and Button enabled depends to scope interactivity
                 */
                loading.isVisible = it.isViewLoading == true
                login.isEnabled = it.logged == false || it.isUserDataReady == true

                /**
                 * At step logged HomeActivity is loading
                 */
                if (it.logged == true) {
                    homeLoader(identifier)
                    toastMessage(getString(R.string.login_success))
                }

                /**
                 * Not logged an error message and reset stateflow
                 */
                if (it.logged == false) {
                    viewModel.reset()
                    toastMessage(getString(R.string.login_failed))
                }

                /**
                 * Error message generic
                 */
                if (it.errorMessage?.isNotBlank() == true) toastMessage(it.errorMessage)
            }
        }
    }

    /**
     * Methode to load HomeActivity and pass User Id to next Activity
     */
    private fun homeLoader(currentId: EditText) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("currentId", currentId.text.toString())
        }
        startActivity(intent)
        finish()
    }

    /**
     * Methode to link viewModel to pass control by stateflow
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.loginManage(
                binding.identifier.text.isNotEmpty(),
                binding.password.text.isNotEmpty()
            )
        }
    }

    /**
     * Simplify methode to screen message
     */
    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
