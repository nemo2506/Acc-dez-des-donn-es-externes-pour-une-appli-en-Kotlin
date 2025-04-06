package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private val subtract: (Double, Double) -> Double = { a, b -> a - b }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transfer = binding.transfer
        val loading = binding.loading
        transfer.setOnClickListener {
            val amount = binding.amount.text?.toString()?.toDoubleOrNull()
            val recipient: String? = binding.recipient.text?.toString()
            loading.visibility = View.VISIBLE
            lifecycleScope.launch {
                if (manageTransferUI(recipient, amount)) {
                    binding.loading.visibility = View.VISIBLE
                    if (amount != null) homeLoader(amount)
                }
            }
        }

    }

    private fun homeLoader(amount: Double) {
        val resultIntent = Intent()
        val newBalance = viewModel.balance?.let { subtract(it, amount) }
        resultIntent.putExtra("newBalance", newBalance)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private suspend fun manageTransferUI(recipient: String?, amount: Double?): Boolean {
        val report: TransferReportModel
        if (isTransferUIReady(recipient, amount)) {
            if (recipient != null && amount != null) {
                report = viewModel.getAuraTransfer(recipient, amount)
                if (report.done == true) {
                    viewModel.getAuraBalance()
                    return true
                }
                Toast.makeText(this, report.message, Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun isTransferUIReady(recipient: String?, amount: Double?): Boolean {
        if (recipient == null) Toast.makeText(this,
            getString(R.string.recipient_required),Toast.LENGTH_SHORT).show()
        if (amount == null) Toast.makeText(this,
            getString(R.string.amount_required), Toast.LENGTH_SHORT).show()
        return recipient != null && amount != null
    }

}
