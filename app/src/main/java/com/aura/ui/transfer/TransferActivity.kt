package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

        currentId = intent.getStringExtra("currentId").toString()

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
                    currentId,
                    recipient.text.toString(),
                    amount.text.toString().toDouble()
                )
                viewModel.getAuraBalance(currentId)

                viewModel.uiState.collect {
                    loading.isVisible = it.isViewLoading
                    transfer.isEnabled = !it.isViewLoading

                    if (it.transferred == true)
                        toastMessage(getString(R.string.transfer_success))

                    if (it.transferred == false)
                        toastMessage(getString(R.string.transfer_failed))

                    if (it.balanceReady == true) {
                        homeLoader()
                        toastMessage(getString(R.string.balance_success))
                    }

                    if (it.balanceReady == false)
                        toastMessage(getString(R.string.balance_failed))

                    if (it.errorMessage?.isNotBlank() == true)
                        toastMessage(it.errorMessage)
                }
            }
        }

    }

    private fun homeLoader() {
        startActivity(Intent(this, HomeActivity::class.java)
            .apply { putExtra("currentId", currentId) })
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
