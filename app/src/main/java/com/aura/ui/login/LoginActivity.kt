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
 * Activity responsible for handling user login.
 *
 * Validates user input, communicates with [LoginActivityViewModel] to handle authentication,
 * and navigates to [HomeActivity] on successful login.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    /**
     * User login activity.
     *
     * This activity allows the user to log in and, upon successful authentication,
     * navigates to [HomeActivity], passing the user's ID via an Intent.
     *
     * @property CURRENT_ID Constant key used to pass the user ID through an Intent.
     */
    companion object {
        const val CURRENT_ID: String = "currentId"
    }

    private val viewModel: LoginActivityViewModel by viewModels()

    /**
     * View binding for the login screen layout.
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
         * Enable login button only when both identifier and password are not empty.
         */
        identifier.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        /**
         * Login button triggers login process through the ViewModel.
         */
        login.setOnClickListener {
            viewModel.getAuraLogin(identifier.text.toString(), password.text.toString())
        }

        /**
         * Observe UI state from the ViewModel and update the UI accordingly.
         */
        lifecycleScope.launch {
            viewModel.uiState.collect {

                // Show or hide the loading indicator
                loading.isVisible = it.isViewLoading == true
                // Enable login button based on loader state or data readiness
                login.isEnabled = it.isViewLoading == false && it.isUserDataReady == true

                // Navigate to HomeActivity if login is successful
                if (it.logged == true) {
                    homeLoader(identifier)
                    toastMessage(getString(R.string.login_success))
                }

                // Show error and reset state on login failure
                if (it.logged == false) {
                    viewModel.reset()
                    toastMessage(getString(R.string.login_failed))
                }

                // Show a generic error message if present
                if (it.errorMessage?.isNotBlank() == true) {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    /**
     * Navigates to [HomeActivity] and passes the logged-in user's ID via Intent.
     *
     * @param currentId The EditText containing the user identifier.
     */
    private fun homeLoader(currentId: EditText) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(CURRENT_ID, currentId.text.toString())
        }
        startActivity(intent)
        finish()
    }

    /**
     * TextWatcher to notify ViewModel when user input changes,
     * used to determine if login button should be enabled.
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.userDataControl(
                binding.identifier.text.isNotEmpty(),
                binding.password.text.isNotEmpty()
            )
        }
    }

    /**
     * Displays a toast message on the screen.
     *
     * @param message The message to be displayed.
     */
    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
