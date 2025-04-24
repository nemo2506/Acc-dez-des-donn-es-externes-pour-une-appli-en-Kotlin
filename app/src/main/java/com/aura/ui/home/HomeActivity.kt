package com.aura.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.ConstantsApp
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The main screen shown after a successful login, displaying the user's account balance
 * and allowing access to transfer operations.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    /**
     * View binding instance for accessing layout views.
     */
    private lateinit var binding: ActivityHomeBinding

    /**
     * ViewModel associated with this activity.
     */
    private val viewModel: HomeActivityViewModel by viewModels()

    /**
     * Callback to handle results from [TransferActivity].
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            // TODO: Handle transfer result if needed
        }

    /**
     * Called when the activity is starting. Initializes the UI and observers.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val balance = binding.balance
        val loading = binding.loading
        val transfer = binding.transfer
        val retry = binding.retry

        // Trigger balance fetch initially and on retry button click
        viewModel.getAuraBalance()
        retry.setOnClickListener {
            viewModel.getAuraBalance()
        }

        // Launch transfer screen when the transfer button is clicked
        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(this, TransferActivity::class.java)
                    .putExtra(ConstantsApp.CURRENT_ID, viewModel.currentId)
            )
        }

        // Observe ViewModel state updates using lifecycle-aware coroutine scope
        lifecycleScope.launch {
            viewModel.uiState.collect {

                // Control UI loading and retry states
                loading.isVisible = it.isViewLoading == true
                retry.isVisible = it.balance == null

                // Show balance if available
                if (it.isBalanceReady == true) {
                    balance.text = "%.2f€".format(it.balance)
                    toastMessage(getString(R.string.balance_success))
                }

                // Reset state if balance fetch fails
                if (it.isBalanceReady == false) {
                    viewModel.reset()
                    toastMessage(getString(R.string.balance_failed))
                }
            }
        }
    }

    /**
     * Inflates the menu defined in `home_menu.xml`.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    /**
     * Handles menu item selections.
     *
     * @param item The selected menu item.
     * @return True if the action was handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.disconnect -> {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows a short toast message on screen.
     *
     * @param message The message to display.
     */
    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
