package com.cdoan.dogs.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.cdoan.dogs.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, null)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun checkSmsPermission() {
        val permission = Manifest.permission.SEND_SMS

        if (isPermissionGranted(permission)) {
            notifyDetailFragment(true)
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this)
                .setTitle("Send SMS permission")
                .setMessage("This app requires access to send an SMS.")
                .setPositiveButton("Ask me") { _, _ ->
                    requestPermission(permission, PERMISSION_SEND_SMS)
                }
                .setNegativeButton("No") { _, _ ->
                    notifyDetailFragment(false)
                }
                .show()
            return
        }

        requestPermission(permission, PERMISSION_SEND_SMS)
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_SEND_SMS) {
            notifyDetailFragment(PackageManager.PERMISSION_GRANTED in grantResults)
        }
    }

    private fun notifyDetailFragment(smsPermissionGranted: Boolean) {
        val activeFragment = this.navHostFragment.childFragmentManager.primaryNavigationFragment
        if (activeFragment is DetailFragment) {
            activeFragment.onSmsPermissionResult(smsPermissionGranted)
        }
    }

    companion object {
        const val PERMISSION_SEND_SMS = 123
    }

}