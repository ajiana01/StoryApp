package com.ajiananta.submisiintermediate.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.databinding.ActivitySignUpBinding
import com.ajiananta.submisiintermediate.view.ViewModelFactory
import com.ajiananta.submisiintermediate.view.welcome.WelcomeActivity

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: SignUpViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showSuccessDialog() {
        val build = AlertDialog.Builder(this)
        build.setTitle(getString(R.string.great))
        build.setMessage(getString(R.string.acc_success))
        val alertDialog: AlertDialog = build.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun showFailDialog() {
        val build = AlertDialog.Builder(this)
        build.setTitle(getString(R.string.bruh))
        build.setMessage(getString(R.string.acc_fail_create))
        val alertDialog: AlertDialog = build.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            alertDialog.dismiss()
        }, 2000)
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val pass = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> binding.edRegisterName.error = getString(R.string.name_not_blank)
                email.isEmpty() -> binding.edRegisterEmail.error = getString(R.string.email_not_blank)
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.edRegisterEmail.error = getString(R.string.email_not_valid)
                pass.isEmpty() -> binding.edRegisterPassword.error = getString(R.string.password_not_blank)
                pass.length < 8 -> binding.edRegisterPassword.error = getString(R.string.password_must_8)
                else -> {
                    viewModel.register(name, email, pass).observe(this) { userResult ->
                        if (userResult.message == "201") {
                            showSuccessDialog()
                        }
                        if (userResult.message == "400") {
                            showFailDialog()
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

    private fun showLoading(isLoad: Boolean) {
        binding.loadingLayout.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}