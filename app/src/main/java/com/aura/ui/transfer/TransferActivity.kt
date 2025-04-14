package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityTransferBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        dataUserUi(recipient, amount, transfer)

        binding.transfer.setOnClickListener {
            lifecycleScope.launch {

                viewModel.getAuraTransfer(
                    recipient.text.toString(),
                    amount.text.toString().toDouble()
                )
                viewModel.getAuraBalance()

                viewModel.uiState.collect {
                    loading.isVisible = it.isViewLoading
                    transfer.isEnabled = !it.isViewLoading

                    if (it.transferred == true) {
                        toastMessage(getString(R.string.transfer_success))
                    }

                    if (it.transferred == false) {
                        toastMessage(getString(R.string.transfer_failed))
                    }

                    if (it.balanceReady == true) {
                        it.newBalance?.let { it1 -> homeLoader(it1) }
                        toastMessage(getString(R.string.balance_success))
                    }

                    if (it.balanceReady == false) {
                        toastMessage(getString(R.string.balance_failed))
                    }

                    if (it.errorMessage?.isNotBlank() == true) toastMessage(it.errorMessage)
                }
            }
        }

    }

    private fun homeLoader(balance: Double) {
        val resultIntent = Intent()
        resultIntent.putExtra("newBalance", balance)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun dataUserUi(recipient: EditText, amount: EditText, transfer: Button) {
        transfer.isEnabled = false
        val presence = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isRecipientNotEmpty = recipient.text?.isNotEmpty() == true
                val isAmountNotEmpty = amount.text?.isNotEmpty() == true
                transfer.isEnabled = isRecipientNotEmpty && isAmountNotEmpty
            }
        }

        recipient.addTextChangedListener(presence)
        amount.addTextChangedListener(presence)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
