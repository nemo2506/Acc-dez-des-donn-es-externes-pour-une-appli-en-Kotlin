package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityTransferBinding
import com.aura.domain.model.TransferReportModel
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
            loaderShow(loading)
            lifecycleScope.launch {
                if (transferManage(recipient, amount)) {
                    homeLoader(amount)
                } else {
                    loaderHide(loading)
                    transferShow(transfer)
                    transferFailedMessage()
                }
            }
        }

    }

    private fun transferShow(transfer: Button) {
        transfer.isEnabled = true
    }

    private fun transferHide(transfer: Button) {
        transfer.isEnabled = false
    }

    private fun loaderShow(loading: ProgressBar) {
        loading.visibility = View.VISIBLE
    }

    private fun loaderHide(loading: ProgressBar) {
        loading.visibility = View.GONE
    }

    private fun homeLoader(amount: EditText) {
        val subtract: (Double, Double) -> Double = { a, b -> a - b }
        val resultIntent = Intent()
        val newBalance = viewModel.balance?.let { subtract(it, amount.text.toString().toDouble()) }
        resultIntent.putExtra("newBalance", newBalance)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private suspend fun transferManage(recipient: EditText, amount: EditText): Boolean {
        val report =
            viewModel.getAuraTransfer(recipient.text.toString(), amount.text.toString().toDouble())
        if (report.done == true) {
            viewModel.getAuraBalance()
            return true
        }
        report.message?.let { toastMessage(it) }
        return false
    }

    private fun dataUserUi(recipient: EditText, amount: EditText, transfer: Button) {
        transfer.isEnabled = false
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isRecipientNotEmpty = recipient.text?.isNotEmpty() == true
                val isAmountNotEmpty = amount.text?.isNotEmpty() == true
                transfer.isEnabled = isRecipientNotEmpty && isAmountNotEmpty
            }
        }

        recipient.addTextChangedListener(textWatcher)
        amount.addTextChangedListener(textWatcher)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun transferFailedMessage() {
        toastMessage(getString(R.string.amount_required))
    }
}
