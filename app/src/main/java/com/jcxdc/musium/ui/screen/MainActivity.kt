package com.jcxdc.musium.ui.screen

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.ActivityMainBinding
import com.jcxdc.musium.service.MusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var bottomViewNavigationListener: BottomViewNavigationListener? = null


    var musicService: MusicService? = null
    private val REQUEST_CODE_NOTIFICATIONS = 1001
    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, you can proceed with notifications
        } else {
            // Show a message to the user that permission is needed
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted, proceed with notifications
                }
                else -> {
                    // Request the POST_NOTIFICATIONS permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, request READ_MEDIA_AUDIO permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            // For Android 12 and below, request READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()
        checkStoragePermission() // Check storage permission

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        togglePlayPause()
        binding.rlBottomView.visibility = View.GONE
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        handleBottomView()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.libraryFragment -> {
                    navController.navigate(R.id.libraryFragment)
                }

                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.playlistFragment ->{
                    navController.navigate(R.id.playlistFragment)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.splashFragment -> hideBottomNavigation()
                R.id.playerFragment ->hideBottomView()
                
                else -> showBottomNavigation()

            }
        }
    }
    fun setBottomViewNavigationListener(listener: BottomViewNavigationListener?) {
        bottomViewNavigationListener = listener
    }
    private fun handleBottomView() {
        binding.rlBottomView.setOnClickListener {
            bottomViewNavigationListener?.navigateToPlayer()
        }
        binding.ivCloseBottomView.setOnClickListener{
            binding.rlBottomView.visibility = View.GONE
            musicService?.stopTrack()
            musicService?.stopMusicService()
        }
        binding.ivPlay.setOnClickListener {
            togglePlayPause()
        }
    }
    private fun togglePlayPause() {
        if (musicService?.isPlaying() == true) {
            musicService?.pauseTrack()
            binding.ivPlay.setImageResource(R.drawable.play)
        } else {
            musicService?.resumeTrack()
            binding.ivPlay.setImageResource(R.drawable.pause)
        }
    }

    fun updateBottomViewTitle(title: String,duration:String) {

        binding.bottomViewTitle.text = title
        binding.bottomViewDuration.text = duration
    }

    fun hideBottomView(){
        binding.rlBottomView.visibility = View.GONE
    }
    fun showBottomView(){
        binding.rlBottomView.visibility = View.VISIBLE
    }


    private fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
    }
    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        musicService = null
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can now post notifications
            } else {
                // Permission denied, handle the denial (optional)
            }
        }
    }
}
