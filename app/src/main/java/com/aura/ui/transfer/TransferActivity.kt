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

        recipient.addTextChangedListener(textWatcher)
        amount.addTextChangedListener(textWatcher)

        transfer.setOnClickListener {
            viewModel.getAuraTransfer(
                currentId,
                recipient.text.toString(),
                amount.text.toString().toDouble()
            )
        }

        lifecycleScope.launch {

            viewModel.uiState.collect {
                loading.isVisible = it.isViewLoading == true
                transfer.isEnabled = it.transferred == true || it.isDataReady == true

                if (it.transferred == true){
                    homeLoader()
                    toastMessage(getString(R.string.transfer_success))
                }

                if (it.transferred == false){
                    viewModel.reset()
                    toastMessage(getString(R.string.transfer_failed))
                }

                if (it.errorMessage?.isNotBlank() == true)
                    toastMessage(it.errorMessage)
            }
        }

    }

    private fun homeLoader() {
        startActivity(Intent(this, HomeActivity::class.java)
            .apply { putExtra("currentId", currentId) })
        finish()
    }

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

    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
