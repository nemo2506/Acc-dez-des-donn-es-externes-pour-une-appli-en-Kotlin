package com.aura.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    /**
     * The binding for the home layout.
     */
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityViewModel by viewModels()
    private lateinit var currentId: String

    /**
     * A callback for the result of starting the TransferActivity.
     */
    private val startTransferActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //TODO
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentId = intent.getStringExtra("currentId").toString()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val balance = binding.balance
        val loading = binding.loading
        val transfer = binding.transfer
        val retry = binding.retry

        /**
         * Get Balance by loading or by clicking
         */
        viewModel.getAuraBalance(currentId)
        retry.setOnClickListener() {
            viewModel.getAuraBalance(currentId)
        }

        /**
         * When button Transfer is clicked TransferActivity is loading
         */
        transfer.setOnClickListener {
            startTransferActivityForResult.launch(
                Intent(this, TransferActivity::class.java)
                    .putExtra("currentId", currentId)
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
                retry.isVisible = it.isBalanceReady == false

                /**
                 * At step balance ready it's displaying to screen
                 */
                if (it.isBalanceReady == true) {
                    balance.text = "%.2f€".format(it.balance)
                    toastMessage(getString(R.string.balance_success))
                }

                /**
                 * At step not balance stateflow is reset
                 */
                if (it.isBalanceReady == false) {
                    viewModel.reset()
                    toastMessage(getString(R.string.balance_failed))
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

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
     * Simplify methode to screen message
     */
    private fun toastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
