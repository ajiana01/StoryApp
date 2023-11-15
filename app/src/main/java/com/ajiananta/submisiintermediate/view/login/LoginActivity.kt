package com.ajiananta.submisiintermediate.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.databinding.ActivityLoginBinding
import com.ajiananta.submisiintermediate.view.ViewModelFactory
import com.ajiananta.submisiintermediate.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()
        setupAction()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.input_your_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.input_password)
                }
                else -> {
                    loginViewModel.login(email, password).observe(this) { userResult ->
                        val user = userResult.loginResult
                        if (user != null) {
                            loginViewModel.saveSession(
                                user.name,
                                user.userId,
                                user.token
                            )
                            if (userResult.error)
                            {
                                if (userResult.message == "400") {
                                    showAlertDialog(getString(R.string.bruh), getString(R.string.login_fail))
                                }
                                if (userResult.message == "401") showAlertDialog(getString(R.string.hm), getString(R.string.login_fail_user_not_found))
                            } else {
                                val build = AlertDialog.Builder(this)
                                build.setTitle(getString(R.string.great))
                                build.setMessage(getString(R.string.login_success))
                                val alertDialog: AlertDialog = build.create()
                                alertDialog.setCancelable(false)
                                alertDialog.show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    alertDialog.dismiss()
                                    val intent = Intent(this, MainActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    }
                                    startActivity(intent)
                                    finish()
                                }, 2000)
                            }
                        }
                        if (userResult.message == "") {
                            showLoading(true)
                        } else {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val viewsToAnimate = arrayOf(
            binding.titleTextView,
            binding.messageTextView,
            binding.emailTextView,
            binding.emailEditTextLayout,
            binding.passwordTextView,
            binding.passwordEditTextLayout,
            binding.loginButton
        )

        viewsToAnimate.forEach { view ->
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
                duration = 100
                startDelay = 100
            }.start()
        }

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.visibility = View.VISIBLE
        } else {
            binding.loadingLayout.visibility = View.GONE
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
        }.create()
        alertDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }
}