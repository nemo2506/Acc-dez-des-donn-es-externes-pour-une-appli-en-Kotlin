package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.os.IBinder.DeathRecipient
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityTransferBinding
import com.aura.domain.model.TransferReportModel
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
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

        transfer.setOnClickListener {
            loading.visibility = View.VISIBLE

            setResult(Activity.RESULT_OK)
            finish()
        }
        lifecycleScope.launch {
            if (manageTransferUI()) {
                binding.loading.visibility = View.VISIBLE
            }
        }

    }

    private suspend fun manageTransferUI(): Boolean {
        val report: TransferReportModel
        val recipient: String? = binding.recipient.text?.toString()
        val amount: Double? = binding.amount.text?.toString()?.toDoubleOrNull()
        if(isTransferUIReady(recipient,amount)){
            if(recipient != null && amount != null){
                report = viewModel.getAuraTransfer(recipient, amount)
                if(report.done == true) return true
                Toast.makeText(this, report.message, Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }

    private fun isTransferUIReady(recipient: String?, amount: Double?): Boolean {
        if(recipient == null) Toast.makeText(this, "Destinataire doit être saisi", Toast.LENGTH_SHORT).show()
        if(amount == null) Toast.makeText(this, "Montant doit être saisi", Toast.LENGTH_SHORT).show()
        return recipient != null && amount != null
    }

}
