package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity

/**
 * The login activity for the app.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * The binding for the login layout.
     */
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading

        login.setOnClickListener {
            loading.visibility = View.VISIBLE

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)

            finish()
        }

        // Add listeners to update login button visibility
        binding.identifier.doAfterTextChanged { isLoginReady() }
        binding.password.doAfterTextChanged { isLoginReady() }
    }

    private fun isLoginReady(): Unit {
        val isIdentifierReady: Boolean = binding.identifier.text.toString().isNotEmpty()
        val isPasswordReady: Boolean = binding.password.text.toString().isNotEmpty()
        binding.login.visibility =
            if (isIdentifierReady && isPasswordReady) View.VISIBLE else View.INVISIBLE
    }
}
