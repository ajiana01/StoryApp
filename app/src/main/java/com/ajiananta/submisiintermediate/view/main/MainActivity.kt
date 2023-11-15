package com.ajiananta.submisiintermediate.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajiananta.submisiintermediate.R
import com.ajiananta.submisiintermediate.adapter.LoadAdapter
import com.ajiananta.submisiintermediate.databinding.ActivityMainBinding
import com.ajiananta.submisiintermediate.view.ViewModelFactory
import com.ajiananta.submisiintermediate.view.login.LoginViewModel
import com.ajiananta.submisiintermediate.view.maps.MapsActivity
import com.ajiananta.submisiintermediate.view.upload.UploadStoryActivity
import com.ajiananta.submisiintermediate.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.truestory_app)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = StoriesAdapter()
        showLoad(true)

        loginViewModel.getSession().observe(this) { user ->
            if (user.userId.isEmpty()) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                binding.rvListStories.adapter = adapter.withLoadStateFooter(
                    footer = LoadAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.getAllStories(user.token).observe(this) {
                    Log.e("List Stories", it.toString())
                    adapter.submitData(lifecycle, it)
                    showLoad(false)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvListStories.layoutManager = layoutManager

        binding.addStoryButton.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps_access -> {
                val intentMap = Intent(this, MapsActivity::class.java)
                startActivity(intentMap)
            }
            R.id.setting_language -> {
                val intentLang = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intentLang)
            }
            R.id.action_logout -> {
                loginViewModel.logout()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoad(isLoading: Boolean) {
        binding.loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}