package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityTransferBinding

/**
 * The transfer activity for the app.
 */
class TransferActivity : AppCompatActivity()
{

  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding
  private val viewModel: TransferActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
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
  }

}
