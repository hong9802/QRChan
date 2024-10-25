package com.bread_cat.QRchan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bread_cat.QRchan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var qrCodeScan = QRCodeScan(this)

        binding.qrScanButton.setOnClickListener {
            qrCodeScan.startQRscan()
        }
        binding.qrGalleryScanButton.setOnClickListener() {
            qrCodeScan.pickImageFromGallery()
        }
    }
}