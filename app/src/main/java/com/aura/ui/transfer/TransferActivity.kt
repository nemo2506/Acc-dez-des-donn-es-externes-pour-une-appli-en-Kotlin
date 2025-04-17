package com.aura.ui.transfer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferActivityViewModel by viewModels()
    private lateinit var currentId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * User Id from preview Screen
         */
        currentId = intent.getStringExtra("currentId").toString()

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        /**
         * Recipient and Amount is verified to ensure the data is not empty
         */
        recipient.addTextChangedListener(textWatcher)
        amount.addTextChangedListener(textWatcher)

        /**
         * When button transfer is clicked viewModel get transfer
         */
        transfer.setOnClickListener {
            viewModel.getAuraTransfer(
                currentId,
                recipient.text.toString(),
                amount.text.toString().toDouble()
            )
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
                transfer.isEnabled = it.transferred == true || it.isUserDataReady == true

                /**
                 * At step logged HomeActivity is loading
                 */
                if (it.transferred == true) {
                    homeLoader()
                    toastMessage(getString(R.string.transfer_success))
                }

                /**
                 * Not transferred an error message and reset stateflow
                 */
                if (it.transferred == false) {
                    viewModel.reset()
                    toastMessage(getString(R.string.transfer_failed))
                }

                /**
                 * Error message generic
                 */
                if (it.errorMessage?.isNotBlank() == true)
                    toastMessage(it.errorMessage)
            }
        }

    }

    /**
     * Methode to load HomeActivity and pass User Id to next Activity
     */
    private fun homeLoader() {
        startActivity(Intent(this, HomeActivity::class.java)
            .apply { putExtra("currentId", currentId) })
        finish()
    }

    /**
     * Methode to link viewModel to pass control by stateflow
     */
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            viewModel.transferManage(
                binding.recipient.text.isNotEmpty(),
                binding.amount.text.isNotEmpty()
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
