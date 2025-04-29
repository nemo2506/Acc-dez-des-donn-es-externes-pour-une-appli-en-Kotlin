package com.aura.ui.transfer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.ConstantsApp
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The activity responsible for handling money transfers in the app.
 * This allows the user to input recipient information and transfer amount.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding

    /**
     * The ViewModel for handling transfer-related logic.
     */
    private val viewModel: TransferActivityViewModel by viewModels()

    /**
     * The current user ID passed from the previous screen.
     */
    private lateinit var currentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using the binding object
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract UI elements from the layout
        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        // Verify the recipient and amount inputs to ensure they are not empty
        recipient.addTextChangedListener(textWatcher)
        amount.addTextChangedListener(textWatcher)

        viewModel.reset()
        // Handle the transfer button click to initiate the transfer process
        transfer.setOnClickListener {
            viewModel.reset()
            // Call ViewModel method to handle transfer logic
            viewModel.getAuraTransfer(
                recipient.text.toString(),
                amount.text.toString().toDouble()
            )
        }

        // Collect UI state updates from the ViewModel and update the UI accordingly
        lifecycleScope.launch {

            viewModel.uiState.collect {

                Log.d("MARC MARC", "onCreate: $it")
                // Show loading indicator and disable transfer button while loading
                loading.isVisible = it.isViewLoading == true
                // Enable transfer button based on loader state or data readiness
                transfer.isEnabled = it.isUserDataReady == true

                // If the transfer is successful, navigate to HomeActivity
                if (it.transferred == true) {
                    homeLoader()
                    toastMessage(getString(R.string.transfer_success))
                }

                // Display message if transfer failed
                if (it.transferred == false)
                    toastMessage(getString(R.string.transfer_failed))

                // Display any error message if error intern
                if (it.errorMessage != null)
                    toastMessage(it.errorMessage)

            }
        }
    }

    /**
     * Navigate to HomeActivity and pass the current user ID to the next activity.
     */
    private fun homeLoader() {
        startActivity(Intent(this, HomeActivity::class.java)
            .apply { putExtra(ConstantsApp.CURRENT_ID, viewModel.currentId) })
        finish()
    }

    /**
     * Watches the recipient and amount fields to enable or disable the transfer button.
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            // Enable or disable the transfer button based on the recipient and amount fields
            viewModel.userDataControl(
                binding.recipient.text.isNotEmpty(),
                binding.amount.text.isNotEmpty()
            )
        }
    }

    /**
     * Simplifies showing toast messages on the screen.
     *
     * @param message The message to be displayed in the toast.
     */
    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
